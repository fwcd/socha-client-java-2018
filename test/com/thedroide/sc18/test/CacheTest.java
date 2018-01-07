package com.thedroide.sc18.test;

import org.junit.Test;

import com.thedroide.sc18.utils.CacheMap;
import com.thedroide.sc18.utils.LinkedCacheMap;

import static org.junit.Assert.*;

public class CacheTest {
	@Test
	public void test() {
		CacheMap<Integer, String> cache = new LinkedCacheMap<>(3);
		cache.put(56, "test");
		cache.put(34, "demo");
		cache.put(21, "a");
		cache.put(29, "b");
		cache.put(87, "c");
		assertTrue(cache.size() == 3);
		assertTrue(cache.keySet().iterator().next() == 21);
	}
}
