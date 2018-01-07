package com.thedroide.sc18.testclient.gui;

import java.awt.GridLayout;
import java.util.NoSuchElementException;

import javax.swing.JPanel;

public class DualScorePane {
	private final JPanel view;
	
	private final String displayName1;
	private final String displayName2;
	private final ScoreGUI gui1;
	private final ScoreGUI gui2;
	
	public DualScorePane(String displayName1, String displayName2) {
		view = new JPanel();
		view.setLayout(new GridLayout(1, 2));
		
		this.displayName1 = displayName1;
		this.displayName2 = displayName2;
		
		gui1 = new ScoreGUI(displayName1);
		view.add(gui1.getView());
		gui2 = new ScoreGUI(displayName2);
		view.add(gui2.getView());
	}
	
	public ScoreGUI getGUI(String displayName) {
		if (displayName1.equals(displayName)) {
			return gui1;
		} else if (displayName2.equals(displayName)) {
			return gui2;
		} else {
			throw new NoSuchElementException("No ScoreGUI associated with " + displayName);
		}
	}
	
	public JPanel getView() {
		return view;
	}
}
