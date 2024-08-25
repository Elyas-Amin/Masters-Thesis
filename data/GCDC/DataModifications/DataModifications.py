import pandas as pd
import os
import glob
import json

class DataModifications:
    def __init__(self, pathToTextFolder):
        self.pathToTextFolder = pathToTextFolder
    
    def combineJSONLTextFilesToParquet(self):
        jsonl_files = glob.glob(os.path.join(self.pathToTextFolder, '*.jsonl'))
        print(f"Found {len(jsonl_files)} files")

        if not jsonl_files:
            raise ValueError("No .jsonl files found in the specified directory.")
        dataframes = []
        # Loop over each file and read it into a DataFrame
        for file in jsonl_files:
            with open(file, 'r') as f:
                data = [json.loads(line) for line in f]
                if data:  # Check if data is not empty
                    df = pd.DataFrame(data)
                    # Ensure text_id is treated as a string
                    df['text_id'] = df['text_id'].astype(str)
                    dataframes.append(df)
        
        combined_df = pd.concat(dataframes, ignore_index=True)
        
        combined_df.to_parquet("/Users/Ghamay/Documents/Masters-Thesis/Data/GCDC/Data/GCDCParquet/GCDC.parquet")
        
def main():
    #To extract and combine data into one parquet file run 'CombineJSONLFilesToParquet.py' file
    #To remove 'facts' column from parquet file run 'DeleteFactsColumn.py' file
    
    #print columns and whole df file
    df = pd.read_parquet("/Users/Ghamay/Documents/Masters-Thesis/Data/GCDC/Data/GCDCParquet/GCDC.parquet")
    print(df.columns)
    print(df)
    
    
if __name__ == "__main__":
    main()
    
        


    

