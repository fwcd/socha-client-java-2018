package com.thedroide.sc18.test.clientbench.utils;

import java.awt.Color;
import java.awt.Graphics2D;

public class ColoredGrid {
	private final int wCells;
	private final int hCells;
	private Color[][] bgColors;
	private Color[][] fgColors;
	private char[][] chars;
	
	public ColoredGrid(int wCells, int hCells) {
		this.wCells = wCells;
		this.hCells = hCells;
		clear();
	}

	public void clear() {
		bgColors = new Color[hCells][wCells];
		fgColors = new Color[hCells][wCells];
		chars = new char[hCells][wCells];
	}
	
	public void setBackground(int x, int y, Color color) {
		bgColors[y][x] = color;
	}
	
	public void setForeground(int x, int y, Color color) {
		fgColors[y][x] = color;
	}
	
	public void setChar(int x, int y, char c) {
		chars[y][x] = c;
	}
	
	public void render(Graphics2D g2d, int sideLength) {
		int width = sideLength / wCells;
		int height = sideLength / hCells;
		
		g2d.setFont(g2d.getFont().deriveFont((float) width));
		
		for (int y=0; y<hCells; y++) {
			for (int x=0; x<wCells; x++) {
				Color bgColor = bgColors[y][x];
				Color fgColor = fgColors[y][x];
				char c = chars[y][x];
				
				if (bgColor != null) {
					g2d.setColor(bgColor);
					g2d.fillRect(x * width, y * height, width, height);

					if (c != 0) {
						g2d.setColor(invert(bgColor));
						g2d.drawString(Character.toString(c), x * width, y * height + height);
					}
				}
				
				if (fgColor != null) {
					g2d.setColor(fgColor);
					g2d.fillOval(x * width, y * height, width, height);
				}
			}
		}
	}
	
	private Color invert(Color color) {
		return new Color(255 - color.getRed(), 255 - color.getGreen(), 255 - color.getBlue());
	}
}
