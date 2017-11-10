package com.thedroide.clienttester.ui;

import java.awt.Font;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.thedroide.clienttester.core.ClientJAR;

public class ClientStatsPane {
	private final JPanel view;
	private final JLabel titleLabel;
	
	private ClientJAR client;
	private int wins;
	
	public ClientStatsPane() {
		view = new JPanel();
		view.setLayout(new GridBagLayout());
		
		titleLabel = new JLabel("");
		titleLabel.setFont(new Font(titleLabel.getFont().getFontName(), Font.PLAIN, 28));
		view.add(titleLabel);
	}
	
	public void setClient(ClientJAR client) {
		this.client = client;
		update();
	}
	
	public int getWins() {
		return wins;
	}
	
	public void incrementWins() {
		wins++;
	}
	
	private void update() {
		titleLabel.setText(client.getName());
	}
	
	public JPanel getView() {
		return view;
	}
}
