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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fwcd.sc18.utils.FloatList;
import com.fwcd.sc18.utils.IndexedHashMap;
import com.fwcd.sc18.utils.IndexedMap;

public class Population {
	private static final Logger GENETIC_LOG = LoggerFactory.getLogger("geneticlog");
	
	private final IndexedMap<float[], Float> individuals;
	private final Supplier<float[]> spawner;
	private final int survivorsPerGeneration;
	
	private File autoSaveFolder = null;
	private float mutatorWeight = 1;
	private float mutatorBias = 0;
	
	private int counter = 0;
	private int streak = 0;
	private int generation = 0;
	
	public Population(int size, Supplier<float[]> spawner, File autoSaveFolder) {
		this.autoSaveFolder = autoSaveFolder;
		survivorsPerGeneration = size / 2;
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
		int size = individuals.size();
		if (counter >= size) {
			counter = size - 1;
			saveCounter(autoSaveFolder.getAbsolutePath());
		}
		
		return individuals.getKey(counter);
	}
	
	public void put(float[] individual, float fitness) {
		individuals.put(individual, fitness);
		
		if (autoSaveFolder != null) {
			save(autoSaveFolder.getAbsolutePath(), individuals.indexOfKey(individual), individual, fitness);
		}
	}
	
	public void put(int index, float[] individual, float fitness) {
		individuals.put(index, individual, fitness);
		
		if (autoSaveFolder != null) {
			save(autoSaveFolder.getAbsolutePath(), index, individual, fitness);
		}
	}
	
	public float updateFitness(boolean won, float[] individual, float newFitness) {
		float bias = (streak > 0 ? individuals.get(individual) : 0);
		float totalFitness = bias + newFitness;
		put(counter, individual, totalFitness);
		
		return totalFitness;
	}
	
	public void evolve(boolean won) {
		boolean nextGeneration = false;
		
		if (!won && streak >= 1) {
			counter++;
			nextGeneration = (counter >= individuals.size()) && (streak >= 1);
			streak = 0;
		} else {
			streak++;
		}
		
		if (nextGeneration) {
			// Reached a full generation
			sortByFitnessDescending();
			
			counter = 0;
			streak = 0;
			generation++;
			
			GENETIC_LOG.debug("");
			GENETIC_LOG.debug(" <------------------ Generation {} ------------------> ", generation);
			GENETIC_LOG.debug("{}", this);
			GENETIC_LOG.debug("");

			copyMutate();
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
		File file = new File(folderPath + File.separator + "Counter");
		
		try (FileOutputStream fos = new FileOutputStream(file); DataOutputStream dos = new DataOutputStream(fos)) {
			dos.writeInt(counter);
			dos.writeInt(streak);
			dos.writeInt(generation);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	private boolean loadCounter(String folderPath) {
		File file = new File(folderPath + File.separator + "Counter");
		
		try (FileInputStream fis = new FileInputStream(file); DataInputStream dis = new DataInputStream(fis)) {
			counter = dis.readInt();
			streak = dis.readInt();
			generation = (dis.available() > 0 ? dis.readInt() : 0);
		} catch (IOException e) {
			return false;
		}
		
		return true;
	}

	private void save(String folderPath, int index, float[] individual, float fitness) {
		File file = new File(folderPath + File.separator + "Individual" + Integer.toString(index));
		
		try (FileOutputStream fos = new FileOutputStream(file); DataOutputStream dos = new DataOutputStream(fos)) {
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
			File file = new File(folderPath + File.separator + "Individual" + Integer.toString(index));
			
			if (!file.exists()) {
				return false;
			}
			
			try (FileInputStream fis = new FileInputStream(file); DataInputStream dis = new DataInputStream(fis)) {
				FloatList individual = new FloatList();
				float fitness = dis.readFloat();
				
				while (dis.available() > 0) {
					individual.add(dis.readFloat());
				}
				
				individuals.put(individual.toArray(), fitness);
			} catch (IOException e) {
				put(spawner.get(), Float.NEGATIVE_INFINITY);
			}
		}
		
		if (!loadCounter(folderPath)) {
			saveCounter(folderPath);
		}
		
		return true;
	}
	
	private void sortByFitnessDescending() {
		individuals.sortByValue((a, b) -> b.compareTo(a));
	}
	
	private void mutate(float[] individual, float[] target, Random random) {
		// Gaussian mutation
		
		for (int i=0; i<individual.length; i++) {
			target[i] = mutate(individual[i], random);
		}
	}
	
	private float mutate(float x, Random random) {
		return x + ((float) random.nextGaussian() * mutatorWeight) + mutatorBias;
	}
	
	private void copyMutate() {
		Random random = ThreadLocalRandom.current();
		
		for (int i=0; i<survivorsPerGeneration; i++) {
			int targetIndex = survivorsPerGeneration + i;
			float[] source = individuals.getKey(i);
			float[] target = individuals.getKey(targetIndex);
			
			mutate(source, target, random);
			individuals.setValue(targetIndex, mutate(individuals.get(source), random));
		}
	}
	
	public int getCounter() {
		return counter;
	}
	
	public int getStreak() {
		return streak;
	}
	
	@Override
	public String toString() {
		return "[Population] " + individuals.valueList().toString();
	}
}
