package com.fwcd.sc18.geneticneural;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

import com.fwcd.sc18.utils.IndexedHashMap;
import com.fwcd.sc18.utils.IndexedMap;

public class Population {
	private final IndexedMap<float[], Float> individuals;
	
	private int survivorsPerGeneration = 5;
	private float mutationChance = 0.1F;
	private float mutatorWeight = 2;
	private float mutatorBias = 0;
	private float selectorEpsilon = 0.3F; // Probability of selecting a random instead of a "good" individual
	
	public Population(int size, Supplier<float[]> spawner) {
		individuals = new IndexedHashMap<>();

		Float initialFitness = Float.NEGATIVE_INFINITY; // This is intentionally using the boxed type
		
		for (int i=0; i<size; i++) {
			float[] individual = spawner.get();
			individuals.put(individual, initialFitness);
		}
	}
	
	public float[] sample() {
		Random random = ThreadLocalRandom.current();
		
		if (random.nextFloat() < selectorEpsilon) {
			// Choose randomly
			return individuals.getKey(random.nextInt(individuals.size()));
		} else {
			// Choose a "survivor"
			return individuals.getKey(random.nextInt(survivorsPerGeneration));
		}
	}
	
	public void setFitness(float[] individual, float fitness) {
		individuals.put(individual, fitness);
	}
	
	public void evolve() {
		sortByFitnessDescending();
		mutateIndividuals(survivorsPerGeneration, individuals.size());
	}
	
	private void sortByFitnessDescending() {
		individuals.sortByValue((a, b) -> b.compareTo(a));
	}
	
	/**
	 * Mutates the given range of individuals.
	 * 
	 * @param start - The (inclusive) start index
	 * @param end - The (exclusive) end index
	 */
	private void mutateIndividuals(int start, int end) {
		for (int i=start; i<end; i++) {
			mutate(individuals.getKey(i));
		}
	}
	
	private void mutate(float[] individual) {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		
		for (int i=0; i<individual.length; i++) {
			if (random.nextFloat() < mutationChance) {
				float previousValue = individual[i];
				individual[i] = (previousValue * (float) random.nextGaussian() * mutatorWeight) + mutatorBias;
			}
		}
	}
}
