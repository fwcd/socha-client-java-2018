package fwcd.sc18.utils;

import java.awt.Color;
import java.util.List;

/**
 * A generic tree node used for plotting.
 */
public interface TreeNode {
	boolean isLeaf();
	
	/**
	 * Fetches the children of this node.
	 * 
	 * @return A list of child nodes
	 */
	List<? extends TreeNode> getChildren();
	
	/**
	 * Fetches the description of this node. Subclasses are forced
	 * to implement this "toString()"-like method to ensure a
	 * proper String representation. It makes sense to delegate
	 * toString() to this method.
	 * 
	 * @return A String representation of this node
	 */
	String getNodeDescription();
	
	/**
	 * Returns a color of this node used for plotting. Defaults
	 * to black to may be used to highlight paths in the tree
	 * (for example).
	 * 
	 * @return The color of this node
	 */
	default Color getColor() {
		return Color.BLACK;
	}
}
