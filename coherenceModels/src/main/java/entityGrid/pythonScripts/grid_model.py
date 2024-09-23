'''
Created on 12 Jan 2015
 
 Constructs an entity grid from a given file containing ptb trees. The file may be English, French or German.
 The entity grid uses the Stanford Parser to identify all nouns in the input text.
 For the English version it additionally determines the grammatical role played by that entity in each particular occurance. 
 The various options are set on the commandline, to ensure correct parser is set.
 
 @author Karin Sim

'''
import argparse
import sys
import traceback
import logging
import gzip
from grid import r2i, i2r
import StanfordDependencies
import numpy as np
from discourse.doctext import iterdoctext, writedoctext
from discourse.util import smart_open, read_documents
from collections import defaultdict

nouns = ['NNP', 'NP','NNS','NN','N','NE']

''' csubj,  
    csubjpass, {xsubj}: controlling subject}, 
    subj,  
    nsubj (nominal subject), 
    nsubjpass
    '''
subject =[ 'csubj', 'csubjpass','subj','nsubj','nsubjpass']

''' pobj (object of a preposition) 
    also dobj ( direct object) 
    and iobj ( indirect object )'''
object= ["pobj","dobj","iobj"] 



def open_file(path):
    if path.endswith('.gz'):
        return gzip.open(path)
    else:
        return open(path)
        
# input is in form of ptb trees. 
def main(args):
    """ Extract entities and construct grid """
    try:
        with open(args.directory, 'r') as fi:
            with open(f"{args.directory}_grid", 'w') as fo:
                entities, sentences = extract_grids(fi)
                grid = construct_grid(entities, sentences)
                output_grid(grid, fo)
            logging.info('done: %s', args.directory)
    except Exception as e:
        logging.exception('Error occurred: %s', str(e))  
            
def extract_grids(fi):
    """ Identify entities from ptb trees for document. Store in dictionary for grid construction. """
    idx = 0
    entities = defaultdict(lambda: defaultdict(dict))
    for lines, attrs in iterdoctext(fi):
        logging.debug('document %s', attrs['docid'])
        logging.info('Extracting %d lines', len(lines))
        for line in lines:
            entities, idx = convert_tree(line, entities, idx)
    return entities, idx


def convert_tree(line, entities, idx):
    logging.info('Converting tree: %s', line)
    sd = StanfordDependencies.get_instance(
        jar_filename=r'C:\SMT\StanfordNLP\stanford-corenlp-full-2013-11-12\stanford-corenlp-3.3.0.jar',
        backend='subprocess'
    )

    dependencies = sd.convert_tree(line, debug=True)

    for token in dependencies:
        logging.info('Processing token: %s', token)
        if token.pos in nouns:
            grammatical_role = 'X'
            if token.deprel in subject:
                grammatical_role = 'S'
            elif token.deprel in object:
                grammatical_role = 'O'

            if token.lemma in entities and idx in entities[token.lemma]:
                if entities[token.lemma][idx] < r2i[grammatical_role]:
                    entities[token.lemma][idx] = r2i[grammatical_role]
            else:
                entities[token.lemma][idx] = r2i[grammatical_role]
    idx += 1
    return entities, idx


def construct_grid(entities, sentences):
    """ Construct grid from dictionary, rows are sentences, cols are entities """
    logging.info('Constructing grid. Entity count: %d', len(entities))
    grid = np.zeros((sentences, len(entities)))
    entity_idx = 0

    for entity in entities.keys():
        occurrences = entities[entity]
        for sentence in occurrences:
            grid[sentence][entity_idx] = occurrences[sentence]
        entity_idx += 1

    return grid


def output_grid(grids, ostream):
    """ Output grid """
    for grid in grids:
        for i, row in enumerate(grid):
            for j, val in enumerate(row):
                if val == 0:
                    print('-', end='', file=ostream)
                else:
                    print(val, end='', file=ostream)
            print('\n', end='', file=ostream)
        print('\n', file=ostream)


def parse_args():
    """ Parse command line arguments """
    parser = argparse.ArgumentParser(description='Implementation of Entity grid using ptb trees as input',
                                     formatter_class=argparse.ArgumentDefaultsHelpFormatter)

    parser.add_argument('directory', type=str, help="Path for input file")
    parser.add_argument('--verbose', '-v', action='store_true', help='Increase verbosity level')

    args = parser.parse_args()

    logging.basicConfig(
        level=(logging.DEBUG if args.verbose else logging.INFO),
        format='%(levelname)s: %(message)s'
    )
    return args


if __name__ == '__main__':
    main(parse_args())