package com.thedroide.sc18.testclient.utils;

import javax.swing.JFileChooser;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Hacky approach to obtain a natively-looking file chooser.
 */
public class NativeFileChooser {
	private final JFileChooser chooser;
	
	public NativeFileChooser() {
		try {
			LookAndFeel prevLAF = UIManager.getLookAndFeel();
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			chooser = new JFileChooser();
			UIManager.setLookAndFeel(prevLAF);
		} catch (ClassNotFoundException
				| InstantiationException
				| IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			throw new RuntimeException(e);
		}
	}
	
	public JFileChooser get() {
		return chooser;
	}
}
