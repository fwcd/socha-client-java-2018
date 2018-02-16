package com.fwcd.sc18.geneticneural;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

import com.fwcd.sc18.utils.IndexedHashMap;
import com.fwcd.sc18.utils.IndexedMap;

public class Population {
	private final IndexedMap<float[], Float> individuals;
	
	private File autoSaveFolder = null;
	private int survivorsPerGeneration = 5;
	private float mutationChance = 0.1F;
	private float mutatorWeight = 2;
	private float mutatorBias = 0;
	private float selectorEpsilon = 0.3F; // Probability of selecting a random instead of a "good" individual
	
	public Population(int size, Supplier<float[]> spawner, File autoSaveFolder) {
		this.autoSaveFolder = autoSaveFolder;
		individuals = new IndexedHashMap<>();

		if (!loadAll(size)) {
			Float initialFitness = Float.NEGATIVE_INFINITY;
			
			for (int i=0; i<size; i++) {
				float[] individual = spawner.get();
				put(individual, initialFitness);
			}
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
	
	public void put(float[] individual, float fitness) {
		individuals.put(individual, fitness);
		
		if (autoSaveFolder != null) {
			save(individual, fitness);
		}
	}

	private void save(float[] individual, float fitness) {
		int index = individuals.indexOfKey(individual);
		File file = new File(autoSaveFolder.getAbsolutePath() + "/Individual" + Integer.toString(index));
		
		try (FileOutputStream fos = new FileOutputStream(file); DataOutputStream dos = new DataOutputStream(fos)) {
			dos.writeInt(individual.length);
			dos.writeFloat(fitness);
			for (float gene : individual) {
				dos.writeFloat(gene);
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private boolean loadAll(int total) {
		String folderPath = autoSaveFolder.getAbsolutePath();
		
		for (int index=0; index<total; index++) {
			File file = new File(folderPath + "/Individual" + Integer.toString(index));
			
			if (!file.exists()) {
				return false;
			}
			
			try (FileInputStream fis = new FileInputStream(file); DataInputStream dis = new DataInputStream(fis)) {
				float[] individual = new float[dis.readInt()];
				float fitness = dis.readFloat();
				int i = 0;
				
				while (dis.available() > 0) {
					individual[i++] = dis.readFloat();
				}
				
				individuals.put(index, individual, fitness);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
		
		return true;
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
