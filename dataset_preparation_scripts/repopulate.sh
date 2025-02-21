hdfs dfsadmin -safemode leave

echo "Deleting and repopulating following files"
sleep 10

hdfs dfs -ls /input/project_gutenberg

hdfs dfs -rm -r /input/project_gutenberg/books_v1
hdfs dfs -rm -r /input/project_gutenberg/books_v1_10
hdfs dfs -rm -r /input/project_gutenberg/books_v1_100
hdfs dfs -rm -r /input/project_gutenberg/books_v1_10_combined
hdfs dfs -rm -r /input/project_gutenberg/books_v1_300
hdfs dfs -rm -r /input/project_gutenberg/books_v1_600
hdfs dfs -rm -r /input/project_gutenberg/books_v1_combined
hdfs dfs -rm -r /input/project_gutenberg/books_v1_split
hdfs dfs -rm -r /input/project_gutenberg/books_5000

echo ""
echo "Deleted!"
echo ""

hdfs dfs -ls /input/project_gutenberg

sleep 10

echo ""
echo "Repopulating"
echo ""



hdfs dfs -put books_v1 /input/project_gutenberg/books_v1 &
hdfs dfs -put books_v1_10 /input/project_gutenberg/books_v1_10 &
hdfs dfs -put books_v1_100 /input/project_gutenberg/books_v1_100 &
hdfs dfs -put books_v1_10_combined /input/project_gutenberg/books_v1_10_combined &
hdfs dfs -put books_v1_300 /input/project_gutenberg/books_v1_300 &
hdfs dfs -put books_v1_600 /input/project_gutenberg/books_v1_600 &
hdfs dfs -put books_v1_combined /input/project_gutenberg/books_v1_combined &
hdfs dfs -put books_v1_split /input/project_gutenberg/books_v1_split &


hdfs dfs -put books_v1 /input/project_gutenberg/books_5000 &
hdfs dfs -put books_v2 /input/project_gutenberg/books_5000 &
hdfs dfs -put books_v3 /input/project_gutenberg/books_5000 &
hdfs dfs -put books_v4 /input/project_gutenberg/books_5000 &
hdfs dfs -put books_v5 /input/project_gutenberg/books_5000 &

wait

hdfs dfs -ls /input/project_gutenberg

echo "Complete!"

