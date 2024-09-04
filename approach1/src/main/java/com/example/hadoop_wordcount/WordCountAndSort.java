package com.example.hadoop_wordcount;

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

public class WordCountAndSort {

	public static class TokenizerMapper
			extends Mapper<Object, Text, Text, IntWritable> {

		private final static IntWritable one = new IntWritable(1);
		private Text word = new Text();

		// Regular expression pattern to match words and numbers
		private static final Pattern WORD_BOUNDARY = Pattern.compile("[^a-zA-Z0-9]+");

		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			StringTokenizer itr = new StringTokenizer(value.toString());
			while (itr.hasMoreTokens()) {
				String token = itr.nextToken();
				// Remove punctuation by replacing non-alphanumeric characters with an empty
				// string
				token = WORD_BOUNDARY.matcher(token).replaceAll("");
				// word.set(itr.nextToken());
				// context.write(word, one);
				if (!token.isEmpty()) {
					word.set(token.toLowerCase()); // Convert to lowercase for case-insensitive counting
					context.write(word, one);
				}
			}
		}
	}

	public static class IntSumReducer
			extends Reducer<Text, IntWritable, Text, IntWritable> {
		private IntWritable result = new IntWritable();

		public void reduce(Text key, Iterable<IntWritable> values,
				Context context) throws IOException, InterruptedException {
			int sum = 0;
			for (IntWritable val : values) {
				sum += val.get();
			}
			result.set(sum);
			context.write(key, result);
		}
	}

	public static class SortMapper
			extends Mapper<Object, Text, IntWritable, Text> {
		private IntWritable count = new IntWritable();
		private Text word = new Text();

		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			String[] parts = value.toString().split("\t");
			if (parts.length == 2) {
				word.set(parts[0]);
				count.set(Integer.parseInt(parts[1]));
				context.write(count, word);
			}
		}
	}

	public static class SortReducer
			extends Reducer<IntWritable, Text, Text, IntWritable> {
		public void reduce(IntWritable key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			for (Text val : values) {
				context.write(val, key);
			}
		}
	}
	
	

	public static void main(String[] args) throws Exception {
		
		System.out.println("\nargs[0] = " + args[0]+ ", args[1] =  "+args[1]+ ", args[2] = "+args[2] + "\n" );

		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "word count");
		job.setJarByClass(WordCountAndSort.class);
		job.setMapperClass(TokenizerMapper.class);
		job.setCombinerClass(IntSumReducer.class); // sort on key - alphabetical
		job.setReducerClass(IntSumReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		job.setNumReduceTasks(3);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		// System.exit(job.waitForCompletion(true) ? 0 : 1);
		if (!job.waitForCompletion(true)) {
			System.exit(1);
		}

		Job sortJob = Job.getInstance(conf, "soft by frequency");
		sortJob.setJarByClass(WordCountAndSort.class);
		sortJob.setMapperClass(SortMapper.class);
		sortJob.setReducerClass(SortReducer.class);
		// Sort in ascending order by default)
		//sortJob.setSortComparatorClass(IntWritable.Comparator.class);
		// Set custom comparator for sorting keys in descending order
        sortJob.setSortComparatorClass(DescendingIntWritableComparator.class);
		sortJob.setOutputKeyClass(IntWritable.class);
		sortJob.setOutputValueClass(Text.class);
		sortJob.setNumReduceTasks(1);
		FileInputFormat.addInputPath(sortJob, new Path(args[1]));
		FileOutputFormat.setOutputPath(sortJob, new Path(args[2]));
		// System.exit(sortJob.waitForCompletion(true) ? 0 : 1);
		if (!sortJob.waitForCompletion(true)) {
			System.exit(1);
		}
		
	}
}