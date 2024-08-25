import pandas as pd

# Define the path to the Parquet file
path_to_GCDC_parquet = "/Users/Ghamay/Documents/Masters-Thesis/Data/GCDC/Data/GCDCParquet/GCDC.parquet"

# Read the Parquet file
df = pd.read_parquet(path_to_GCDC_parquet)

# Drop the 'facts' column if it exists
if 'facts' in df.columns:
    print("facts column exits")
    df = df.drop(columns=['facts'])
else:
    print("facts column does not exist")

# Save the updated DataFrame back to the Parquet file (overwriting the original file)
df.to_parquet(path_to_GCDC_parquet)