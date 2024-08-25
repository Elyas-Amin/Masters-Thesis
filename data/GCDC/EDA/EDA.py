import pandas as pd
import ssl
import matplotlib.pyplot as plt
import nltk
ssl._create_default_https_context = ssl._create_unverified_context
nltk.download('punkt_tab')
from nltk.tokenize import word_tokenize, sent_tokenize


class EDA:
    def total_token_count(self, df, column_name):
        total_tokens = df[column_name].apply(lambda x: len(nltk.word_tokenize(x))).sum()
        return total_tokens
    
    def coherence_frequency(self, df, column_name):
        frequency = df[column_name].value_counts()

        low = frequency.get(1, 0)
        moderate = frequency.get(2, 0)
        high = frequency.get(3, 0)

        print(f"Low coherence (1) frequency: {low}")
        print(f"Moderate coherence (2) frequency: {moderate}")
        print(f"High coherence (3) frequency: {high}")
        
        return {"low": low, "moderate": moderate, "high": high}
    
    def average_sentence_length(self, df):
        df["number_of_sentences"] = df["sentences"].apply(lambda x: len(sent_tokenize(x)))
        print("done")
        df["sentence_length"] = df["number_of_sentences"].apply(lambda x: [len(word_tokenize(x)) for sentence in x])
        df["average_sentence_length"] = df["sentence_length"]/df["number_of_sentences"]
        
        print(df["number_of_sentences"])

    
    def average_sentence_length_distribution(self, df, column_name):
        df['average_sentence_length'] = df[column_name].apply(self.average_sentence_length)
        return df
    
    def llm_cost_count(self):
        return
    
def main():
    #variables
    path_to_GCDC_parquet = "/Users/Ghamay/Documents/Masters-Thesis/Data/GCDC/Data/GCDCParquet/GCDC.parquet"
    df = pd.read_parquet(path_to_GCDC_parquet)
    text_column_name = "text"
    label_column_name = "label"
    eda = EDA()
    
    print("_____________________________________")
    
    # #Total tokens
    # total_tokens = eda.total_token_count(df, text_column_name)
    
    # #Coherence Frequency
    # eda.coherence_frequency(df, label_column_name)
    # print(f"Total number of tokens in the dataset: {total_tokens}")
    
    #Average Sentence Length Calculation/Chart
    eda.average_sentence_length(df)
    # plt.figure(figsize=(6, 3))
    # plt.hist(df['average_sentence_length'])
    # plt.title('Distribution of Average Sentence Lengths')
    # plt.xlabel('Average Sentence Length')
    # plt.ylabel('Frequency')
    # plt.grid(True)
    # plt.show()
    
    print()
    print("_____________________________________")

if __name__ == "__main__":
    main()
    