package com.thedroide.sc18.debug;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

public class GUILogger {
	private static final boolean ENABLED = true;
	private static final GUILogger INSTANCE = ENABLED ? new GUILogger() : null;
	
	private final JFrame view;
	private final JScrollPane scrollPane;
	private final JPanel outputArea;
	
	private List<String> output = new ArrayList<>();
	
	private GUILogger() {
		view = new JFrame("GUILogger");
		view.setMinimumSize(new Dimension(600, 250));
		view.setLayout(new BorderLayout());
		
		outputArea = new JPanel();
		outputArea.setLayout(new BoxLayout(outputArea, BoxLayout.Y_AXIS));
		outputArea.setBackground(Color.BLACK);
		
		scrollPane = new JScrollPane(outputArea);
		view.add(scrollPane);
		
		view.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		view.setVisible(true);
	}
	
	public static void log(Object s) {
		if (ENABLED) {
			INSTANCE.println(getPrefix() + (s == null ? "null" : s.toString()));
		}
	}

	private static String getPrefix() {
		return "[" + Integer.toHexString(Thread.currentThread().hashCode()) + "] ";
	}
	
	private void println(String s) {
		view.repaint();
		
		for (String line : Arrays.asList(s.split("\n"))) {
			JLabel label = new JLabel(line);
			label.setForeground(Color.WHITE);
			
			outputArea.add(label);
			output.add(line);
		}
		
		JScrollBar vScrollBar = scrollPane.getVerticalScrollBar();
		vScrollBar.setValue(vScrollBar.getMaximum());
	}
}
