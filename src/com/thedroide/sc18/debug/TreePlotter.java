package com.thedroide.sc18.debug;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.thedroide.sc18.algorithmics.GraphTreeNode;

/**
 * A graphical tree plotter using Swing.
 */
public class TreePlotter {
	private JFrame view;
	private GraphTreeNode treeRoot;
	
	/**
	 * Constructs a new, empty TreePlotter.
	 */
	public TreePlotter() {
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
		
		view.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		view.setVisible(true);
	}
	
	/**
	 * Changes and repaints the tree used by this plotter.
	 * 
	 * @param treeRoot - The new tree
	 */
	public void setTree(GraphTreeNode treeRoot) {
		this.treeRoot = treeRoot;
		view.repaint();
	}
	
	/**
	 * Fetches the center x-position of the window.
	 * 
	 * @return The center x-position
	 */
	private int centerX() {
		return view.getWidth() / 2;
	}
	
	/**
	 * Renders the tree.
	 * 
	 * @param g2d - The graphics context the tree will be rendered in
	 */
	private void render(Graphics2D g2d) {
		if (treeRoot != null) {
			render(g2d, treeRoot, centerX(), 20, 0);
		}
	}
	
	/**
	 * Internal, recursive tree drawing method.
	 * 
	 * @param g2d - The graphics context
	 * @param node - The node to be rendered
	 * @param x - The start x-position
	 * @param y - The start y-position
	 * @param incrementalDepth - The current depth of the tree
	 */
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
