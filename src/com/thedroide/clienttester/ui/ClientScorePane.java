package com.thedroide.clienttester.ui;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class ClientScorePane {
	private final JPanel view;
	private final JLabel scoreLabel;
	
	private String name1 = "";
	private String name2 = "";
	private int score1 = 0;
	private int score2 = 0;
	
	public ClientScorePane() {
		view = new JPanel();
		view.setLayout(new BorderLayout());
		
		scoreLabel = new JLabel("- : -");
		scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
		scoreLabel.setFont(new Font(scoreLabel.getFont().getFontName(), Font.BOLD, 42));
		view.add(scoreLabel, BorderLayout.CENTER);
	}
	
	public void setNames(String name1, String name2) {
		this.name1 = name1;
		this.name2 = name2;
		update();
	}
	
	public void update() {
		scoreLabel.setText(name1 + " " + Integer.toString(score1) + " - " + Integer.toString(score2) + " " + name2);
	}
	
	public JPanel getView() {
		return view;
	}

	public void increment1() {
		score1++;
		update();
	}
	
	public void increment2() {
		score2++;
		update();
	}
}
