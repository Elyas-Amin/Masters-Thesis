"""

Input format (ignore the header and the first column, they are there just for illustration)

    e1 e2 e3
s1  S  -  O
s2  -  S  -
s3  S  O  X

@author: wilkeraziz
"""

import argparse
import numpy as np
import sys
import logging
import itertools
from util import pairwise, bar, iterdoctext
import functools
import os

# TODO: generalise vocabulary of roles
r2i = {'S': 3, 'O': 2, 'X': 1, '-': 0}
i2r = {v: k for k, v in r2i.items()}


def read_grids(istream, str2int):
    """
    Reads the grids from the input stream using iterdoctext and converts them using str2int mapping.
    :param istream: input stream to read from
    :param str2int: a dictionary mapping string roles to integers
    :return: a list of numpy arrays representing the grids
    """
    return [np.array([[str2int[role] for role in line] for line in lines], int) for lines, attrs in iterdoctext(istream)]


def train(corpus, vocab_size, salience):
    U = np.zeros(vocab_size, int)
    B = np.zeros((vocab_size, vocab_size), int)
    for grid in bar(corpus, msg='Counting role transitions'):
        for entity_roles in grid.transpose():
            if get_number_of_occurrences(entity_roles) >= salience:
                for r in entity_roles:
                    U[r] += 1
                for ri, rj in pairwise(entity_roles):
                    B[ri, rj] += 1
    return U, B


def get_number_of_occurrences(entity_roles):
    # the number of roles (not null) recorded for the entity
    return sum(r != r2i['-'] for r in entity_roles)


def get_role_count(entity_roles):
    from collections import Counter
    roles = Counter(entity_roles)
    temp = functools.reduce(lambda x, y: x + y, Counter(entity_roles).values())
    return temp - roles[0]


def process_folder(input_folder, str2int):
    """
    Iterates over all files in the input folder, applies the read_grids function to each file, 
    and returns the results for processing in the main function.
    """
    grids = []
    for filename in os.listdir(input_folder):
        input_file_path = os.path.join(input_folder, filename)
        if os.path.isfile(input_file_path):
            # logging.info(f'Processing file: {filename}')
            with open(input_file_path, 'r') as f:
                file_grids = read_grids(f, str2int)
                grids.extend(file_grids)
    return grids


def save_grids(output_folder, filename, grids):
    """
    Save the grids to the specified output folder.
    
    :param output_folder: path to the folder where output files will be saved
    :param filename: the filename to save the grids as
    :param grids: the grids (numpy arrays) to save
    """
    os.makedirs(output_folder, exist_ok=True)
    output_file_path = os.path.join(output_folder, f'processed_{filename}.npy')
    logging.info(f'Saving processed grids to: {output_file_path}')
    np.save(output_file_path, grids)

def main(args):
    """Load grids and extract unigrams and bigrams"""

    logging.basicConfig(
        level=(logging.DEBUG if args.verbose else logging.INFO),
        format='%(levelname)s: %(message)s'
    )

    # Process the folder and get the training grids
    training = process_folder(args.input, r2i)
    
    # Check that training grids are not empty
    if not training:
        logging.error("No training grids were found or processed.")
        sys.exit(1)
    
    logging.info('Training set contains %d docs', len(training))

    # Train and extract unigrams and bigrams
    unigrams, bigrams = train(training, len(r2i), args.salience)
    logging.info('%d unigrams and %d bigrams', unigrams.size, bigrams.size)

    # Save unigrams and bigrams in a human-readable format
    with open(f'{args.output}.unigrams', 'w') as fu:
        fu.write('#role\t#count\n')
        for rid, count in enumerate(unigrams):
            fu.write(f'{i2r[rid]}\t{count}\n')

    with open(f'{args.output}.bigrams', 'w') as fb:
        fb.write('#role\t#role\t#count\n')
        for r1, r2 in itertools.product(range(len(r2i)), range(len(r2i))):
            fb.write(f'{i2r[r1]}\t{i2r[r2]}\t{bigrams[r1, r2]}\n')


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Process grids, extract unigrams and bigrams, and save results')
    parser.add_argument('--input', required=True, help='Input folder containing grid files (directory path)')
    parser.add_argument('--output', required=True, help='Output base name for unigrams and bigrams')
    parser.add_argument('--salience', type=int, default=1, help='Salience threshold for counting role occurrences')
    parser.add_argument('--verbose', '-v', action='store_true', help='Increase output verbosity')

    args = parser.parse_args()
    main(args)
