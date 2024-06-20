import numpy as np
import pandas as pd

df = pd.read_parquet("hf://datasets/truthfulqa/truthful_qa/generation/validation-00000-of-00001.parquet", engine='pyarrow')

print(df)