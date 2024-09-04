#!/bin/bash

# Source directory containing the 1000 files
src_dir="./books_v1_10"



# Output file where all contents will be combined
output_file="./books_v1_10_combined/combined_file.txt"

# Initialize or clear the output file
> "$output_file"

# Loop through each file in the source directory and append its content to the output file
for file in "$src_dir"/*; do
    cat "$file" >> "$output_file"
done

echo "All files have been combined into $output_file."
