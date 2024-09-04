package com.example.hadoop_wordcount;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;
import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class DescendingIntWritableComparator extends WritableComparator {

    public DescendingIntWritableComparator() {
        super(IntWritable.class);
    }

    @Override
    public int compare(byte[] b1, int s1, int l1, byte[] b2, int s2, int l2) {
        IntWritable int1 = new IntWritable();
        IntWritable int2 = new IntWritable();
        try {
            int1.readFields(new DataInputStream(new ByteArrayInputStream(b1, s1, l1)));
            int2.readFields(new DataInputStream(new ByteArrayInputStream(b2, s2, l2)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return -int1.compareTo(int2); // Reverse the comparison for descending order
    }
}