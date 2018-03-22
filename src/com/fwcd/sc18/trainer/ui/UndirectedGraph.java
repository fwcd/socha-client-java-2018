package com.fwcd.sc18.trainer.ui;

import java.util.Set;

public interface UndirectedGraph<T> {
	boolean contains(T node);
	
	Set<T> getNeighbors(T node);
	
	Set<T> getNodes();
	
	void addNode(T node);
	
	void connect(T node1, T node2);
}
