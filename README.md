# hadoop_wordcount_final

# Environment Information

To run successfully, make sure the environment variables and hadoop settings such as core-site.xml, mapred.xml are properly set. I used Windows Subsystem for Linux and installed Ubuntu 22.04.4 LTS.

Refer the official documentation for installation and setup
'''
https://hadoop.apache.org/releases.html
https://hadoop.apache.org/docs/stable/hadoop-project-dist/hadoop-common/SingleCluster.html
https://hadoop.apache.org/docs/current/hadoop-mapreduce-client/hadoop-mapreduce-client-core/MapReduceTutorial.html
'''
I used the eclipse IDE and used the maven build tool for compiling and packaging

# Execution Commands

To run the program, use the following commands
'''
hadoop jar WordCountAndSort_Final.jar com.example.hadoop_wordcount.WordCountAndSort /input/project_gutenberg/books_v1 /output/gutenberg/temp /output/gutenberg/output/

hadoop jar WordCountAndSort_InvertedFinal.jar com.example.inverted_method.WordCountAndSortInverted /input/project_gutenberg/books_v1_100 /output/gutenberg/temp1 /output/gutenberg/temp2 /output/gutenberg/output/
'''
Make sure to clear any output directories using following commands
'''
hadoop dfs -rm -r /output/WordCount/temp /output/WordCount/Shake
hadoop dfs -rm -r /output/gutenberg/output /output/gutenberg/temp
hadoop dfs -rm -r /output/gutenberg/temp1 /output/gutenberg/temp2
'''

# Note on Dataset used

I used plain text files from project gutenberg (https://www.gutenberg.org/). 

To get a large number of files, I used a helper script to automate downloads(auxillary_scripts/downloader.py) (Reference : https://cognitivedemons.wordpress.com/2017/07/10/downloading-all-english-books-from-gutenberg-org-with-python/). 

I split the downloaded files into different directories using bash scripts (auxillary_scripts/*.sh)

Then, I moved them into the hdfs file system using hdfs commands
'''
hdfs dfs -put books_v1 /input/project_gutenberg/books_v1 
'''

# Additional information: 

The env variables in my ~/.bashrc
'''
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
'''


export HADOOP_CLASSPATH=${JAVA_HOME}/lib/tools.jar
#export JAVA_HOME=$(readlink -f /usr/bin/java | sed "s:bin/java::")
