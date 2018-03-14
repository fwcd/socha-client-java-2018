package com.fwcd.sc18.trainer.ui;

import java.awt.BorderLayout;
import java.io.File;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;

import com.fwcd.sc18.utils.MapTableModel;

public class PopulationPanel {
	private final JSplitPane view;
	private final JPanel populationView;
	private final StatsMonitor statsMonitor;
	private final ConfigPanel config;
	private final MapTableModel tableModel;
	
	private PopulationMonitor monitor;
	private Supplier<File> file;
	private Supplier<String> counterName;
	private Supplier<String> personName;
	private Supplier<String> statsName;
	private BooleanSupplier monitorWeights;
	
	public PopulationPanel(int index) {
		view = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		view.setBorder(new EmptyBorder(0, 0, 0, 0));
		
		populationView = new JPanel();
		populationView.setLayout(new BorderLayout());
		
		config = new ConfigPanel(true, false);
		file = config.addFileOption("Choose population " + Integer.toString(index) + " folder", "", new File("."), true);
		
		ConfigPanel options = config.addSubPanel("Configuration");
		counterName = options.addStringOption("Counter file name", "Counter");
		personName = options.addStringOption("Person file prefix", "Individual");
		statsName = options.addStringOption("Stats file name", "Stats");
		monitorWeights = options.addBoolOption("Monitor weights", false);

		tableModel = new MapTableModel();
		config.addButton("Verify and load", this::load);
		
		populationView.add(config.getView(), BorderLayout.NORTH);
		populationView.add(new JScrollPane(new JTable(tableModel)));
		view.setLeftComponent(populationView);
		
		statsMonitor = new StatsMonitor();
		JScrollPane statsScrollPane = new JScrollPane(statsMonitor.getView());
		JScrollBar vsb = statsScrollPane.getVerticalScrollBar();
		vsb.addAdjustmentListener(e -> statsMonitor.getView().repaint());
		vsb.setUnitIncrement(10);
		view.setRightComponent(statsScrollPane);
	}

	private void load() {
		monitor = new PopulationMonitor(
				tableModel,
				file.get().toPath(),
				counterName.get(),
				personName.get(),
				statsName.get(),
				monitorWeights.getAsBoolean()
		);
		statsMonitor.update(monitor);
	}
	
	public PopulationMonitor getMonitor() { return monitor; }
	
	public JSplitPane getView() { return view; }
	
	public void reload() {
		if (monitor != null) {
			monitor.reload();
		}
	}
}
