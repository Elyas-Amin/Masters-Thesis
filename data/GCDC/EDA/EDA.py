import os
import pandas as pd
import numpy as np
import ssl
import matplotlib.pyplot as plt
import nltk
import spacy
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
        df["number_of_sentences"] = df["sentences"].apply(lambda x: len(x))
        df["words_per_sentence"] = df["sentences"].apply(lambda x: sum([len(sentence) for sentence in x]))
        df["average_sentence_length"] = round(df["words_per_sentence"]/df["number_of_sentences"])
        return df
        
    def average_sentence_length_distribution_plot(self, df):
        frequency = df['average_sentence_length'].value_counts()
        frequency = frequency.sort_index()
        plt.figure(figsize=(10, 6))
        frequency.plot(kind='bar', color='blue', edgecolor='black')   
        plt.title('Distribution of Average Sentence Lengths')
        plt.xlabel('Average Sentence Length')
        plt.ylabel('Frequency')
        plt.grid(axis='y')
        
        output_folder = "/Users/Ghamay/Documents/Masters-Thesis/Data/GCDC/EDA/images"
        output_file = os.path.join(output_folder, 'average_sentence_length_distribution.png')
        plt.savefig(output_file)
        
    def total_word_count_distribution_plot(self, df):
        
        plt.hist(df["words_per_sentence"], bins=100)
        plt.figure(figsize=(10, 6))
        plt.title('Distribution of Word Count')
        plt.xlabel('Average Text Length')
        plt.ylabel('Frequency')
        plt.grid(axis='y')
        
        output_folder = "/Users/Ghamay/Documents/Masters-Thesis/Data/GCDC/EDA/images"
        output_file = os.path.join(output_folder, 'average_text_length_distribution.png')
        plt.savefig(output_file)
    
    def llm_cost_count(self):
        gpt4o = 2.5
        gpt35 = 1.50
        llama13b = 0.30
        mistral7b = 0.25
        tokens = 847185
        
        print(f"Total Costs: {(gpt4o+gpt35+llama13b+mistral7b)*10*tokens/1000000}")
        print(f"Model\t\tCost/Million Tokens\tSingle Run\tExperiment Total")
        print(f"GPT-4o\t\t{gpt4o}\t\t\t{round(gpt4o*tokens/1000000,2)}\t\t{round(gpt4o*10*tokens/1000000,2)}")
        print(f"GPT-3.5\t\t{gpt35}\t\t\t{round(gpt35*tokens/1000000,2)}\t\t{round(gpt35*10*tokens/1000000,2)}")
        print(f"Llama-13b\t{llama13b}\t\t\t{round(llama13b*tokens/1000000,2)}\t\t{round(llama13b*10*tokens/1000000,2)}")
        print(f"Mistral-7b\t{mistral7b}\t\t\t{round(mistral7b*tokens/1000000,2)}\t\t{round(mistral7b*10*tokens/1000000,2)}")
        
    def POS_tagging(self, df):
        nlp = spacy.load('en_core_web_sm')

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
    
    #Average Sentence Length Calculation
    # eda.average_sentence_length(df)
    # df.to_parquet(path_to_GCDC_parquet)

    #Sentence Length Distribution
    # eda.average_sentence_length_distribution_plot(df)
    
    #Word Count Distribution
    # eda.total_word_count_distribution_plot(df)
    
    #Total Cost
    # eda.llm_cost_count()
    
    print("_____________________________________")

if __name__ == "__main__":
    main()
    