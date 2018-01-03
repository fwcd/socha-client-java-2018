package com.thedroide.sc18.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Non-thread-safe {@link CacheMap} using a {@link LinkedHashMap}.
 *
 * @param <K> - The key type
 * @param <V> - The value type
 */
public class LinkedCacheMap<K, V> implements CacheMap<K, V> {
	private final int maxSize;
	private final Map<K, V> data;
	
	public LinkedCacheMap(int maxSize) {
		this.maxSize = maxSize;
		data = new LinkedHashMap<>(maxSize);
	}
	
	@Override
	public int getMaxSize() {
		return maxSize;
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
		if (data.size() >= maxSize) {
			Iterator<Entry<K, V>> it = data.entrySet().iterator();
			
			while (data.size() >= maxSize && it.hasNext()) {
				it.next();
				it.remove();
			}
		}
		
		return data.put(key, value);
	}

	@Override
	public V remove(Object key) {
		return data.remove(key);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public void clear() {
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
	public String toString() {
		return data.toString();
	}
	
	@Override
	public int hashCode() {
		return data.hashCode();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object o) {
		return data.equals(o) && maxSize == ((LinkedCacheMap<K, V>) o).maxSize;
	}
}
