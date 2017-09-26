package com.thedroide.sc18.algorithmics;

import java.awt.Color;
import java.util.List;

/**
 * A generic tree node used for plotting.
 */
public interface GraphTreeNode {
	public boolean isLeaf();
	
	public List<? extends GraphTreeNode> getChildren();
	
	public String getNodeDescription();
	
	public default Color getColor() {
		return Color.BLACK;
	}
}
