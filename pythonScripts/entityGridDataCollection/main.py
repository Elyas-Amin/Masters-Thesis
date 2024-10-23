import argparse
import logging
import os
import sys
import itertools
from grid_decoder import decode_many
from grid import process_folder, train

# Mapping roles to integers and vice versa
r2i = {'S': 3, 'O': 2, 'X': 1, '-': 0}
i2r = {v: k for k, v in r2i.items()}

def main(args):
    """Training"""
    logging.basicConfig(
        level=(logging.DEBUG if args.verbose else logging.INFO),
        format='%(levelname)s: %(message)s'
    )

    # Process the JSONL file and get the training grids
    training = process_folder(args.input_train, r2i)
    
    # Check that training grids are not empty
    if not training:
        logging.error("No training grids were found or processed.")
        sys.exit(1)
    
    logging.info('Training set contains %d docs', len(training))

    # Train and extract unigrams and bigrams
    unigrams, bigrams = train(training, len(r2i), args.salience)
    logging.info('%d unigrams and %d bigrams', unigrams.size, bigrams.size)

    # Construct the path for the training and testing output directories
    training_output_dir = os.path.join(os.path.dirname(args.input_train), "../clinton", f"{os.path.splitext(os.path.basename(args.input_train))[0]}_training_data")
    testing_output_dir = os.path.join(os.path.dirname(args.input_train), "../clinton", f"{os.path.splitext(os.path.basename(args.input_train))[0]}_testing_data")

    # Create the directories if they do not exist
    os.makedirs(training_output_dir, exist_ok=True)
    os.makedirs(testing_output_dir, exist_ok=True)

    train_unigram_file = f'{training_output_dir}/unigrams.txt'
    train_bigram_file = f'{training_output_dir}/bigrams.txt'

    # Save unigrams and bigrams in a human-readable format
    with open(train_unigram_file, 'w') as fu:
        fu.write('#role\t#count\n')
        for rid, count in enumerate(unigrams):
            fu.write(f'{i2r[rid]}\t{count}\n')

    with open(train_bigram_file, 'w') as fb:
        fb.write('#role\t#role\t#count\n')
        for r1, r2 in itertools.product(range(len(r2i)), range(len(r2i))):
            fb.write(f'{i2r[r1]}\t{i2r[r2]}\t{bigrams[r1, r2]}\n')
            
    print("Done Testing")

    """Testing"""
    decode_many(train_unigram_file, train_bigram_file, args.salience, args.input_test, testing_output_dir, args.jobs)

def argparser():
    parser = argparse.ArgumentParser(prog='main')

    parser.description = 'Generative implementation of Entity Grid and Entity Graph'
    parser.formatter_class = argparse.ArgumentDefaultsHelpFormatter

    parser.add_argument('--input_train',
                        type=str,
                        required=True,
                        help='Directory containing training grids.')
    parser.add_argument('--input_test',
                        type=str,
                        required=True,
                        help='Path to testing grids directory')
    parser.add_argument('--salience', default=0,
                        type=int,
                        help='Salience variable for entities')
    parser.add_argument('--jobs', default=1,
                        type=int,
                        help='Number of parallel jobs to run')
    parser.add_argument('--verbose', '-v',
                        action='store_true',
                        help='Increase the verbosity level')

    return parser

if __name__ == '__main__':
    parser = argparser()  # Initialize argument parser
    args = parser.parse_args()  # Parse command-line arguments
    main(args)  # Call the main function with parsed arguments
