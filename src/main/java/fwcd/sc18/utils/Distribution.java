package fwcd.sc18.utils;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Represents a softmaxed, weighted list of items from
 * which an item may be picked stochastically.
 * 
 * @author Fredrik
 *
 * @param <E> - The item type
 */
public class Distribution<E> {
	private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();
	private final IndexedMap<E, Float> probabilities;
	private float total = 0;
	
	public Distribution(IndexedMap<E, Float> input) {
		probabilities = new IndexedHashMap<>(input.size());
		float expSum = 0;
		
		for (float v : input.values()) {
			float exp = clamp((float) Math.exp(v), 1e32F, -1e32F);
			expSum += exp;
		}
		
		for (E key : input.keyList()) {
			float p = clamp((float) Math.exp(input.get(key)), 1e32F, -1e32F) / expSum;
			total += p;
			probabilities.put(key, p);
		}
	}
	
	private float clamp(float x, float min, float max) {
		if (x > max) {
			return max;
		} else if (x < min) {
			return min;
		} else {
			return x;
		}
	}

	public int pickIndexStochastically(int excludedIndex) {
		int size = probabilities.size();
		float random = RANDOM.nextFloat() - probabilities.getValue(excludedIndex);
		
		for (int i=0; i<size; i++) {
			if (i == excludedIndex) {
				continue;
			}
			
			random -= probabilities.getValue(i);
			
			if (random <= 0) {
				return i;
			}
		}
		
		throw new RuntimeException("No index could be picked from the distribution.");
	}

	public int pickIndexStochastically() {
		int size = probabilities.size();
		float random = RANDOM.nextFloat();
		
		for (int i=0; i<size; i++) {
			random -= probabilities.getValue(i);
			
			if (random <= 0) {
				return i;
			}
		}
		
		throw new RuntimeException("No index could be picked from the distribution.");
	}
	
	public E pickStochastically() {
		return probabilities.getKey(pickIndexStochastically());
	}
}
