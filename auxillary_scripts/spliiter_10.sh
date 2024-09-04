#!/bin/bash

# Source directory containing the 1000 files
src_dir="./books_v1"

# Destination directories
dest_dir="./books_v1_10"

# Create the destination directory if it doesn't exist
mkdir -p "$dest_dir"

# Get the list of files in the source directory
file_list=($(ls "$src_dir"))

# Copy the first 10 files to the destination directory
for ((i=0; i<10; i++)); do
    cp "$src_dir/${file_list[i]}" "$dest_dir"
done

echo "10 files have been copied to the destination directory."
