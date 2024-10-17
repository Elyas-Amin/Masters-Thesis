"""

Algorithm for generative entity grid. Takes the transitions from training.
Created on 25 Nov 2014

@author: Karin Sim
"""
import sys
import argparse
import logging
import numpy as np
import itertools
from util import pairwise, smart_open
from grid import read_grids, r2i, i2r
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


def decode_many(unigrams, bigrams, salience, ipaths, opaths, jobs, estream=sys.stderr):
    # Reads in the model
    logging.info('Reading unigrams from: %s', unigrams)
    U = read_unigrams(smart_open(unigrams), r2i)
    logging.info('Read in %d unigrams', U.size)

    logging.info('Reading bigrams from: %s', bigrams)
    B = read_bigrams(smart_open(bigrams), r2i)
    logging.info('Read in %d bigrams', B.size)

    tests = [None] * len(ipaths)
    for i, ipath in enumerate(ipaths):
        documents = read_grids(smart_open(ipath), r2i)
        logging.info('%s: %d test documents read', ipath, len(documents))
        tests[i] = documents

    # Computes the log likelihood of each document in each test file
    pool = Pool(jobs)
    all_L = pool.map(partial(wrapped_loglikelihood, U=U, B=B, salience=salience), tests)

    print('#file\t#sum\t#mean', file=estream)
    for ipath, opath, test, L in zip(ipaths, opaths, tests, all_L):
        with smart_open(opath, 'w') as ostream:
            print('#doc\t#logprob\t#sentences\t#entities', file=ostream)
            for i, ll in enumerate(L):
                num_sentences = test[i].shape[0]
                num_entities = test[i].shape[1]
                print(f'{i}\t{ll}\t{num_sentences}\t{num_entities}', file=ostream)
        print(f'{opath}\t{L.sum()}\t{np.mean(L)}', file=estream)


def main(args):
    """Load data and compute the coherence"""
    logging.basicConfig(
        level=(logging.DEBUG if args.verbose else logging.INFO),
        format='%(levelname)s: %(message)s'
    )
    U = read_unigrams(args.unigrams, r2i)
    logging.info('Read in %d unigrams', U.size)
    B = read_bigrams(args.bigrams, r2i)
    logging.info('Read in %d bigrams', B.size)
    test = read_grids(args.input, r2i)
    logging.info('Scoring %d documents', len(test))
    
    print('#docid\t#loglikelihood', file=args.output)
    for i, grid in enumerate(test):
        ll = grid_loglikelihood(grid, U, B, args.salience)
        print(f'{i}\t{ll}', file=args.output)


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
    parser.add_argument('input', nargs='?',
                        type=argparse.FileType('r'), default=sys.stdin,
                        help='Input corpus in doctext format')
    parser.add_argument('output', nargs='?',
                        type=argparse.FileType('w'), default=sys.stdout,
                        help='Output probabilities')
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
