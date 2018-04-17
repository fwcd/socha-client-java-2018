package com.fwcd.sc18.trainer.ui;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class StatsMonitor {
	private final JPanel view;
	
	public StatsMonitor() {
		view = new JPanel();
		view.setLayout(new BoxLayout(view, BoxLayout.Y_AXIS));
	}

	public void update(PopulationMonitor population) {
		Map<String, int[]> stats = population.readStats();
		List<JPanel> panels = new ArrayList<>();
		
		for (String label : stats.keySet()) {
			int[] dataPoints = stats.get(label);
			DataPlot plot = new DataPlot(label, dataPoints);
			plot.getView().setPreferredSize(new Dimension(300, 100));
			plot.setStrokeThickness(1F);
			panels.add(plot.getView());
		}
		
		view.removeAll();
		
		for (JPanel panel : panels) {
			view.add(panel);
		}
		
		view.revalidate();
		view.repaint();
	}
	
	public JPanel getView() { return view; }
}
