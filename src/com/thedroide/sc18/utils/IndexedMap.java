package com.thedroide.sc18.utils;

import java.util.Map;

public interface IndexedMap<K, V> extends Map<K, V> {
	V put(int index, K key, V value);
	
	V remove(int index);
	
	K getKey(int index);
	
	default V setValue(int index, V value) {
		return put(index, getKey(index), value);
	}
	
	default V getValue(int index) {
		return get(getKey(index));
	}
}
