package fwcd.sc18.trainer.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Arrays;

import javax.swing.JPanel;

public class DataPlot {
	private final JPanel view;
	private final int[] dataPoints;
	private final double[] normalizedPoints;
	private final String name;
	
	private int padding = 15;
	private float strokeThickness = 2;
	private Color strokeColor = Color.BLUE;
	
	public DataPlot(String name, int[] dataPoints) {
		this.dataPoints = dataPoints;
		this.name = name;
		normalizedPoints = getNormalizedPoints();
		view = new JPanel() {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void paintComponent(Graphics g) { render((Graphics2D) g, getSize()); }
		};
	}
	
	private double normalize(double x, double min, double max) {
		return (x - min) / (max - min);
	}
	
	private void render(Graphics2D g2d, Dimension canvasSize) {
		g2d.setStroke(new BasicStroke(2F));
		g2d.setColor(Color.DARK_GRAY);
		
		// Bounding box
		int bbX = padding;
		int bbY = padding;
		int bbW = (int) canvasSize.getWidth() - (padding * 2);
		int bbH = (int) canvasSize.getHeight() - (padding * 2);
		
		// Draw y-axis
		g2d.drawLine(bbX, bbY, bbX, bbY + bbH);
		g2d.drawLine(bbX, bbY, bbX - (padding / 2), bbY + (padding / 2));
		g2d.drawLine(bbX, bbY, bbX + (padding / 2), bbY + (padding / 2));
		
		// Draw x-axis
		g2d.drawLine(bbX, bbY + bbH, bbX + bbW, bbY + bbH);
		g2d.drawLine(bbX + bbW, bbY + bbH, bbX + bbW - (padding / 2), bbY + bbH - (padding / 2));
		g2d.drawLine(bbX + bbW, bbY + bbH, bbX + bbW - (padding / 2), bbY + bbH + (padding / 2));
		
		// Draw linearly interpolated points
		double dx = bbW / (double) normalizedPoints.length;

		double lastX;
		double lastY;
		double startY = bbY + bbH;
		double x = bbX;
		double y = startY;

		g2d.setStroke(new BasicStroke(strokeThickness));
		g2d.setColor(strokeColor);
		
		for (double normalizedPoint : normalizedPoints) {
			lastX = x;
			lastY = y;
			x += dx;
			y = startY - (normalizedPoint * bbH);
			
			g2d.drawLine((int) lastX, (int) lastY, (int) x, (int) y);
		}
		
		g2d.setColor(Color.BLACK);
		g2d.setFont(g2d.getFont().deriveFont(16F));
		g2d.drawString(name, bbX + padding, bbY + padding);
	}
	
	public void setStrokeThickness(float thickness) { strokeThickness = thickness; }
	
	public void setStrokeColor(Color color) { strokeColor = color; }
	
	private double[] getNormalizedPoints() {
		int currentMin = Integer.MAX_VALUE;
		int currentMax = Integer.MIN_VALUE;
		
		for (int point : dataPoints) {
			currentMin = Math.min(point, currentMin);
			currentMax = Math.max(point, currentMax);
		}
		
		final int min = currentMin;
		final int max = currentMax;
		double[] normalizedPoints = Arrays.stream(dataPoints)
				.mapToDouble(v -> normalize(v, min, max))
				.toArray();
		return normalizedPoints;
	}
	
	public JPanel getView() { return view; }
}
