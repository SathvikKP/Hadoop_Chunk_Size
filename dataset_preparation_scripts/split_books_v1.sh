#!/bin/bash

# Source directory containing the 1000 files
src_dir="./books_v1"

# Destination directory for the 500 combined files
dest_dir="./books_v1_split"

# Create the destination directory if it doesn't exist
mkdir -p "$dest_dir"

# Initialize a counter for the split files
file_count=1

# Loop through each file in the source directory
for file in "$src_dir"/*; do
    # Get the base name and extension of the file
    base_name=$(basename "$file")
    file_ext="${base_name##*.}"
    base_name="${base_name%.*}"

    # Calculate the split size (half of the original file size)
    file_size=$(stat -c %s "$file")
    split_size=$((file_size / 2))

    # Define the output filenames
    output_file1=$(printf "%s/%s_part1.%s" "$dest_dir" "$base_name" "$file_ext")
    output_file2=$(printf "%s/%s_part2.%s" "$dest_dir" "$base_name" "$file_ext")

    # Split the file into two parts
    dd if="$file" bs=1 count="$split_size" of="$output_file1"
    dd if="$file" bs=1 skip="$split_size" of="$output_file2"

    # Increment the file count
    file_count=$((file_count + 2))
done

echo "2000 split files have been created in the destination directory."
