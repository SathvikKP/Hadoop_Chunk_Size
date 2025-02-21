hdfs dfsadmin -safemode leave

echo "^C to exit - deleting files"
read p

hadoop dfs -rm -r /output/gutenberg/sort_v1_10
hadoop dfs -rm -r /output/gutenberg/sort_v1_100
hadoop dfs -rm -r /output/gutenberg/sort_v1_300
hadoop dfs -rm -r /output/gutenberg/sort_v1_600

# Variables (adjust paths as needed)
HADOOP_EXAMPLES_JAR="/usr/local/hadoop/share/hadoop/mapreduce/hadoop-mapreduce-examples-3.4.0.jar"


for size in 10 100 300 600; do
  INPUT_DIR="/input/project_gutenberg/books_v1_${size}"
  OUTPUT_DIR="/output/gutenberg/sort_v1_${size}"

  # Delete previous output
  hadoop fs -rm -r -skipTrash $OUTPUT_DIR 2>/dev/null

  echo "Start? ^C to skip"
  read p
  # Run Sort and measure time
  echo "Running Sort for $INPUT_DIR..."
  time hadoop jar $HADOOP_EXAMPLES_JAR sort \
    -D mapreduce.job.name="Sort_${size}" \
    $INPUT_DIR $OUTPUT_DIR
done

