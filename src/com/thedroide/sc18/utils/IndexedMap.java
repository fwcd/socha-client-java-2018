package com.thedroide.sc18.utils;

import java.util.Map;

public interface IndexedMap<K, V> extends Map<K, V> {
	public V put(int index, K key, V value);
	
	public V remove(int index);
	
	public K getKey(int index);
	
	public default V setValue(int index, V value) {
		return put(index, getKey(index), value);
	}
	
	public default V getValue(int index) {
		return get(getKey(index));
	}
}
