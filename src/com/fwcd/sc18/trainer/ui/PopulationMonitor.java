package com.fwcd.sc18.trainer.ui;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

import com.fwcd.sc18.utils.MapTableModel;

public class PopulationMonitor {
	private final MapTableModel table;
	
	private final Path folder;
	private final String counterName;
	private final String statsName;
	private final String individualPrefix;
	private final boolean useAntonsFormat;
	private final boolean monitorWeights;
	
	public PopulationMonitor(
			MapTableModel table,
			Path folder,
			String counterName,
			String personName,
			String statsName,
			boolean useAntonsFormat,
			boolean monitorWeights
	) {
		this.table = table;
		this.folder = folder;
		this.counterName = counterName;
		this.statsName = statsName;
		this.individualPrefix = personName;
		this.useAntonsFormat = useAntonsFormat;
		this.monitorWeights = monitorWeights;
		
		table.clear();
		reload();
	}
	
	public Map<String, int[]> readStats() {
		Map<String, List<Integer>> stats = new HashMap<>();
		
		List<Integer> wins = new ArrayList<>();
		List<Integer> goalWins = new ArrayList<>();
		List<Integer> maxFitness = new ArrayList<>();
		List<Integer> losses = new ArrayList<>();
		List<Integer> minGoalMoves = new ArrayList<>();
		List<Integer> maxGoalMoves = new ArrayList<>();
		List<Integer> maxStreak = new ArrayList<>();
		
		stats.put("wins", wins);
		stats.put("goalWins", goalWins);
		stats.put("maxFitness", maxFitness);
		stats.put("losses", losses);
		stats.put("minGoalMoves", minGoalMoves);
		stats.put("maxGoalMoves", maxGoalMoves);
		stats.put("maxStreak", maxStreak);
		
		try (InputStream fis = Files.newInputStream(folder.resolve(statsName)); DataInputStream dis = new DataInputStream(fis)) {
			while (dis.available() > 0) {
				wins.add(dis.readInt());
				goalWins.add(dis.readInt());
				maxFitness.add(dis.readInt());
				losses.add(dis.readInt());
				minGoalMoves.add(dis.readInt());
				maxGoalMoves.add(dis.readInt());
				maxStreak.add(dis.readInt());
			}
		} catch (EOFException e) {
			// Do nothing
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		
		return stats.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().stream()
						.mapToInt(Integer::intValue)
						.toArray()));
	}
	
	public void reload() {
		if (folder == null) {
			reject("No folder selected");
		} else if (!Files.exists(folder)) {
			reject("Not existing on drive");
		} else if (!Files.isDirectory(folder)) {
			reject("Not a folder");
		} else {
			loadCounter();
			loadIndividuals();
		}
	}

	private void loadCounter() {
		Path file = folder.resolve(counterName);
		try (InputStream fis = Files.newInputStream(file); DataInputStream dis = new DataInputStream(fis)) {
			String index = "";
			String streak = "";
			String gen = "";
			
			if (useAntonsFormat) {
				index = "Index: " + dis.readFloat();
				streak = "Streak: " + dis.readFloat();
			} else {
				index = "Index: " + dis.readInt();
				streak = "Streak: " + dis.readInt();
				gen = "Generation: " + dis.readInt();
			}
			
			table.put("Counter", index, streak, gen);
		} catch (IOException e) {
			reject("Invalid counter file");
		}
	}
	
	private void loadIndividuals() {
		for (File file : folder.toFile().listFiles(file -> file.getName().startsWith(individualPrefix))) {
			try (FileInputStream fis = new FileInputStream(file); DataInputStream dis = new DataInputStream(fis)) {
				String fitness = "Fitness: " + dis.readFloat();
				
				if (monitorWeights) {
					StringBuilder weights = new StringBuilder("Weights: [");
					
					while (dis.available() > 0) {
						weights.append(dis.readFloat()).append(", ");
					}
					
					weights.delete(weights.length() - 2, weights.length()).append(']');
					table.put(file.getName(), fitness, weights.toString());
				} else {
					table.put(file.getName(), fitness);
				}
			} catch (IOException e) {
				reject("Invalid individual/person file: " + file.getName());
			} catch (NumberFormatException e) {
				reject("Invalid individual/person file naming: " + file.getName());
			}
		}
	}

	private void reject(String msg) {
		JOptionPane.showMessageDialog(
				null,
				folder != null ? ("Population " + folder + " is not valid: " + msg) : msg,
				"Population load error",
				JOptionPane.WARNING_MESSAGE
		);
	}
}
