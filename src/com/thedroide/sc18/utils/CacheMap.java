package com.thedroide.sc18.utils;

import java.util.Map;

/**
 * A map that can be used for
 * caching and will automatically discard the
 * oldest keys when it's size reaches the specified
 * maximum size and an element is inserted.<br><br>
 * 
 * <b>The implementation is expected to preserve insertion
 * order on iteration or arbitrary keys might be removed
 * when trimming the map.</b>
 *
 * @param <K> - The key type
 * @param <V> - The value type
 */
public interface CacheMap<K, V> extends Map<K, V> {
	int getMaxSize();
}
