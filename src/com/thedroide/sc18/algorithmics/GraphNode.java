package com.thedroide.sc18.algorithmics;

import java.util.List;

/**
 * A generic tree node used for plotting.
 */
public interface GraphNode {
	public boolean isLeaf();
	
	public List<? extends GraphNode> getChildren();
	
	public String getNodeDescription();
}
