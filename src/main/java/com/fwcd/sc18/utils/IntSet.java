package com.fwcd.sc18.utils;

import java.util.Arrays;
import java.util.Set;

/**
 * A primitive specialization of {@link Set} for integers
 * using a hash-table as data structure.
 */
public class IntSet {
	private final int[][] data;
	private final int initialBucketSize;
	private boolean containsZero = false;
	
	public IntSet() {
		this(16, 3);
	}
	
	public IntSet(int bucketCount, int initialBucketSize) {
		data = new int[bucketCount][];
		this.initialBucketSize = initialBucketSize;
		
		for (int i=0; i<bucketCount; i++) {
			data[i] = new int[0];
		}
	}
	
	public IntSet(int... values) {
		this();
		for (int v : values) {
			add(v);
		}
	}

	public void add(int v) {
		if (v == 0) {
			containsZero = true;
		} else {
			int i = hash(v);
			int j = expandAndGetIndex(i, v);
			data[i][j] = v;
		}
	}
	
	public void remove(int v) {
		if (v == 0) {
			containsZero = false;
		} else {
			int[] bucket = data[hash(v)];
			
			for (int i=0; i<bucket.length; i++) {
				if (bucket[i] == v) {
					bucket[i] = 0;
					break;
				}
			}
		}
	}
	
	private int expandAndGetIndex(int bucketIndex, int valueToBeStored) {
		int[] arr = data[bucketIndex];
		
		if (arr.length == 0) {
			int[] newBucket = new int[initialBucketSize];
			for (int i=0; i<arr.length; i++) {
				newBucket[i] = 0;
			}
			
			data[bucketIndex] = newBucket;
			return 0;
		} else {
			for (int i=0; i<arr.length; i++) {
				int v = arr[i];
				if (v == 0 || v == valueToBeStored) {
					return i;
				}
			}
			
			int[] newBucket = new int[arr.length + 1];
			System.arraycopy(arr, 0, newBucket, 0, arr.length);
			newBucket[arr.length] = 0;
			data[bucketIndex] = newBucket;
			
			return arr.length;
		}
	}
	
	public boolean contains(int searchedValue) {
		if (searchedValue == 0) {
			return containsZero;
		}
		
		int[] bucket = data[hash(searchedValue)];
		
		for (int v : bucket) {
			if (v == searchedValue) {
				return true;
			}
		}
		
		return false;
	}
	
	private int hash(int v) {
		return Math.abs(v) % data.length;
	}
	
	public String bucketsToString() {
		return Arrays.deepToString(data);
	}
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder().append('[');
		
		if (containsZero) {
			s.append("0, ");
		}
		
		for (int[] bucket : data) {
			for (int v : bucket) {
				if (v != 0) {
					s.append(v).append(", ");
				}
			}
		}
		
		int len = s.length();
		if (len > 2) {
			s.delete(len - 2, len);
		}
		
		return s.append(']').toString();
	}
}
