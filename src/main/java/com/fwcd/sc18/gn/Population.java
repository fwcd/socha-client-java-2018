package com.fwcd.sc18.gn;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

public class Population {
	private final Map<float[], Float> individuals;
	
	private int survivorsPerGeneration = 5;
	private float mutationChance = 0.1F;
	private float mutatorWeight = 2;
	private float mutatorBias = 0;
	
	public Population(int size, Supplier<float[]> spawner) {
		individuals = new HashMap<>();
		
		for (int i=0; i<size; i++) {
			float[] individual = spawner.get();
			
			if (individual.length != individualSize) {
				throw new RuntimeException("The spawned individual's gene count does not match provided individual size.");
			}
			
			individuals[i] = individual;
		}
	}
	
	public void evolve() {
		sortByFitnessDescending();
		mutateIndividuals(survivorsPerGeneration, individuals.length);
	}
	
	private void sortByFitnessDescending() {
		
	}
	
	/**
	 * Mutates the given range of individuals.
	 * 
	 * @param start - The (inclusive) start index
	 * @param end - The (exclusive) end index
	 */
	private void mutateIndividuals(int start, int end) {
		for (int i=start; i<end; i++) {
			mutate(individuals[i]);
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
