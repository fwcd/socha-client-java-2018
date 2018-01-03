package com.thedroide.sc18.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class MapTable<K, S, V> implements Table<K, S, V> {
	private final Supplier<Map<S, V>> keyMapSupplier2;
	private final Map<K, Map<S, V>> data;
	
	public MapTable() {
		this(HashMap::new, HashMap::new);
	}
	
	public MapTable(Supplier<Map<K, Map<S, V>>> keyMapSupplier1, Supplier<Map<S, V>> keyMapSupplier2) {
		this.keyMapSupplier2 = keyMapSupplier2;
		data = keyMapSupplier1.get();
	}
	
	@Override
	public V put(K key1, S key2, V value) {
		data.putIfAbsent(key1, keyMapSupplier2.get());
		return data.get(key1).put(key2, value);
	}

	@Override
	public V get(K key1, S key2) {
		Map<S, V> s = data.get(key1);
		
		if (s == null) {
			return null;
		} else {
			return s.get(key2);
		}
	}

	@Override
	public int size() {
		return (int) data.values().stream()
				.flatMap(map -> map.values().stream())
				.count();
	}

	@Override
	public V remove(K key1, S key2) {
		Map<S, V> s = data.get(key1);
		
		if (s == null) {
			return null;
		} else {
			return s.remove(key2);
		}
	}
}
