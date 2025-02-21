# hadoop_wordcount_final

# PLEASE READ THE "P1.Karatattu Padmanabha Sathvik.COMPLETE_REPORT.pdf" file

# Environment Information

To run successfully, make sure the environment variables and hadoop settings such as core-site.xml, mapred.xml are properly set. I used Windows Subsystem for Linux and installed Ubuntu 22.04.4 LTS.

Refer the official documentation for installation and setup
```
https://hadoop.apache.org/releases.html
https://hadoop.apache.org/docs/stable/hadoop-project-dist/hadoop-common/SingleCluster.html
https://hadoop.apache.org/docs/current/hadoop-mapreduce-client/hadoop-mapreduce-client-core/MapReduceTutorial.html
```
I used the eclipse IDE and used the maven build tool for compiling and packaging

# Note on Dataset used

I used plain text files from project gutenberg (https://www.gutenberg.org/). 

To get a large number of files, I used a helper script to automate downloads(auxillary_scripts/downloader.py) (Reference : https://cognitivedemons.wordpress.com/2017/07/10/downloading-all-english-books-from-gutenberg-org-with-python/). 

I split the downloaded files into different directories using bash scripts (auxillary_scripts/*.sh)

Then, I moved them into the hdfs file system using hdfs commands
```
hdfs dfs -put books_v1 /input/project_gutenberg/books_v1 
```

## Overview of data set preparation scripts 
-  downloader.py : Downloads large number of text files
- splitter_*.sh : Splits files into groups to upload to hdfs (like 10 files, 100 files, 300 files, 600 files)
- repopulate.sh : Uploads files to the hdfs file system

# Main scripts

```bash
run_inbuild_example_wordcount.sh
```
• A shell script to run the built-in Hadoop wordcount example.
• Simply execute the script in your terminal (ensure your Hadoop environment is configured).
• This will store logs in the logs directory

```bash
run_inbuild_example_grep.sh
```
• A shell script to run the built-in Hadoop grep example.
•  Run this script from your Linux terminal after verifying that Hadoop is properly set up
• This will store logs in the logs directory

# logs Directory
• Contains logs generated during Hadoop job executions.
• Check this directory for execution details, errors, and performance messages.

# stat_analyzer.py
• A Python script for analyzing and summarizing performance metrics from Hadoop jobs.
• It processes data from the stats.txt file and outputs analysis reports.

# stats.txt
• A text file containing detailed performance metrics and system usage statistics from your Hadoop jobs.
• Review this file to understand memory, network, CPU, and disk performance during job runs.

# hadoop_graphs
• A directory containing graphs that visualize performance trends based on the data in stats.txt. 

# Execution and Analysis Workflow
- Run the wordcount and grep examples using the provided shell scripts.
- Monitor job execution by checking the logs directory.
- Analyze performance using stat_analyzer.py, which reads and interprets the data in stats.txt.

# Environment Settings
To change the chunk size, edit the vi hdfs-site.xml file inside the $HADOOP_HOME/etc/hadoop location. Add the following property and vary it. Remember to restart everytime once you change theh property
```
    <property>
            <name>dfs.blocksize</name>
            <value>536870912</value>
    </property>
    <!-- 4MB 4194304 -->
    <!-- 16MB 16777216 -->
    <!-- 64MB 67108864 -->
    <!-- 128MB 134217728-->
    <!-- 256MB 268435456 -->
    <!-- 512MB 536870912 -->
```
# Extra Stuff Guide --> Finding Most Popular Words in the text files

To run the program, use the following commands (change the input and output directories, note the number of temporary directories involved)
```
hadoop jar WordCountAndSort_Final.jar com.example.hadoop_wordcount.WordCountAndSort /input/files /output/temp /output/result

hadoop jar WordCountAndSort_InvertedFinal.jar com.example.inverted_method.WordCountAndSortInverted /input/files /output/temp1 /output/temp2 /output/result
```
Make sure to clear any output directories using following commands
```
hadoop dfs -rm -r /output/result /output/gutenberg/temp /output/gutenberg/temp1 /output/gutenberg/temp2
```



# Additional information: 

The env variables in my ~/.bashrc
```
#java
#export JAVA_HOME=/usr/lib/jvm/jdk-22.0.2-oracle-x64
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

#hadoop
export HADOOP_HOME=/usr/local/hadoop
export HADOOP_INSTALL=$HADOOP_HOME
export HADOOP_MAPRED_HOME=$HADOOP_HOME
export HADOOP_COMMON_HOME=$HADOOP_HOME
export HADOOP_HDFS_HOME=$HADOOP_HOME
export YARN_HOME=$HADOOP_HOME
export HADOOP_COMMON_LIB_NATIVE_DIR=$HADOOP_HOME/lib/native
export PATH=$PATH:$HADOOP_HOME/sbin:$HADOOP_HOME/bin
export HADOOP_OPTS="-Djava.library.path=$HADOOP_HOME/lib/native"

export HADOOP_CLASSPATH=${JAVA_HOME}/lib/tools.jar
#export JAVA_HOME=$(readlink -f /usr/bin/java | sed "s:bin/java::")
```
# Hadoop_Chunk_Size
