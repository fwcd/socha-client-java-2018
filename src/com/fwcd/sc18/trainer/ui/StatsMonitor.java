package com.fwcd.sc18.trainer.ui;

import java.awt.Dimension;
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
		view.removeAll();
		Map<String, int[]> stats = population.readStats();
		
		for (String label : stats.keySet()) {
			int[] dataPoints = stats.get(label);
			DataPlot plot = new DataPlot(label, dataPoints);
			plot.getView().setPreferredSize(new Dimension(300, 100));
			plot.setStrokeThickness(1F);
			view.add(plot.getView());
		}
		
		view.revalidate();
		view.repaint();
	}
	
	public JPanel getView() { return view; }
}
