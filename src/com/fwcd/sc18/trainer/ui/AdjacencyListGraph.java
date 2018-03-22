package com.fwcd.sc18.trainer.ui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class AdjacencyListGraph<T> implements UndirectedGraph<T> {
	private final Map<T, Set<T>> nodes = new HashMap<>();
	
	@Override
	public boolean contains(T node) {
		return nodes.containsKey(node);
	}

	@Override
	public Set<T> getNeighbors(T node) {
		Set<T> neighbors = nodes.get(node);
		
		if (neighbors != null) {
			return neighbors;
		} else {
			throw new NoSuchElementException("Graph does not contain node: " + node);
		}
	}

	@Override
	public Set<T> getNodes() {
		return nodes.keySet();
	}

	@Override
	public void addNode(T node) {
		nodes.put(node, new HashSet<>());
	}

	@Override
	public void connect(T node1, T node2) {
		try {
			nodes.get(node1).add(node2);
			nodes.get(node2).add(node1);
		} catch (NullPointerException e) {
			throw new NoSuchElementException("Graph does not contain at least one of the nodes: " + node1 + ", " + node2);
		}
	}
}
