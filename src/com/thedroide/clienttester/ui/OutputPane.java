package com.thedroide.clienttester.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayDeque;
import java.util.Queue;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.thedroide.clienttester.core.OutputLogger;
import com.thedroide.clienttester.utils.QueueHandler;

public class OutputPane implements OutputLogger {
	private static final int BUFFER_LENGTH = 50000; // In characters
	private final JPanel view;
	private final JTextPane area;
	private final JScrollPane scrollPane;

	private final StyledDocument doc;
	private final Style style;
	
	private final Queue<String> outQueue = new ArrayDeque<>();
	private final Thread printThread;
	
	public OutputPane() {
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
		
		printThread = new Thread(new QueueHandler<>(outQueue, this::logDirectly), "Output logging thread");
		printThread.start();
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
				doc.insertString(length, line + "\n", style);
			} catch (BadLocationException e) {
				throw new RuntimeException(e);
			}
			
			area.setCaretPosition(length);
			
			view.repaint();
		}
	}
	
	private Color getColor(String line) {
		if (line.contains("WARN")) {
			return Color.YELLOW;
		} else if (line.contains("DEBUG")) {
			return Color.CYAN;
		} else {
			return Color.WHITE;
		}
	}
	
	@Override
	public void log(String line) {
		outQueue.offer(line);
	}
	
	@Override
	public void clear() {
		area.setText("");
		view.repaint();
	}
	
	public JPanel getView() {
		return view;
	}
}
