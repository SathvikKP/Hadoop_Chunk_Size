#!/bin/bash

# Source directory containing the 1000 files
src_dir="./books_v1"

# Destination directories
dest_dir1="./books_v1_100"
dest_dir2="./books_v1_300"
dest_dir3="./books_v1_600"

# Create the destination directories if they don't exist
mkdir -p "$dest_dir1"
mkdir -p "$dest_dir2"
mkdir -p "$dest_dir3"

# Get the list of files in the source directory
file_list=($(ls "$src_dir"))

# Copy the first 100 files to dir1
for ((i=0; i<100; i++)); do
    cp "$src_dir/${file_list[i]}" "$dest_dir1"
done

# Copy the next 300 files to dir2
for ((i=100; i<400; i++)); do
    cp "$src_dir/${file_list[i]}" "$dest_dir2"
done

# Copy the remaining 600 files to dir3
for ((i=400; i<1000; i++)); do
    cp "$src_dir/${file_list[i]}" "$dest_dir3"
done

echo "Files have been copied to the destination directories in the specified ratio."
