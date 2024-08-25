import sys
import os

# Add the parent directory to sys.path
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '..')))

from DataModifications import DataModifications

if __name__ == "__main__":
    #Define the path to the folder containing the JSONL files
    path_to_data_text = "/Users/Ghamay/Documents/Masters-Thesis/Data/GCDC/Data/JSONLFiles"
    
    #Create an instance of the DataExtraction class
    data_extraction = DataModifications(path_to_data_text)
    
    #Combine the JSONL files into a single Parquet file
    data_extraction.combineJSONLTextFilesToParquet()
    