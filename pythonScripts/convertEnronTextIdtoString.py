import json

# Define the file path
file_path = '/Users/Ghamay/Documents/mastersThesis/Data/GCDC/Data/JSONLFiles/Enron_train.jsonl'

# Read the original file content
with open(file_path, 'r') as infile:
    lines = infile.readlines()

# Open the same file for writing (this will overwrite the existing file)
with open(file_path, 'w') as outfile:
    # Process each line in the JSONL file
    for line in lines:
        # Parse the JSON object from the line
        json_obj = json.loads(line)
        
        # Convert text_id from int to string if necessary
        if isinstance(json_obj.get('text_id'), int):
            json_obj['text_id'] = str(json_obj['text_id'])
        
        # Write the modified JSON object back to the same file
        outfile.write(json.dumps(json_obj) + '\n')

print(f'Conversion complete. The file has been overwritten at {file_path}')
