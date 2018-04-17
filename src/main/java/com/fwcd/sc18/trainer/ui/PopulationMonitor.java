package com.fwcd.sc18.trainer.ui;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

import com.fwcd.sc18.utils.EventListPoller;
import com.fwcd.sc18.utils.MapTableModel;

public class PopulationMonitor implements AutoCloseable {
	private MapTableModel table;
	
	private Path folder;
	private String counterName;
	private String statsName;
	private String individualPrefix;
	private boolean monitorWeights;
	private boolean autoUpdate;
	private Runnable onReload;
	
	private WatchService watcher;
	private Thread watchPollThread;
	
	private PopulationMonitor() {}
	
	public Map<String, int[]> readStats() {
		return readStats(false);
	}
	
	private Map<String, int[]> readStats(boolean silently) {
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
			reject("Invalid stats file.", silently);
		}
		
		return stats.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().stream()
						.mapToInt(Integer::intValue)
						.toArray()));
	}
	
	public void reload() {
		reload(false);
	}
	
	private void reload(boolean silently) {
		if (folder == null) {
			reject("No folder selected", silently);
		} else if (!Files.exists(folder)) {
			reject("Not existing on drive", silently);
		} else if (!Files.isDirectory(folder)) {
			reject("Not a folder", silently);
		} else {
			loadCounter(silently);
			loadIndividuals(silently);
			onReload.run();
		}
	}

	private void loadCounter(boolean silently) {
		Path file = folder.resolve(counterName);
		try (InputStream fis = Files.newInputStream(file); DataInputStream dis = new DataInputStream(fis)) {
			String index = "Index: " + dis.readInt();
			String streak = "Streak: " + dis.readInt();
			String gen = "Generation: " + dis.readInt();
			
			table.put("Counter", index, streak, gen);
		} catch (IOException e) {
			reject("Invalid counter file", silently);
		}
	}
	
	private void loadIndividuals(boolean silently) {
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
				reject("Invalid individual/person file: " + file.getName(), silently);
			} catch (NumberFormatException e) {
				reject("Invalid individual/person file naming: " + file.getName(), silently);
			}
		}
	}

	private void reject(String msg, boolean silently) {
		if (!silently) {
			JOptionPane.showMessageDialog(
					null,
					folder != null ? ("Population " + folder + " is not valid: " + msg) : msg,
					"Population load error",
					JOptionPane.WARNING_MESSAGE
			);
		}
	}

	@Override
	public void close() {
		try {
			if (autoUpdate) {
				watcher.close();
				watchPollThread.interrupt();
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	public static class Builder {
		private final PopulationMonitor obj = new PopulationMonitor();
		
		public Builder table(MapTableModel table) { obj.table = table; return this; }
		
		public Builder folder(Path folder) { obj.folder = folder; return this; }
		
		public Builder counterName(String counterName) { obj.counterName = counterName; return this; }
		
		public Builder personName(String personName) { obj.individualPrefix = personName; return this; }
		
		public Builder statsName(String statsName) { obj.statsName = statsName; return this; }
		
		public Builder monitorWeights(boolean monitorWeights) { obj.monitorWeights = monitorWeights; return this; }
		
		public Builder onReload(Runnable onReload) { obj.onReload = onReload; return this; }
		
		public Builder autoUpdate(boolean autoUpdate) {
			obj.autoUpdate = autoUpdate;
			try {
				if (autoUpdate) {
					obj.watcher = FileSystems.getDefault().newWatchService();
					WatchKey key = obj.folder.register(
							obj.watcher,
							StandardWatchEventKinds.ENTRY_CREATE,
							StandardWatchEventKinds.ENTRY_DELETE,
							StandardWatchEventKinds.ENTRY_MODIFY
					);
					obj.watchPollThread = new Thread(new EventListPoller<>(key::pollEvents, e -> obj.reload(true)));
					obj.watchPollThread.start();
				} else {
					obj.watcher = null;
					obj.watchPollThread = null;
				}
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
			return this;
		}
		
		public PopulationMonitor build() {
			obj.table.clear();
			obj.reload();
			return obj;
		}
	}
}
