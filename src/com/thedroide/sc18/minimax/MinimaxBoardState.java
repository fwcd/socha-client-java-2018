package com.thedroide.sc18.minimax;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveAction;

import com.thedroide.sc18.algorithmics.GraphTreeNode;
import com.thedroide.sc18.algorithmics.Rating;
import com.thedroide.sc18.algorithmics.Evaluator;
import com.thedroide.sc18.utils.SimpleMove;

import sc.plugin2018.GameState;
import sc.plugin2018.Move;
import sc.shared.InvalidMoveException;

/**
 * A tree of possible board states using recursive
 * fork/join computation.
 */
public class MinimaxBoardState extends RecursiveAction implements GraphTreeNode, Comparable<MinimaxBoardState> {
	private static final long serialVersionUID = 1L;
	
	private final MinimaxBoardState root;
	
	private Rating alpha = IntRating.getEmpty(); // Worst-case for maximizer (me)
	private Rating beta = IntRating.getMax(); // Worst-case for minimizer (opponent)
	
	private List<MinimaxBoardState> children = new ArrayList<>();
	private MinimaxBoardState bestChild = null;
	private Rating rating = null;
	
	private final boolean maximize;
	private final GameState state;
	private final Evaluator<MinimaxBoardState> strategy;
	private final int depth;
	private final SimpleMove lastMove;
	
	public MinimaxBoardState(GameState state, Evaluator<MinimaxBoardState> strategy, int depth) {
		this(state, strategy, true, depth, null, null);
	}
	
	public MinimaxBoardState(
			GameState state,
			Evaluator<MinimaxBoardState> strategy,
			boolean maximize,
			int depth,
			SimpleMove lastMove,
			MinimaxBoardState root) {
		this.state = state;
		this.strategy = strategy;
		this.maximize = maximize;
		this.depth = depth;
		this.lastMove = lastMove;
		this.root = (root == null ? this : root);
	}
	
	public GameState getState() {
		return state;
	}
	
	public List<SimpleMove> getAvailableMoves() {
		try {
			List<SimpleMove> availableMoves = new ArrayList<>();
			
			for (Move scMove : state.getPossibleMoves()) {
				GameState nextState = state.clone();
				scMove.perform(nextState);
				
				availableMoves.add(new SimpleMove(nextState, scMove));
			}
			
			return availableMoves;
		} catch (CloneNotSupportedException | InvalidMoveException e) {
			throw new RuntimeException(e);
		}
	}
	
	public SimpleMove getLastMove() {
		return lastMove;
	}
	
	@Override
	protected void compute() {
		try {
			if (!isLeaf()) {
				
				for (SimpleMove move : getAvailableMoves()) {
					GameState switchedState = move.getStateAfterMove().clone();
					switchedState.switchCurrentPlayer();
					
					MinimaxBoardState child = new MinimaxBoardState(
							switchedState,
							strategy,
							!maximize,
							depth - 1,
							move,
							this);
					children.add(child);
					child.quietlyInvoke();
				}

				minimax();
			} else {
				rating = strategy.evaluate(this);
				
				if (!maximize) {
					rating = rating.invert();
				}
			}
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
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
			throw new RuntimeException("Can't maximize without any child nodes! :( Depth: " + depth + "");
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
			throw new RuntimeException("Can't minimize without any child nodes! :( Depth: " + depth + "");
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
	
	public SimpleMove getBestMove() {
		return bestChild.getLastMove();
	}

	public boolean isMaximizing() {
		return maximize;
	}
	
	@Override
	public Color getColor() {
		if (root.getBestPath().contains(this)) {
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
