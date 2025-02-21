#!/bin/bash

echo "Start? Will delete old outputs ^C to exit"
#read p

# Note to self --> I am saving exec times as file name for easy reference
HADOOP_EXAMPLES_JAR="/usr/local/hadoop/share/hadoop/mapreduce/hadoop-mapreduce-examples-3.4.0.jar"
PATTERN='Australia|Brazil|Canada|China|Egypt|France|Germany|India|Italy|Japan|Mexico|Russia|Spain|United States|England|Britain'
EXEC_TS=$(date +%Y%m%d%H%M%S) 
BASE_DIR="logs/exec_${EXEC_TS}"
mkdir -p $BASE_DIR

DATASET_SIZES=(10 100 300) #600)

hdfs dfsadmin -safemode leave

# Note to self --> risky
for size in "${DATASET_SIZES[@]}"; do
  hadoop fs -rm -r -skipTrash "/output/gutenberg/grep_v1_${size}"
done


for size in "${DATASET_SIZES[@]}"; do
  echo "Start exec? ^C to exit"
  #read p
  
  # for each file size class, create directory structure to store logs
  SIZE_DIR="${BASE_DIR}/${size}"
  mkdir -p "${SIZE_DIR}/metrics" "${SIZE_DIR}/logs" "${SIZE_DIR}/outputs"
  
  INPUT_DIR="/input/project_gutenberg/books_v1_${size}"
  OUTPUT_DIR="/output/gutenberg/grep_v1_${size}"
  JOB_NAME="Grep_${size}"

  # i am using sar to monitor sys stats
  sar -urdb -n DEV 1 > "${SIZE_DIR}/metrics/sar.log" &
  SAR_PID=$!

  # actual command
  echo -e "\n Starting exec $JOB_NAME "
  /usr/bin/time -v -o "${SIZE_DIR}/metrics/time.txt" hadoop jar $HADOOP_EXAMPLES_JAR grep -D mapreduce.job.name="$JOB_NAME" $INPUT_DIR $OUTPUT_DIR "$PATTERN" 2>&1 | tee "${SIZE_DIR}/logs/job.log"

  # kill monitoring
  kill $SAR_PID
  sleep 2
  hdfs dfs -get /output/gutenberg/grep_v1_${size} ${SIZE_DIR}/outputs/
done

echo "Completed!! Results in ${BASE_DIR}/"
