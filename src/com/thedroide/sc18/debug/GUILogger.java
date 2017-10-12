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

/**
 * A graphical logger window to ease debugging when
 * launching this application through the provided
 * Software Challenge Server in absence of a proper
 * console.
 */
public class GUILogger {
	private static final boolean ENABLED = false; // When disabling permanently, remove all log() calls to increase performance
	private static final GUILogger INSTANCE = ENABLED ? new GUILogger() : null;
	
	private final JFrame view;
	private final JScrollPane scrollPane;
	private final JPanel outputArea;
	
	private List<String> output = new ArrayList<>();
	
	/**
	 * Internal singleton initializer.
	 */
	private GUILogger() {
		view = new JFrame("GUILogger");
		view.setMinimumSize(new Dimension(600, 250));
		view.setLayout(new BorderLayout());
		
		outputArea = new JPanel();
		outputArea.setLayout(new BoxLayout(outputArea, BoxLayout.Y_AXIS));
		outputArea.setBackground(Color.BLACK);
		
		scrollPane = new JScrollPane(outputArea);
		scrollPane.getVerticalScrollBar().setUnitIncrement(10);
		view.add(scrollPane);
		
		view.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		view.setVisible(true);
	}
	
	/**
	 * Prints a new line containing the given object's
	 * String representation. Input may or may not
	 * be null.
	 * 
	 * @param s - The object to be printed
	 */
	public static void log(Object s) {
		if (ENABLED) {
			INSTANCE.println(getPrefix() + (s == null ? "null" : s.toString()));
		}
	}

	/**
	 * Fetches this Thread's hashCode().
	 * 
	 * @return This Thread's hashCode()
	 */
	private static String getPrefix() {
		return "[" + Integer.toHexString(Thread.currentThread().hashCode()) + "] ";
	}
	
	/**
	 * Prints a String to a new line on this
	 * window. Input should not be null.
	 * 
	 * @param s - The String to be printed
	 */
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
