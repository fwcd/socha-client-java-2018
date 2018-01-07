package com.thedroide.sc18.testclient.gui;

import java.awt.GridBagLayout;
import java.math.BigDecimal;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import sc.shared.Score;

public class ScoreGUI {
	private final JPanel view;
	
	private final JLabel displayName;
	private final JLabel winPoints;
	private final JLabel avgCarrotLabel;
	private final JLabel avgFieldLabel;
	private final JLabel counterLabel;
	
	public ScoreGUI(String name) {
		view = new JPanel();
		view.setLayout(new GridBagLayout()); // Just for easy center alignment of the wrapper
		
		JPanel wrapper = new JPanel();
		wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
		
		displayName = getLabel(name, 24);
		wrapper.add(displayName);
		
		winPoints = getLabel("0", 48);
		wrapper.add(winPoints);

		avgFieldLabel = getLabel("-", 18);
		wrapper.add(avgFieldLabel);
		
		avgCarrotLabel = getLabel("-", 18);
		wrapper.add(avgCarrotLabel);

		counterLabel = getLabel("-", 18);
		wrapper.add(counterLabel);
		
		view.add(wrapper);
	}
	
	private BigDecimal scoreVal(Score score, int index) {
		return score.getScoreValues().get(index).getValue();
	}
	
	public void update(Score score, int currentTests, int numberOfTests) {
		winPoints.setText(scoreVal(score, 0).toBigInteger().toString());
		
		BigDecimal avgField = scoreVal(score, 1);
		BigDecimal avgCarrots = scoreVal(score, 2);
		
		avgFieldLabel.setText(
				"Avg. field: "
				+ avgField.toString()
		);
		avgCarrotLabel.setText(
				"Avg. carrots: "
				+ avgCarrots.toString()
		);
		counterLabel.setText(
				Integer.toString(currentTests)
				+ " of "
				+ Integer.toString(numberOfTests)
				+ " tests"
		);
		view.repaint();
	}
	
	private JLabel getLabel(String text, int fontSize) {
		JLabel label = new JLabel(text);
		label.setFont(label.getFont().deriveFont((float) fontSize));
		return label;
	}
	
	public JPanel getView() {
		return view;
	}
}
