package com.thedroide.sc18.debug;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.PrintWriter;

import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.thedroide.sc18.utils.CustomPrintWriter;

/**
 * A graphical logger window to ease debugging when
 * launching this application through the provided
 * Software Challenge Server in absence of a proper
 * console or debugging environment.
 */
public class GUILogger {
	private static final boolean ENABLED = true; // When disabling permanently, remove all log() calls to increase performance
	private static final StringBuilder QUEUE = new StringBuilder();
	private static final PrintWriter WRITER = new CustomPrintWriter(GUILogger::println);
	private static GUILogger instance = null;
	
	private final JFrame view;
	private final JScrollPane scrollPane;
	private final JTextArea outputArea;
	
	static {
		// Seperate init thread to speed up startup of the client
		new Thread(() -> {
			if (ENABLED) {
				GUILogger logger = new GUILogger();
				logger.writeln(QUEUE.toString());
				instance = logger;
			}
		}, "GUILogger initializer").start();
	}
	
	/**
	 * Internal singleton initializer.
	 */
	private GUILogger() {
		view = new JFrame("GUILogger");
		view.setMinimumSize(new Dimension(800, 400));
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
	
	private static void print(Object s) {
		if (ENABLED) {
			String out = getPrefix() + (s == null ? "null" : s.toString());
			
			if (instance == null) {
				QUEUE.append(s);
			} else {
				instance.write(out);
			}
		}
	}
	
	public static void printStack(Throwable t) {
		if (ENABLED) {
			t.printStackTrace(WRITER);
		}
	}
	
	/**
	 * Prints a new line containing the given object's
	 * String representation. Input may or may not
	 * be null.
	 * 
	 * @param s - The object to be printed
	 */
	public static void println(Object s) {
		print(s + "\n");
	}

	/**
	 * Fetches this Thread's hashCode().
	 * 
	 * @return This Thread's hashCode()
	 */
	private static String getPrefix() {
		return "[" + Integer.toHexString(Thread.currentThread().hashCode()) + "]\t";
	}
	
	private void writeln(String s) {
		write(s + "\n");
	}
	
	/**
	 * Writes a String to a new line on this
	 * window. Input should not be null.
	 * 
	 * @param s - The String to be printed
	 */
	private void write(String s) {
		outputArea.setText(outputArea.getText() + s);
		view.repaint();
		
		JScrollBar bar = scrollPane.getVerticalScrollBar();
		bar.setValue(bar.getMaximum());
	}
}
