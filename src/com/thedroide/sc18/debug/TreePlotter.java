package com.thedroide.sc18.debug;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.thedroide.sc18.algorithmics.GraphTreeNode;

public class TreePlotter {
	private JFrame view;
	private GraphTreeNode treeRoot;
	
	public TreePlotter(GraphTreeNode treeRoot) {
		this.treeRoot = treeRoot;
		
		view = new JFrame("GraphTreePlotter");
		view.setSize(900, 400);
		view.setLayout(new BorderLayout());
		
		view.add(new JPanel() {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				render((Graphics2D) g);
			}
		}, BorderLayout.CENTER);
		
		view.setVisible(true);
	}
	
	private int centerX() {
		return view.getWidth() / 2;
	}
	
	private void render(Graphics2D g2d) {
		render(g2d, treeRoot, centerX(), 20, 0);
	}
	
	private void render(Graphics2D g2d, GraphTreeNode node, int x, int y, int incrementalDepth) {
		String nodeDesc = node.getNodeDescription();
		FontMetrics metrics = g2d.getFontMetrics();
		
		int topLeftX = x - (metrics.stringWidth(nodeDesc) / 2);
		int topLeftY = y - (metrics.getHeight() / 2);
		
		g2d.setColor(node.getColor());
		g2d.drawString(nodeDesc, topLeftX, topLeftY);
		
		int children = node.getChildren().size() - 1;
		int step = (int) ((1D / (incrementalDepth + 1)) * 50D);
		
		int i = 0;
		for (GraphTreeNode child : node.getChildren()) {
			int childX = x - ((children * step) / 2) + (i * step);
			int childY = y + metrics.getHeight() * 3;
			
			g2d.setColor(Color.GRAY);
			g2d.drawLine(x, y, childX, childY);
			
			render(g2d, child, childX, childY, incrementalDepth + 1);
			
			i++;
		}
	}
}
