#!/bin/bash

# Source directory containing the 1000 files
src_dir="./books_v1"

# Destination directory for the 500 combined files
dest_dir="./books_v1_combined"

# Create the destination directory if it doesn't exist
mkdir -p "$dest_dir"

# Get a list of files and shuffle the list
file_list=($(ls "$src_dir" | shuf))

# Initialize a counter
file_count=1

# Loop through the file list in pairs
for ((i=0; i<${#file_list[@]}; i+=2)); do
    # Get filenames for the pair
    file1="$src_dir/${file_list[i]}"
    file2="$src_dir/${file_list[i+1]}"

    # Define the output file name
    output_file=$(printf "%s/combined%04d.txt" "$dest_dir" "$file_count")

    # Combine the two files and write to the output file
    cat "$file1" "$file2" > "$output_file"

    # Increment the counter
    file_count=$((file_count + 1))
done
echo "500 combined files have been created in the destination directory."
