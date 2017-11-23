package com.thedroide.sc18.debug;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * A graphical logger window to ease debugging when
 * launching this application through the provided
 * Software Challenge Server in absence of a proper
 * console or debugging environment.
 */
public class GUILogger {
	private static final boolean ENABLED = true; // When disabling permanently, remove all log() calls to increase performance
	private static final StringBuilder QUEUE = new StringBuilder();
	private static final PrintWriter WRITER;
	private static GUILogger instance = null;
	
	private final JFrame view;
	private final JScrollPane scrollPane;
	private final JTextArea outputArea;
	
	static {
		WRITER = new PrintWriter(new ByteArrayOutputStream(0)) {

			@Override
			public void write(String s) {
				log(s);
			}

			@Override
			public void write(char[] buf, int off, int len) {
				log(new String(buf));
			}

			@Override
			public void write(char[] buf) {
				log(new String(buf));
			}

			@Override
			public void write(String s, int off, int len) {
				log(s);
			}
			
		};
		
		// Seperate init thread to speed up startup of the client
		new Thread(() -> {
			if (ENABLED) {
				GUILogger logger = new GUILogger();
				logger.println(QUEUE.toString());
				instance = logger;
			}
		}, "GUILogger initializer").start();
	}
	
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

	public static PrintWriter getWriter() {
		return WRITER;
	}
	
	/**
	 * Prints a new line containing the given object's
	 * String representation. Input may or may not
	 * be null.
	 * 
	 * @param s - The object to be printed
	 */
	public static void log(Object s) {
		String out = getPrefix() + (s == null ? "null" : s.toString());
		
		if (ENABLED) {
			if (instance == null) {
				QUEUE.append(s + "\n");
			} else {
				instance.println(out);
			}
		}
	}

	/**
	 * Fetches this Thread's hashCode().
	 * 
	 * @return This Thread's hashCode()
	 */
	private static String getPrefix() {
		return "[" + Integer.toHexString(Thread.currentThread().hashCode()) + "]\t";
	}
	
	/**
	 * Prints a String to a new line on this
	 * window. Input should not be null.
	 * 
	 * @param s - The String to be printed
	 */
	private void println(String s) {
		outputArea.setText(outputArea.getText() + "\n" + s);
		
		JScrollBar bar = scrollPane.getVerticalScrollBar();
		bar.setValue(bar.getMaximum());
		
		view.repaint();
	}
}
