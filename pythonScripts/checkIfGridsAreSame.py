import os

def compare_files_in_directories(dir1, dir2, filename):
    # Construct full file paths
    file1_path = os.path.join(dir1, filename)
    file2_path = os.path.join(dir2, filename)

    #Check if both files exist
    if not os.path.exists(file1_path):
        print(f"File not found in directory 1: {file1_path}")
        return False
    if not os.path.exists(file2_path):
        print(f"File not found in directory 2: {file2_path}")
        return False

    # Open and compare the files line by line
    with open(file1_path, 'r') as file1, open(file2_path, 'r') as file2:
        file1_lines = file1.readlines()
        file2_lines = file2.readlines()

        if len(file1_lines) != len(file2_lines):
            print("Files have different number of lines.")
            return False

        for line_num, (line1, line2) in enumerate(zip(file1_lines, file2_lines), 1):
            if line1 != line2:
                print(f"Difference found on line {line_num}:\nFile 1: {line1.strip()}\nFile 2: {line2.strip()}")
                return False

    print("Files are identical.")
    return True

# Define directories
dir1 = '/Users/Ghamay/Documents/mastersThesis/Data/GCDC/Data/OutputData/test/test_train_grids/'
dir2 = '/Users/Ghamay/Documents/mastersThesis/Data/GCDC/Data/OutputData/clinton/clinton_train_grids/'

# Define the common file name to compare
filename = 'C05739631_0_grids.txt'

# Compare the files with the same name in different directories
compare_files_in_directories(dir1, dir2, filename)
