package com.thedroide.clienttester.utils;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * A class to reduce boilerplate when attaching change listeners
 * to text (swing) components.
 */
public class DocChangeListener implements DocumentListener {
	private final Runnable onRun;
	
	public DocChangeListener(Runnable onRun) {
		this.onRun = onRun;
	}
	
	@Override
	public void insertUpdate(DocumentEvent e) {
		changedUpdate(e);
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		changedUpdate(e);
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		onRun.run();
	}
}
