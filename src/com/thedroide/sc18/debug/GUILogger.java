package com.thedroide.sc18.debug;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

public class GUILogger {
	private static final boolean ENABLED = false;
	private static final GUILogger INSTANCE = ENABLED ? new GUILogger() : null;
	
	private final JFrame view;
	private List<String> output = new ArrayList<>();
	
	private GUILogger() {
		view = new JFrame("GUILogger");
		view.setMinimumSize(new Dimension(600, 250));
		view.setLayout(new BorderLayout());
		
		JPanel outputArea = new JPanel();
		outputArea.setLayout(new BoxLayout(outputArea, BoxLayout.Y_AXIS));
		outputArea.setBackground(Color.BLACK);
		
		JScrollPane scrollPane = new JScrollPane(outputArea);
		view.add(scrollPane);
		
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				outputArea.removeAll();
				
				for (String line : output) {
					JLabel label = new JLabel(line);
					label.setForeground(Color.WHITE);
					outputArea.add(label);
				}
				
				JScrollBar vScrollBar = scrollPane.getVerticalScrollBar();
				vScrollBar.setValue(vScrollBar.getMaximum());
				
				view.revalidate();
				view.repaint();
			}
			
		}, 1000, 100);
		
		view.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		view.setVisible(true);
	}
	
	public static void log(Object s) {
		if (ENABLED) {
			INSTANCE.println(s.toString());
		}
	}
	
	private void println(String s) {
		output.addAll(Arrays.asList(s.split("\n")));
	}
}
