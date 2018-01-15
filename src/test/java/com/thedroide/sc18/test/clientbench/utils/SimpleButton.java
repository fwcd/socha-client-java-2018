package com.thedroide.sc18.test.clientbench.utils;

import javax.swing.JButton;

public class SimpleButton extends JButton {
	private static final long serialVersionUID = 1L;

	public SimpleButton(String text, Runnable onClick) {
		super(text);
		addActionListener(l -> onClick.run());
	}
}
