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
from discourse.util import pairwise, bar
from discourse.doctext import iterdoctext
from discourse import command
import functools

# TODO: generalise vocabulary of roles
r2i = {'S': 3, 'O': 2, 'X': 1, '-': 0}
i2r = {v: k for k, v in r2i.items()}


def read_grids(istream, str2int):
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


def main(args):
    """Load grids and extract unigrams and bigrams"""

    logging.basicConfig(
        level=(logging.DEBUG if args.verbose else logging.INFO),
        format='%(levelname)s: %(message)s'
    )

    training = read_grids(args.input, r2i)
    logging.info('Training set contains %d docs', len(training))
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


@command('grid', 'entity-based')
def argparser(parser=None, func=main):
    """Parse command line arguments"""

    if parser is None:
        parser = argparse.ArgumentParser(prog='grid')

    parser.description = 'Generative implementation of Entity grid'
    parser.formatter_class = argparse.ArgumentDefaultsHelpFormatter

    parser.add_argument('input', nargs='?', 
                        type=argparse.FileType('r'), default=sys.stdin,
                        help='Input corpus in doctext format')

    parser.add_argument('output', 
                        type=str,
                        help="Prefix for model files")

    parser.add_argument('--salience', default=0,
                        type=int,
                        help='Salience variable for entities')

    parser.add_argument('--verbose', '-v',
                        action='store_true',
                        help='Increase the verbosity level')

    if func is not None:
        parser.set_defaults(func=func)

    return parser


if __name__ == '__main__':
    main(argparser().parse_args())
