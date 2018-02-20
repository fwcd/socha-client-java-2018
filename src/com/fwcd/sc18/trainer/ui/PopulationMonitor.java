package com.fwcd.sc18.trainer.ui;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.JOptionPane;

import com.fwcd.sc18.trainer.utils.MapTableModel;

public class PopulationMonitor {
	private final MapTableModel table;
	
	private final File folder;
	private final String counterName;
	private final String personName;
	private final boolean useAntonsFormat;
	private final boolean monitorWeights;
	
	public PopulationMonitor(
			MapTableModel table,
			File folder,
			String counterName,
			String personName,
			boolean useAntonsFormat,
			boolean monitorWeights
	) {
		this.table = table;
		this.folder = folder;
		this.counterName = counterName;
		this.personName = personName;
		this.useAntonsFormat = useAntonsFormat;
		this.monitorWeights = monitorWeights;
		
		table.clear();
		reload();
	}
	
	public void reload() {
		if (folder == null) {
			reject("No folder selected");
		} else if (!folder.exists()) {
			reject("Not existing on drive");
		} else if (!folder.isDirectory()) {
			reject("Not a folder");
		} else {
			loadCounter();
			loadIndividuals();
		}
	}

	private void loadCounter() {
		File file = new File(folder.getAbsolutePath() + File.separator + counterName);
		try (FileInputStream fis = new FileInputStream(file); DataInputStream dis = new DataInputStream(fis)) {
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
		for (File file : folder.listFiles(file -> file.getName().startsWith(personName))) {
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
				folder != null ? ("Population " + folder.getAbsolutePath() + " is not valid: " + msg) : msg,
				"Population load error",
				JOptionPane.WARNING_MESSAGE
		);
	}
}
