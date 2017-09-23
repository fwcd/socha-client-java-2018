package com.thedroide.sc18.minimax;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveAction;

import com.thedroide.sc18.algorithmics.GraphTreeNode;
import com.thedroide.sc18.algorithmics.Rating;
import com.thedroide.sc18.algorithmics.Strategy;

import sc.plugin2018.Action;
import sc.plugin2018.GameState;
import sc.plugin2018.Move;
import sc.shared.InvalidMoveException;

/**
 * A tree of possible board states using recursive
 * fork/join computation.
 */
public class MinimaxBoardState extends RecursiveAction implements GraphTreeNode, Comparable<MinimaxBoardState> {
	private static final long serialVersionUID = 1L;

	private MinimaxBoardState parent = null;
	private List<MinimaxBoardState> children = new ArrayList<>();
	private MinimaxBoardState bestChild = null;
	private Rating rating = null;
	
	private final boolean maximize;
	private final GameState state;
	private final Strategy<MinimaxBoardState> strategy;
	private final int depth;
	private final Move lastMove;
	
	public MinimaxBoardState(GameState state, Strategy<MinimaxBoardState> strategy, int depth) {
		this(state, strategy, true, depth, null, null);
	}
	
	public MinimaxBoardState(
			GameState state,
			Strategy<MinimaxBoardState> strategy,
			boolean maximize,
			int depth,
			Move lastMove,
			MinimaxBoardState parent) {
		this.state = state;
		this.strategy = strategy;
		this.maximize = maximize;
		this.depth = depth;
		this.lastMove = lastMove;
		this.parent = parent;
	}
	
	public GameState getState() {
		return state;
	}
	
	public List<Move> getAvailableMoves() {
		return state.getPossibleMoves();
	}
	
	public Move getLastMove() {
		return lastMove;
	}
	
	@Override
	protected void compute() {
		if (!isLeaf()) {
			for (Move move : getAvailableMoves()) {
				try {
					GameState nextState = state.clone();
					
					for (Action action : move.getActions()) {
						action.perform(nextState);
					}
					
					nextState.switchCurrentPlayer();
					MinimaxBoardState child = new MinimaxBoardState(nextState, strategy, !maximize, depth - 1, move, this);
					children.add(child);
					child.quietlyInvoke();
				} catch (CloneNotSupportedException | InvalidMoveException e) {
					e.printStackTrace();
				}
			}
			
			minimax();
		} else {
			rating = strategy.evaluate(this);
		}
	}
	
	private void minimax() {
		if (maximize) {
			maximize();
		} else {
			minimize();
		}
	}
	
	@Override
	public int compareTo(MinimaxBoardState o) {
		return rating.compareTo(o.rating);
	}
	
	@Override
	public boolean isLeaf() {
		return depth == 0;
	}
	
	@Override
	public List<? extends GraphTreeNode> getChildren() {
		return children;
	}
	
	@Override
	public String getNodeDescription() {
		return "R" + Integer.toString(state.getRedPlayer().getFieldIndex()) + "|"
				+ "B" + Integer.toString(state.getBluePlayer().getFieldIndex());
	}
	
	private void maximize() {
		for (MinimaxBoardState child : children) {
			Rating childRating = child.rating;
			
			if (rating == null || childRating.compareTo(rating) > 0) {
				rating = childRating;
				bestChild = child;
			}
		}
		
		if (rating == null) {
			throw new RuntimeException("Can't maximize without any child nodes!");
		}
	}
	
	private void minimize() {
		for (MinimaxBoardState child : children) {
			Rating childRating = child.rating;
			
			if (rating == null || childRating.compareTo(rating) < 0) {
				rating = childRating;
				bestChild = child;
			}
		}
		
		if (rating == null) {
			throw new RuntimeException("Can't minimize without any child nodes!");
		}
	}

	public List<MinimaxBoardState> getBestPath() {
		List<MinimaxBoardState> path = new ArrayList<>();
		path.add(this);
		
		if (!isLeaf()) {
			path.addAll(bestChild.getBestPath());
		}
		
		return path;
	}
	
	public Move getBestMove() {
		return bestChild.getLastMove();
	}

	public boolean isMaximizing() {
		return maximize;
	}
	
	@Override
	public Color getColor() {
		if (parent != null && parent.bestChild == this) {
			return Color.RED;
		} else {
			return Color.BLACK;
		}
	}
	
	@Override
	public String toString() {
		return getNodeDescription();
	}
}
