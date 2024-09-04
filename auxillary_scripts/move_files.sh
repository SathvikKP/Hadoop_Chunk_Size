
#!/bin/bash

# Source directory containing the files
src_dir="./books_split"

# Target directories
dest_dirs=(
    "./parts/books_v1"
    "./parts/books_v2"
    "./parts/books_v3"
    "./parts/books_v4"
    "./parts/books_v5"
    "./parts/books_v6"
)

# Create target directories if they don't exist
for dir in "${dest_dirs[@]}"; do
    mkdir -p "$dir"
done

# Move 1000 files to the first 5 directories
file_count=0
for file in "$src_dir"/*; do
    if [ $file_count -lt 1000 ]; then
        mv "$file" "${dest_dirs[0]}"
    elif [ $file_count -lt 2000 ]; then
        mv "$file" "${dest_dirs[1]}"
    elif [ $file_count -lt 3000 ]; then
        mv "$file" "${dest_dirs[2]}"
    elif [ $file_count -lt 4000 ]; then
        mv "$file" "${dest_dirs[3]}"
    elif [ $file_count -lt 5000 ]; then
        mv "$file" "${dest_dirs[4]}"
    elif [ $file_count -lt 6170 ]; then
        mv "$file" "${dest_dirs[5]}"
    fi
    file_count=$((file_count + 1))
done
