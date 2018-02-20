package com.fwcd.sc18.trainer.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.File;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.fwcd.sc18.trainer.utils.MapTableModel;

public class PopulationPanel {
	private final JPanel view;
	private final ConfigPanel config;
	private final MapTableModel tableModel;
	
	private PopulationMonitor monitor;
	
	public PopulationPanel(int index) {
		view = new JPanel();
		view.setLayout(new BorderLayout());
		
		config = new ConfigPanel(true, false);
		Supplier<File> file = config.addFileOption("Choose population " + Integer.toString(index) + " folder", "", new File("."), true);
		
		ConfigPanel options = config.addSubPanel("Configuration");
		Supplier<String> counterName = options.addStringOption("Counter file name", "Counter");
		Supplier<String> personName = options.addStringOption("Person file prefix", "Individual");
		BooleanSupplier monitorWeights = options.addBoolOption("Monitor weights", false);
		BooleanSupplier useAntonsFormat = options.addBoolOption("Use Anton's population format", false);

		tableModel = new MapTableModel();
		config.addButton("Verify and load", () -> monitor = new PopulationMonitor(
				tableModel,
				file.get(),
				counterName.get(),
				personName.get(),
				useAntonsFormat.getAsBoolean(),
				monitorWeights.getAsBoolean()
		));
		
		view.add(config.getView(), BorderLayout.NORTH);
		view.add(new JScrollPane(new JTable(tableModel)));
	}
	
	public Component getView() { return view; }
	
	public void reload() {
		if (monitor != null) {
			monitor.reload();
		}
	}
}
