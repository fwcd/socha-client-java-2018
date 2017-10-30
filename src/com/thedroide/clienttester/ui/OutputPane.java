package com.thedroide.clienttester.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.thedroide.clienttester.core.OutputLogger;

public class OutputPane implements OutputLogger {
	private static final int BUFFER_LENGTH = 50000; // In characters
	private final JPanel view;
	private final JTextArea area;
	private final JScrollPane scrollPane;
	
	public OutputPane() {
		view = new JPanel();
		view.setLayout(new BorderLayout());
		view.setPreferredSize(new Dimension(150, 150));
		
		area = new JTextArea();
		area.setForeground(Color.WHITE);
		area.setBackground(Color.BLACK);
		
		scrollPane = new JScrollPane(area);
		view.add(scrollPane, BorderLayout.CENTER);
	}
	
	private String truncate(String text) {
		if (text.length() > BUFFER_LENGTH) {
			return text.substring(text.length() - BUFFER_LENGTH);
		} else {
			return text;
		}
	}
	
	@Override
	public void log(String line) {
		if (line != null) {
			String prevText = area.getText();
			area.setText(truncate(prevText) + "\n" + line);
			
			JScrollBar bar = scrollPane.getVerticalScrollBar();
			bar.setValue(bar.getMaximum());
			
			view.repaint();
		}
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
