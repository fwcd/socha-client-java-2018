package com.thedroide.sc18.debug;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * A graphical logger window to ease debugging when
 * launching this application through the provided
 * Software Challenge Server in absence of a proper
 * console.
 */
public class GUILogger {
	private static final boolean ENABLED = true; // When disabling permanently, remove all log() calls to increase performance
	private static final GUILogger INSTANCE = ENABLED ? new GUILogger() : null;
	
	private final JFrame view;
	private final JScrollPane scrollPane;
	private final JTextArea outputArea;
	
	/**
	 * Internal singleton initializer.
	 */
	private GUILogger() {
		view = new JFrame("GUILogger");
		view.setMinimumSize(new Dimension(600, 250));
		view.setLayout(new BorderLayout());
		
		outputArea = new JTextArea();
		outputArea.setBackground(Color.BLACK);
		outputArea.setForeground(Color.WHITE);
		
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
		outputArea.setText(outputArea.getText() + "\n" + s);
		view.repaint();
	}
}
