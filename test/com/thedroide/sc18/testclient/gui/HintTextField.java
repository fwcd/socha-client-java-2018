package com.thedroide.sc18.testclient.gui;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.JTextField;

/**
 * A JTextField with a greyed-out-hint.<br><br>
 * 
 * @author https://stackoverflow.com/questions/1738966/java-jtextfield-with-input-hint
 *
 */
public class HintTextField extends JTextField {
	private static final long serialVersionUID = 1L;
    private final String hint;
    
    public HintTextField(String hint) {
    	this.hint = hint;
    }
    
    public HintTextField(String hint, int width) {
    	super(width);
        this.hint = hint;
    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (super.getText().length() == 0) {
            int h = getHeight();
            ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            Insets ins = getInsets();
            FontMetrics fm = g.getFontMetrics();
            g.setColor(Color.GRAY);
            g.drawString(hint, ins.left, h / 2 + fm.getAscent() / 2 - 2);
        }
    }
    
    @Override
	public String getText() {
    	String enteredText = super.getText();
		if (enteredText.length() == 0) {
    		return hint;
    	} else {
    		return enteredText;
    	}
    }
}
