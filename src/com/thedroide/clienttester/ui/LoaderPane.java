package com.thedroide.clienttester.ui;

import java.awt.Color;
import java.awt.GridLayout;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.thedroide.clienttester.utils.DocChangeListener;

public class LoaderPane {
	private static final Color LIGHT_RED = new Color(0xffa5a5);
	private static final Color LIGHT_GREEN = new Color(0xa5ffa5);
	
	private final JFileChooser fileChooser;
	private File file = null;
	
	private final JPanel view;
	private final JButton selectButton;
	private final JLabel fileLabel;
	private final JTextField nameField;
	
	public LoaderPane(JFileChooser fileChooser, boolean isClient, int clientID) {
		this.fileChooser = fileChooser;
		
		// GUI
		
		view = new JPanel();
		view.setLayout(new GridLayout(3, 1));
		
		selectButton = new JButton("Select " + (isClient ? "Client " + Integer.toString(clientID) : "Server"));
		selectButton.addActionListener(l -> chooseFile());
		view.add(selectButton);
		
		nameField = new JTextField("");
		nameField.getDocument().addDocumentListener(new DocChangeListener(() -> updateNameField()));
		nameField.setBackground(LIGHT_RED);
		view.add(nameField);
		
		fileLabel = new JLabel(" - no file selected - ");
		fileLabel.setForeground(Color.GRAY);
		view.add(fileLabel);
	}
	
	/**
	 * Fetches the selected file. <b>You should always verify
	 * validAndSelected() before calling this method!</b>
	 * 
	 * @return The selected file
	 * @throws NullPointerException when no file is selected
	 */
	public File getFile() {
		return file;
	}
	
	private void chooseFile() {
		fileChooser.showOpenDialog(view);
		file = fileChooser.getSelectedFile();
		
		if (file != null) {
			update();
		}
	}
	
	private void update() {
		fileLabel.setText(file.getName());
		nameField.setText(file.getName().replaceAll("\\.jar", ""));
	}

	private void updateNameField() {
		if (validAndSelected()) {
			nameField.setBackground(LIGHT_GREEN);
		} else {
			nameField.setBackground(LIGHT_RED);
		}
	}
	
	public boolean validAndSelected() {
		return file != null && nameField.getText().length() > 0;
	}
	
	public JPanel getView() {
		return view;
	}

	public String getName() {
		return nameField.getText();
	}
}
