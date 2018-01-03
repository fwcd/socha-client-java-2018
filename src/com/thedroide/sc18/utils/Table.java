package com.thedroide.sc18.utils;

public interface Table<K, S, V> {
	V put(K key1, S key2, V value);
	
	V remove(K key1, S key2);
	
	V get(K key1, S key2);
	
	int size();
}
