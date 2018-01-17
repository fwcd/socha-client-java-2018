package com.thedroide.sc18.test.clientbench.utils;

import java.awt.Component;

import javax.swing.JOptionPane;

public class ConfigDialog {
	private final String title;
	private final ConfigPanel pane;
	private Component parent;
	private boolean accepted;
	
	public ConfigDialog(String title, ConfigPanel pane, Component parent) {
		this.title = title;
		this.pane = pane;
		this.parent = parent;
	}
	
	public void show() {
		accepted = JOptionPane.showConfirmDialog(parent, pane.getView(), title, JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION;
	}

	public boolean wasAccepted() {
		return accepted;
	}
}
