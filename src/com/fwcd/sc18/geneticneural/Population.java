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

import com.fwcd.sc18.utils.Distribution;
import com.fwcd.sc18.utils.IndexedHashMap;
import com.fwcd.sc18.utils.IndexedMap;

public class Population {
	private final IndexedMap<float[], Float> individuals;
	private final Supplier<float[]> spawner;
	
	private File autoSaveFolder = null;
	private int survivorsPerGeneration = 5;
	private float mutationChance = 0.05F;
	private float resetChance = 0.01F;
	private float mutatorWeight = 2;
	private float mutatorBias = 0;
	
	private int counter = 0;
	
	public Population(int size, Supplier<float[]> spawner, File autoSaveFolder) {
		this.autoSaveFolder = autoSaveFolder;
		individuals = new IndexedHashMap<>();
		this.spawner = spawner;
		
		if (!loadAll(size)) {
			Float initialFitness = Float.NEGATIVE_INFINITY;
			
			for (int i=0; i<size; i++) {
				float[] individual = spawner.get();
				put(individual, initialFitness);
			}
		}
	}
	
	public float[] sample() {
		return individuals.getKey(counter);
	}
	
	public void put(float[] individual, float fitness) {
		individuals.put(individual, fitness);
		
		if (autoSaveFolder != null) {
			save(individual, fitness);
		}
	}
	
	public void put(int index, float[] individual, float fitness) {
		individuals.put(index, individual, fitness);
		
		if (autoSaveFolder != null) {
			save(autoSaveFolder.getAbsolutePath(), index, individual, fitness);
		}
	}

	public void updateFitness(float[] individual, float newFitness) {
		put(individual, newFitness);
	}
	
	public void evolve() {
		counter++;
		
		if (counter >= individuals.size()) {
			// Reached a full generation
			sortByFitnessDescending();
			crossoverIndividuals();
			mutateIndividuals(survivorsPerGeneration, individuals.size() - 2);
			
			counter = 0;
			saveAll();
		} else {
			saveCounter(autoSaveFolder.getAbsolutePath());
		}
	}

	public int size() {
		return individuals.size();
	}
	
	private void saveAll() {
		String folderPath = autoSaveFolder.getAbsolutePath();
		
		int i = 0;
		for (float[] individual : individuals.keyList()) {
			save(folderPath, i, individual, individuals.get(individual));
			i++;
		}
		
		saveCounter(folderPath);
	}
	
	private void saveCounter(String folderPath) {
		File file = new File(folderPath + "/Counter");
		
		try (FileOutputStream fos = new FileOutputStream(file); DataOutputStream dos = new DataOutputStream(fos)) {
			dos.writeInt(counter);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	private void loadCounter(String folderPath) {
		File file = new File(folderPath + "/Counter");
		
		try (FileInputStream fis = new FileInputStream(file); DataInputStream dis = new DataInputStream(fis)) {
			counter = dis.readInt();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private void save(float[] individual, float fitness) {
		save(autoSaveFolder.getAbsolutePath(), individuals.indexOfKey(individual), individual, fitness);
	}

	private void save(String folderPath, int index, float[] individual, float fitness) {
		File file = new File(folderPath + "/Individual" + Integer.toString(index));
		
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
				
				individuals.put(individual, fitness);
			} catch (IOException e) {
				put(spawner.get(), Float.NEGATIVE_INFINITY);
			}
		}
		
		loadCounter(folderPath);
		
		return true;
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
		Random random = ThreadLocalRandom.current();
		
		for (int i=start; i<end; i++) {
			float[] individual = individuals.getKey(i);
			mutate(individual, random);
			
			if (random.nextFloat() < resetChance) {
				individuals.put(i, spawner.get(), Float.NEGATIVE_INFINITY);
			}
		}
	}
	
	private void mutate(float[] individual, Random random) {
		// Gaussian mutation
		
		for (int i=0; i<individual.length; i++) {
			if (random.nextFloat() < mutationChance) {
				individual[i] += ((float) random.nextGaussian() * mutatorWeight) + mutatorBias;
			}
		}
	}
	
	private void crossoverIndividuals() {
		// Fitness proportionate selection
		Distribution<float[]> dist = new Distribution<>(individuals);
		
		int indexA = dist.pickIndexStochastically();
		int indexB = dist.pickIndexStochastically(indexA);

		int size = size();
		float[] a = individuals.getKey(indexA);
		float[] b = individuals.getKey(indexB);
		float[] targetA = individuals.getKey(size - 1);
		float[] targetB = individuals.getKey(size - 2);
		
		crossover(a, b, targetA, targetB, ThreadLocalRandom.current());
	}
	
	private void crossover(float[] a, float[] b, float[] targetA, float[] targetB, Random random) {
		// Single-point crossover
		int point = random.nextInt(a.length);
		
		for (int i=0; i<a.length; i++) {
			if (i < point) {
				targetA[i] = a[i];
				targetB[i] = b[i];
			} else {
				targetA[i] = b[i];
				targetB[i] = a[i];
			}
		}
	}
	
	@Override
	public String toString() {
		return "[Population] " + individuals.valueList().toString();
	}
}
