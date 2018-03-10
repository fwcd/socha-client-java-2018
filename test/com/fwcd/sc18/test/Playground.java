package com.fwcd.sc18.test;

import java.awt.Color;

import javax.swing.JFrame;

import com.fwcd.sc18.trainer.ui.DataPlot;

public class Playground {
	public static void main(String[] args) {
		JFrame frame = new JFrame("TestPlot");
		frame.setSize(640, 480);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		DataPlot plot = new DataPlot("Nice", new int[] {1, 2, 84, 19, 192, 2, 34});
		plot.setStrokeColor(Color.BLUE);
		plot.setStrokeThickness(4F);
		frame.add(plot.getView());
		
		frame.setVisible(true);
	}
}
