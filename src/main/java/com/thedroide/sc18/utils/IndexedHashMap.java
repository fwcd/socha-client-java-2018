package com.thedroide.sc18.utils;

import java.util.ArrayList;
import java.util.Collection;
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
	private List<K> keyIndex = new ArrayList<>();
	private Map<K, V> data = new HashMap<>();
	
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
		keyIndex.add(key);
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
		keyIndex.add(index, key);
		return data.put(key, value);
	}

	@Override
	public V remove(int index) {
		return data.remove(keyIndex.remove(index));
	}
}
