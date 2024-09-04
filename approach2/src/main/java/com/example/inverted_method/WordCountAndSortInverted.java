package com.example.inverted_method;


import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.io.WritableComparator;

// Main class
public class WordCountAndSortInverted {

    // Phase 1: Inverted Index Mapper
    public static class InvertedIndexMapper extends Mapper<Object, Text, Text, Text> {
        private Text word = new Text();
        private Text documentId = new Text();
        private static final Pattern WORD_BOUNDARY = Pattern.compile("[^a-zA-Z0-9]+");

        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            StringTokenizer itr = new StringTokenizer(value.toString());
            documentId.set(key.toString());

            while (itr.hasMoreTokens()) {
                String token = itr.nextToken();
                token = WORD_BOUNDARY.matcher(token).replaceAll("");
                if (!token.isEmpty()) {
                    word.set(token.toLowerCase());
                    context.write(word, documentId);
                }
            }
        }
    }
/*
    // Phase 1: Inverted Index Reducer
    public static class InvertedIndexReducer extends Reducer<Text, Text, Text, Text> {
        @Override
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            HashSet<String> uniqueDocs = new HashSet<>();
            for (Text val : values) {
                uniqueDocs.add(val.toString());
            }
            context.write(key, new Text(String.join(" ", uniqueDocs)));
        }
    }
*/
	
    // Phase 1: Inverted Index Reducer
    public static class InvertedIndexReducer extends Reducer<Text, Text, Text, Text> {
        private IntWritable result = new IntWritable();

        @Override
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        // Use a map to keep track of document IDs and their counts
        Map<String, Integer> docCountMap = new HashMap<>();
        
        for (Text val : values) {
            String docId = val.toString();
            docCountMap.put(docId, docCountMap.getOrDefault(docId, 0) + 1);
        }
        
        // Build the result string in the format: word doc1 doc1 doc2 doc3 doc3 ...
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, Integer> entry : docCountMap.entrySet()) {
            String docId = entry.getKey();
            int count = entry.getValue();
            for (int i = 0; i < count; i++) {
                result.append(docId).append(" ");
            }
        }
        
        context.write(key, new Text(result.toString().trim()));
    	}
    }
    
    // Phase 2: Frequency Count Mapper
    public static class FrequencyCountMapper extends Mapper<Object, Text, Text, IntWritable> {
        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();

        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String[] parts = value.toString().split("\\s+");
            if (parts.length > 1) {
            	word.set(parts[0]);  // The word itself
            	int count = parts.length - 1; // Number of document IDs
            	context.write(word, new IntWritable(count));
            }
        }
    }

    // Phase 2: Frequency Count Reducer
    public static class FrequencyCountReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        private IntWritable result = new IntWritable();

        @Override
        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int count = 0;
            for (IntWritable val : values) {
                count += val.get();  // Sum up the counts for each word
            }
            result.set(count);
            context.write(key, result);
        }
    }

    // Phase 3: Sorting Mapper
    public static class SortingMapper extends Mapper<Object, Text, IntWritable, Text> {
        private IntWritable count = new IntWritable();
        private Text word = new Text();

        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String[] parts = value.toString().split("\\s+");
            if (parts.length == 2) {
                word.set(parts[0]);
                count.set(Integer.parseInt(parts[1]));
                context.write(count, word);
            }
        }
    }

    // Custom Comparator for Sorting in Descending Order
    public static class IntWritableDecreasingComparator extends WritableComparator {
        public IntWritableDecreasingComparator() {
            super(IntWritable.class, true);
        }

        @Override
        public int compare(byte[] b1, int s1, int l1, byte[] b2, int s2, int l2) {
            int int1 = readInt(b1, s1);
            int int2 = readInt(b2, s2);
            return Integer.compare(int2, int1);  // Descending order
        }
    }

    // Phase 3: Sorting Reducer
    public static class SortingReducer extends Reducer<IntWritable, Text, Text, IntWritable> {
        private IntWritable result = new IntWritable();
        private Text word = new Text();

        @Override
        public void reduce(IntWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            for (Text val : values) {
                word.set(val);
                result.set(key.get());
                context.write(word, result);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();

        // Phase 1: Inverted Index
        Job job1 = Job.getInstance(conf, "inverted index");
        job1.setJarByClass(WordCountAndSortInverted.class);
        job1.setMapperClass(InvertedIndexMapper.class);
        job1.setReducerClass(InvertedIndexReducer.class);
        job1.setOutputKeyClass(Text.class);
        job1.setOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job1, new Path(args[0]));
        FileOutputFormat.setOutputPath(job1, new Path(args[1]));
        job1.waitForCompletion(true);

        // Phase 2: Frequency Count
        Job job2 = Job.getInstance(conf, "frequency count");
        job2.setJarByClass(WordCountAndSortInverted.class);
        job2.setMapperClass(FrequencyCountMapper.class);
        job2.setReducerClass(FrequencyCountReducer.class);
        job2.setOutputKeyClass(Text.class);
        job2.setOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(job2, new Path(args[1]));
        FileOutputFormat.setOutputPath(job2, new Path(args[2]));
        job2.waitForCompletion(true);

        // Phase 3: Sorting
        Job job3 = Job.getInstance(conf, "sorting");
        job3.setJarByClass(WordCountAndSortInverted.class);
        job3.setMapperClass(SortingMapper.class);
        job3.setReducerClass(SortingReducer.class);
        job3.setOutputKeyClass(IntWritable.class);
        job3.setOutputValueClass(Text.class);
        job3.setSortComparatorClass(IntWritableDecreasingComparator.class);  // Set custom comparator for descending order
        FileInputFormat.addInputPath(job3, new Path(args[2]));
        FileOutputFormat.setOutputPath(job3, new Path(args[3]));
        System.exit(job3.waitForCompletion(true) ? 0 : 1);
    }
}

