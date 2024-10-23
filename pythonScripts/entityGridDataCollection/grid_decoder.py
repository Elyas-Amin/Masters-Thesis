"""
Algorithm for generative entity grid. Takes the transitions from training.
Created on 25 Nov 2014

@author: Karin Sim
"""
import sys
import argparse
import logging
import numpy as np
import os
from util import pairwise, smart_open
from grid import read_grids, r2i
from multiprocessing import Pool
from functools import partial    

def read_unigrams(istream, str2int):
    U = np.zeros(len(str2int), int)
    for line in istream:
        if line.startswith('#'):
            continue
        line = line.strip()
        if not line:
            continue
        role, count = line.split('\t')
        U[str2int[role]] = int(count)
    return U


def read_bigrams(istream, str2int):
    B = np.zeros((len(str2int), len(str2int)), int)
    for line in istream:
        if line.startswith('#'):
            continue
        line = line.strip()
        if not line:
            continue
        r1, r2, count = line.split('\t')
        B[str2int[r1], str2int[r2]] = int(count)
    return B


def grid_loglikelihood(grid, U, B, salience=0):
    logprob = np.sum([
        np.sum(
            [np.log(np.divide(float(B[ri, rj]), U[ri])) for ri, rj in pairwise(entity_roles) if U[ri] > 0])
        for entity_roles in grid.transpose()])
    
    return logprob / grid.size


def loglikelihood(corpus, U, B, salience):
    return np.array([grid_loglikelihood(grid, U, B, salience) for grid in corpus])


def wrapped_loglikelihood(corpus, U, B, salience):
    try:
        return loglikelihood(corpus, U, B, salience)
    except Exception:
        logging.exception("Error occurred during loglikelihood computation")
        raise


def decode_many(unigrams, bigrams, salience, input_dir, output_path, jobs, estream=sys.stderr):
    # Reads in the model
    logging.info('Reading unigrams from: %s', unigrams)
    U = read_unigrams(smart_open(unigrams, encoding='latin-1'), r2i)
    logging.info('Read in %d unigrams', U.size)

    logging.info('Reading bigrams from: %s', bigrams)
    B = read_bigrams(smart_open(bigrams, encoding='latin-1'), r2i)
    logging.info('Read in %d bigrams', B.size)

    # Get list of all input files from the directory
    input_files = [os.path.join(input_dir, f) for f in os.listdir(input_dir) if os.path.isfile(os.path.join(input_dir, f))]
    logging.info('Found %d input files in directory: %s', len(input_files), input_dir)

    tests = [None] * len(input_files)
    for i, input_file in enumerate(input_files):
        documents = read_grids(smart_open(input_file), r2i)
        logging.info('%s: %d test documents read', input_file, len(documents))
        tests[i] = documents

    # Computes the log likelihood of each document in each test file
    pool = Pool(jobs)
    all_L = pool.map(partial(wrapped_loglikelihood, U=U, B=B, salience=salience), tests)
    print("done")

    # Construct a file path within the testing output directory
    output_file = os.path.join(output_path, 'results.txt')

    # Write results to the output file
    with smart_open(output_file, 'w', encoding='utf-8') as ostream:
        print('#file\t#sum\t#mean', file=ostream)
        for input_file, test, L in zip(input_files, tests, all_L):
            print(f'# Processing file: {input_file}', file=ostream)
            print('#doc\t#logprob\t#sentences\t#entities', file=ostream)
            for i, ll in enumerate(L):
                num_sentences = test[i].shape[0]
                num_entities = test[i].shape[1]
                print(f'{i}\t{ll}\t{num_sentences}\t{num_entities}', file=ostream)
            print(f'{input_file}\t{L.sum()}\t{np.mean(L)}', file=ostream)



def main(args):
    """Load data and compute the coherence for all files in the input directory"""
    logging.basicConfig(
        level=(logging.DEBUG if args.verbose else logging.INFO),
        format='%(levelname)s: %(message)s'
    )
    
    decode_many(args.unigrams, args.bigrams, args.salience, args.input_dir, args.output, args.jobs)


def argparser(parser=None, func=main):
    """Parse command line arguments"""

    if parser is None:
        parser = argparse.ArgumentParser(prog='grid_decoder')

    parser.description = 'Generative implementation of Entity grid'
    parser.formatter_class = argparse.ArgumentDefaultsHelpFormatter

    parser.add_argument('unigrams',
                        type=argparse.FileType('r'),
                        help="Path for unigram file")
    parser.add_argument('bigrams',
                        type=argparse.FileType('r'),
                        help="Path for bigram file")
    parser.add_argument('input_dir',
                        type=str,
                        help='Directory containing input corpus files in doctext format')
    parser.add_argument('output',
                        type=str,
                        help='Path to output file where results will be written')
    parser.add_argument('--salience', default=0,
                        type=int,
                        help='Salience variable for entities')
    parser.add_argument('--jobs', default=1,
                        type=int,
                        help='Number of parallel jobs to run')
    parser.add_argument('--verbose', '-v',
                        action='store_true',
                        help='Increase the verbosity level')

    if func is not None:
        parser.set_defaults(func=func)

    return parser


if __name__ == '__main__':
    main(argparser().parse_args())
