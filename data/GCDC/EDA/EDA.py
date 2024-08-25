import pandas as pd
import ssl
import nltk
ssl._create_default_https_context = ssl._create_unverified_context
nltk.download('punkt_tab')
from nltk.tokenize import word_tokenize


class EDA:
    def __init__(self, path_to_GCDC_data):
        self.df = pd.read_parquet(path_to_GCDC_data)
    
    def total_token_count(self, df, text_column):
        total_tokens = df[text_column].apply(lambda x: len(nltk.word_tokenize(x))).sum()
        return total_tokens
    
def main():
    path_to_GCDC_parquet = "/Users/Ghamay/Documents/Masters-Thesis/Data/GCDC/Data/GCDCParquet/GCDC.parquet"
    text_column_name = "text"
    eda = EDA(path_to_GCDC_parquet)
    total_tokens = eda.total_token_count(eda.df, text_column_name)
    print(f"Total number of tokens in the dataset: {total_tokens}")
    print()

if __name__ == "__main__":
    main()
    