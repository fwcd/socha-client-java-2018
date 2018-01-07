package com.thedroide.sc18.testclient.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Executors;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.slf4j.Logger;

import com.thedroide.sc18.testclient.utils.QueueHandler;
import com.thedroide.sc18.testclient.utils.SimpleLogger;

public class ConsolePanel {
	private static final int BUFFER_LENGTH = 50000; // In characters
	private final JPanel view;
	private final JTextPane area;
	private final JScrollPane scrollPane;
	
	private final Logger logger;
	private final StyledDocument doc;
	private final Style style;
	
	private final Queue<String> outQueue = new ArrayDeque<>();
	
	public ConsolePanel() {
		view = new JPanel();
		view.setLayout(new BorderLayout());
		view.setPreferredSize(new Dimension(150, 150));
		
		area = new JTextPane();
		area.setForeground(Color.WHITE);
		area.setBackground(Color.BLACK);
		
		doc = area.getStyledDocument();
		style = doc.addStyle("ColoredLog", null);
		
		scrollPane = new JScrollPane(area);
		view.add(scrollPane, BorderLayout.CENTER);
		
		logger = new SimpleLogger(this::print);
		
		Executors.newSingleThreadExecutor().execute(new QueueHandler<>(outQueue, this::logDirectly));
	}
	
	public Logger asLogger() {
		return logger;
	}
	
	private void truncate() {
		if (doc.getLength() > BUFFER_LENGTH) {
			try {
				doc.remove(0, 5);
			} catch (BadLocationException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	private void logDirectly(String line) {
		if (line != null) {
			truncate();
			int length = doc.getLength();
			
			StyleConstants.setForeground(style, getColor(line));
			try {
				doc.insertString(length, line, style);
			} catch (BadLocationException e) {
				throw new RuntimeException(e);
			}
			
			area.setCaretPosition(length);
			
			view.repaint();
		}
	}
	
	private Color getColor(String line) {
		if (line.contains("ERROR")) {
			return Color.RED;
		} else if (line.contains("WARN")) {
			return Color.YELLOW;
		} else if (line.contains("DEBUG")) {
			return Color.CYAN;
		} else if (line.contains("TRACE")) {
			return Color.GREEN;
		} else {
			return Color.WHITE;
		}
	}
	
	public void print(String s) {
		outQueue.offer(s);
	}
	
	public void println(String s) {
		outQueue.offer(s + "\n");
	}
	
	public void clear() {
		area.setText("");
		view.repaint();
	}
	
	public JPanel getView() {
		return view;
	}

	public void setHeight(int px) {
		Dimension dimension = new Dimension(px, px);
		view.setPreferredSize(dimension);
		view.setMinimumSize(dimension);
	}
}
