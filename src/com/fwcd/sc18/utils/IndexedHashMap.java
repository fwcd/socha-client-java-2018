package com.fwcd.sc18.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A {@link HashMap}-based implementation of an
 * indexed map.
 * 
 * @param <K> - The key type
 * @param <V> - The value type
 */
public class IndexedHashMap<K, V> implements IndexedMap<K, V> {
	// Intentionally not declaring interface types to
	// avoid unnecessary virtual method calls.
	private final ArrayList<K> keyIndex;
	private final HashMap<K, V> data;
	
	public IndexedHashMap() {
		keyIndex = new ArrayList<>();
		data = new HashMap<>();
	}
	
	public IndexedHashMap(int initialCapacity) {
		keyIndex = new ArrayList<>(initialCapacity);
		data = new HashMap<>(initialCapacity);
	}
	
	@Override
	public int size() {
		return data.size();
	}

	@Override
	public boolean isEmpty() {
		return data.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return data.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return data.containsValue(value);
	}

	@Override
	public V get(Object key) {
		return data.get(key);
	}

	@Override
	public V put(K key, V value) {
		if (!data.containsKey(key)) {
			keyIndex.add(key);
		}
		
		return data.put(key, value);
	}

	@Override
	public V remove(Object key) {
		keyIndex.remove(key);
		return data.remove(key);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		for (K key : m.keySet()) {
			keyIndex.add(key);
		}
		
		data.putAll(m);
	}

	@Override
	public void clear() {
		keyIndex.clear();
		data.clear();
	}

	@Override
	public Set<K> keySet() {
		return data.keySet();
	}

	@Override
	public Collection<V> values() {
		return data.values();
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return data.entrySet();
	}

	@Override
	public K getKey(int index) {
		return keyIndex.get(index);
	}

	@Override
	public V put(int index, K key, V value) {
		keyIndex.set(index, key);
		return data.put(key, value);
	}

	@Override
	public V remove(int index) {
		return data.remove(keyIndex.remove(index));
	}

	@Override
	public void sortByValue(Comparator<V> comparator) {
		Collections.sort(keyIndex, (a, b) -> comparator.compare(data.get(a), data.get(b)));
	}

	@Override
	public void sortByKey(Comparator<K> comparator) {
		Collections.sort(keyIndex, comparator);
	}

	@Override
	public int indexOfKey(K key) {
		return keyIndex.indexOf(key);
	}

	@Override
	public List<K> keyList() {
		return keyIndex;
	}

	@Override
	public List<V> valueList() {
		List<V> values = new ArrayList<>();
		
		for (K key : keyIndex) {
			values.add(data.get(key));
		}
		
		return values;
	}
}
