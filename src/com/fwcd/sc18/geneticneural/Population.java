package com.fwcd.sc18.geneticneural;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fwcd.sc18.exception.CorruptedDataException;
import com.fwcd.sc18.utils.FloatList;
import com.fwcd.sc18.utils.IndexedHashMap;
import com.fwcd.sc18.utils.IndexedMap;

/**
 * A population that uses genetic techniques to
 * find optimize a solution.
 */
public class Population {
	private static final Logger GENETIC_LOG = LoggerFactory.getLogger("geneticlog");
	
	private final IndexedMap<float[], Float> individuals;
	private final Supplier<float[]> spawner;
	private final int survivorsPerGeneration;
	private final boolean trainMode;
	
	private Path savePath = null;
	private float mutatorWeight = 1F;
	private float mutatorBias = 0;
	
	private int counter = 0;
	private int streak = 0;
	private int generation = 0;
	
	private int wins = 0;
	private int goalWins = 0;
	private int losses = 0;
	private int minGoalMoves = Integer.MAX_VALUE;
	private int maxGoalMoves = Integer.MIN_VALUE;
	private int longestStreak = 0;
	
	/**
	 * Constructs a new population with the given hyperparameters. This
	 * method will try to load an exisiting population from the given
	 * folder and otherwise create a new one.
	 * 
	 * @param size - The amount of individuals in the new population
	 * @param spawner - A supply of new individuals
	 * @param savePath - The folder to which individuals will be serialized
	 * @param trainMode - Whether the population should be created/loaded in training mode
	 */
	public Population(int size, Supplier<float[]> spawner, Path savePath, boolean trainMode) {
		this.trainMode = trainMode;
		this.savePath = savePath;
		survivorsPerGeneration = size / 2;
		individuals = new IndexedHashMap<>();
		this.spawner = spawner;
		
		if (!loadAll(size)) {
			individuals.clear();
			Float initialFitness = Float.NEGATIVE_INFINITY;
			
			for (int i=0; i<size; i++) {
				float[] individual = spawner.get();
				
				put(individual, initialFitness);
			}
		}
	}
	
	/**
	 * Samples an individual from this population depending on the trainMode.
	 */
	public float[] sample() {
		return trainMode ? selectCurrentGenes() : selectFittestGenes();
	}
	
	/**
	 * Selects the current individual for training.
	 */
	private float[] selectCurrentGenes() {
		int size = individuals.size();
		
		if (counter < 0 || counter >= size) {
			counter = 0;
		}
		
		return individuals.getKey(counter);
	}
	
	/**
	 * "Greedily" selects the individual with the highest (saved) fitness value.
	 */
	private float[] selectFittestGenes() {
		float bestFitness = Float.NEGATIVE_INFINITY;
		float[] bestIndividual = null;
		
		for (float[] individual : individuals.keySet()) {
			float fitness = individuals.get(individual);
			
			if (fitness > bestFitness) {
				bestFitness = fitness;
				bestIndividual = individual;
			}
		}
		
		if (bestIndividual == null) {
			throw new NoSuchElementException("Couldn't sample from an empty population");
		} else {
			return bestIndividual;
		}
	}
	
	/**
	 * Adds/replaces the given individual and it's associated fitness.
	 */
	public void put(float[] individual, float fitness) {
		individuals.put(individual, fitness);
	}
	
	/**
	 * Adds/replaces the given individual and it's associated fitness
	 * at the given index.
	 */
	public void put(int index, float[] individual, float fitness) {
		individuals.put(index, individual, fitness);
	}
	
	/**
	 * Updates the given individual's fitness. It will increment or
	 * replace, depending on whether the game has been won or not.
	 */
	public float updateFitness(boolean won, float[] individual, float newFitness) {
		float bias = (streak > 0 ? individuals.get(individual) : 0);
		float totalFitness = bias + newFitness;
		put(counter, individual, totalFitness);
		
		return totalFitness;
	}
	
	/**
	 * Evolves this population. If all individuals have been tested,
	 * a new generation has been reached and mutation/crossover will
	 * be performed.
	 * 
	 * <p><b>This method only affects the population if trainMode is set to true.</b></p>
	 */
	public void evolve(boolean won, boolean inGoal, int moves) {
		if (trainMode) {
			boolean nextGeneration = false;
			
			if (!won && streak >= 1) {
				counter++;
				nextGeneration = (counter >= individuals.size()) && (streak >= 1);
				longestStreak = Math.max(longestStreak, streak);
				streak = 0;
			} else {
				streak++;
			}
			
			if (won) {
				if (inGoal) {
					minGoalMoves = Math.min(moves, minGoalMoves);
					maxGoalMoves = Math.max(moves, maxGoalMoves);
					goalWins++;
				} else {
					wins++;
				}
			} else {
				losses++;
			}
			
			if (nextGeneration) {
				// Reached a full generation
				sortByFitnessDescending();
				
				counter = 0;
				streak = 0;
				generation++;
				
				log();

				wins = 0;
				losses = 0;
				goalWins = 0;
				longestStreak = 0;
				minGoalMoves = Integer.MAX_VALUE;
				maxGoalMoves = Integer.MIN_VALUE;
				
				copyMutate();
				saveAll();
			}
		}
	}
	
	private void log() {
		GENETIC_LOG.info("");
		GENETIC_LOG.info(" <------------------ Generation {} ------------------> ", generation);
		GENETIC_LOG.info("{}", this);
		GENETIC_LOG.info("{} wins, {} goal wins, {} losses", new Object[] {wins, goalWins, losses});
		GENETIC_LOG.info("Min goal moves: {}, Max goal moves: {}, longest streak: {}", new Object[] {minGoalMoves, maxGoalMoves, longestStreak});
		GENETIC_LOG.info("");
	}
	
	/**
	 * @return The amount of individuals in this population.
	 */
	public int size() {
		return individuals.size();
	}
	
	private void saveCounter() {
		Path file = savePath.resolve("Counter");
		
		try (OutputStream fos = Files.newOutputStream(file); DataOutputStream dos = new DataOutputStream(fos)) {
			dos.writeInt(counter);
			dos.writeInt(streak);
			dos.writeInt(generation);
			dos.writeInt(wins);
			dos.writeInt(goalWins);
			dos.writeInt(losses);
			dos.writeInt(minGoalMoves);
			dos.writeInt(maxGoalMoves);
			dos.writeInt(longestStreak);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	private boolean loadCounter() {
		Path file = savePath.resolve("Counter");
		
		try (InputStream fis = Files.newInputStream(file); DataInputStream dis = new DataInputStream(fis)) {
			counter = dis.readInt();
			streak = dis.readInt();
			generation = (dis.available() > 0 ? dis.readInt() : 0);
			wins = (dis.available() > 0 ? dis.readInt() : 0);
			goalWins = (dis.available() > 0 ? dis.readInt() : 0);
			losses = (dis.available() > 0 ? dis.readInt() : 0);
			minGoalMoves = (dis.available() > 0 ? dis.readInt() : 0);
			maxGoalMoves = (dis.available() > 0 ? dis.readInt() : 0);
			longestStreak = (dis.available() > 0 ? dis.readInt() : 0);
		} catch (IOException e) {
			return false;
		}
		
		return true;
	}

	private void save(int index, float[] individual, float fitness) {
		Path file = savePath.resolve("Individual" + index);
		
		try (OutputStream fos = Files.newOutputStream(file); DataOutputStream dos = new DataOutputStream(fos)) {
			dos.writeFloat(fitness);
			for (float gene : individual) {
				dos.writeFloat(gene);
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private void saveAll() {
		int i = 0;
		for (float[] individual : individuals.keyList()) {
			save(i, individual, individuals.get(individual));
			i++;
		}
		
		saveCounter();
		
		if (generation > 200 && generation % 100 == 0) {
			createBackup(i);
		}
	}
	
	private void createBackup(int size) {
		Path backupPath = savePath.resolve("Backup");
		
		try {
			if (!Files.exists(backupPath)) {
				Files.createDirectory(backupPath);
			}
			
			Files.copy(
					savePath.resolve("Counter"),
					backupPath.resolve("Counter"),
					StandardCopyOption.REPLACE_EXISTING
			);
			
			for (int i=0; i<size; i++) {
				Files.copy(
						savePath.resolve("Individual" + i),
						backupPath.resolve("Individual" + i),
						StandardCopyOption.REPLACE_EXISTING
				);
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private boolean loadAll(int total) {
		for (int index=0; index<total; index++) {
			Path file = savePath.resolve("Individual" + index);
			
			if (!Files.exists(file)) {
				return false;
			}
			
			try (InputStream fis = Files.newInputStream(file); DataInputStream dis = new DataInputStream(fis)) {
				FloatList individual = new FloatList();
				float fitness = dis.readFloat();
				
				while (dis.available() > 0) {
					individual.add(dis.readFloat());
				}
				
				individuals.put(index, individual.toArray(), fitness);
			} catch (IOException e) {
				put(index, spawner.get(), Float.NEGATIVE_INFINITY);
			}
		}
		
		if (!loadCounter()) {
			saveCounter();
		}
		
		return true;
	}
	
	private void sortByFitnessDescending() {
		individuals.sortByValue((a, b) -> b.compareTo(a));
	}
	
	private void mutate(float[] individual, float[] target, int individualIndex, int targetIndex, Random random) {
		// Gaussian mutation
		
		for (int i=0; i<individual.length; i++) {
			float mutated;
			try {
				mutated = mutate(individual[i], random);
			} catch (ArrayIndexOutOfBoundsException e) {
				throw new CorruptedDataException(individualIndex);
			}
			
			try {
				target[i] = mutated;
			} catch (ArrayIndexOutOfBoundsException e) {
				throw new CorruptedDataException(targetIndex);
			}
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
			
			mutate(source, target, i, targetIndex, random);
			individuals.setValue(targetIndex, Float.NEGATIVE_INFINITY);
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
