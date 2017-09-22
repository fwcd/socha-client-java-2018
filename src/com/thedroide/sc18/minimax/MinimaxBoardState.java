package com.thedroide.sc18.minimax;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.RecursiveAction;

import com.thedroide.sc18.algorithmics.GraphTreeNode;
import com.thedroide.sc18.algorithmics.MoveRating;
import com.thedroide.sc18.algorithmics.Strategy;
import com.thedroide.sc18.debug.GUILogger;

// GUILogger;

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
	
	private final Strategy strategy;
	
	private MinimaxBoardState parent = null; // Will be null, if this is the root
	private final List<MinimaxBoardState> possibleMoveStates = new ArrayList<>();
	private MinimaxBoardState bestMoveState;
	
	private boolean maximize; // Only relevant for non-leafs
	private Move lastMove;
	private Move bestMove; // Only relevant for leafs
	private GameState state;
	private int decrementalDepth;
	
	/**
	 * Convenience constructor for generating a
	 * minimax tree starting with maximizing (player's
	 * turn).
	 * 
	 * @param state - The (initial) state of the board
	 * @param strategy - The evaluation strategy
	 * @param decrementalDepth - The number of moves predicted. Complexity grows exponentially to this.
	 */
	public MinimaxBoardState(GameState state, Strategy strategy, int decrementalDepth) {
		this(state, state.getLastMove(), strategy, decrementalDepth, false, null);
	}
	
	/**
	 * Generates a new tree of possible board states.
	 * 
	 * @param state - The (initial) state of the board
	 * @param lastMove - The most recently commited move
	 * @param decrementalDepth - The number of moves predicted. Complexity grows exponetially to this.
	 * @param maximize - Whether this move should be maximized (or not)
	 * @param parent - The parent node (may be null in case of root)
	 */
	private MinimaxBoardState(
			GameState state,
			Move lastMove,
			Strategy strategy,
			int decrementalDepth,
			boolean maximize,
			MinimaxBoardState parent) {
		this.state = state;
		this.decrementalDepth = decrementalDepth;
		this.maximize = maximize;
		this.parent = parent;
		this.lastMove = lastMove;
		this.strategy = strategy;
	}
	
	/**
	 * Computes the best move for a leaf.
	 */
	private void computeBestMove() {
		// TODO: Implement Alpha-Beta and FieldRatings storing
		
		Move chosenMove = null;
		MoveRating chosenRating = null;
		
		for (Move move : state.getPossibleMoves()) {
			MoveRating rating = strategy.evaluate(move, state);
			
			if (chosenMove == null || (maximize ? rating.compareTo(chosenRating) > 0 : rating.compareTo(chosenRating) < 0)) {
				chosenMove = move;
				chosenRating = rating;
			}
		}
		
		bestMove = chosenMove;
		
		// GUILogger.log("Chose " + bestMove + " from " + state.getPossibleMoves().size());
	}
	
	/**
	 * Minimizes/maximized the best move for a non-leaf.
	 */
	private void minimax() {
		if (maximize) {
			bestMoveState = Collections.max(possibleMoveStates);
			GUILogger.log("Maximized " + strategy.evaluate(bestMoveState.getMove(), bestMoveState.state));
		} else {
			// GUILogger.log("Minimizing " + possibleMoveStates.size());
			bestMoveState = Collections.min(possibleMoveStates);
			GUILogger.log("Minimized " + strategy.evaluate(bestMoveState.getMove(), bestMoveState.state));
		}
		
		GUILogger.log((maximize ? "Maximized" : "Minimized") + bestMoveState.getNodeDescription());
	}
	
	@Override
	public boolean isLeaf() {
		return decrementalDepth == 0;
	}
	
	public Move getMove() {
		if (bestMove != null) {
			return bestMove;
		} else {
			return bestMoveState.lastMove;
		}
	}
	
	@Override
	protected void compute() {
		if (decrementalDepth > 0) {
			for (Move move : state.getPossibleMoves()) {
				try {
					GameState newState = state.clone();
					
					// GUILogger.log("Performing action for move at depth " + decrementalDepth);
					
					for (Action action : move.getActions()) {
						action.perform(newState);
					}
					
					newState.switchCurrentPlayer();
					
					MinimaxBoardState child = new MinimaxBoardState(newState, move, strategy, decrementalDepth - 1, !maximize, this);
					possibleMoveStates.add(child);
				} catch (CloneNotSupportedException | InvalidMoveException e) {
					e.printStackTrace();
				}
			}
			
			invokeAll(possibleMoveStates);
			
			for (MinimaxBoardState child : possibleMoveStates) {
				child.quietlyJoin();
			}
			
			minimax();
		} else {
			computeBestMove();
		}
	}
	
	@Override
	public String toString() {
		return getNodeDescription() + "[" + possibleMoveStates.toString() + "]";
	}

	@Override
	public int compareTo(MinimaxBoardState o) {
		return strategy.evaluate(getMove(), state)
				.compareTo(strategy.evaluate(o.getMove(), state));
	}
	
	@Override
	public boolean equals(Object obj) {
		return compareTo((MinimaxBoardState) obj) == 0;
	}

	@Override
	public List<? extends GraphTreeNode> getChildren() {
		return possibleMoveStates;
	}

	@Override
	public String getNodeDescription() {
		return "R" + Integer.toString(state.getRedPlayer().getFieldIndex()) + "|"
				+ "B" + Integer.toString(state.getBluePlayer().getFieldIndex());
	}
	
	@Override
	public Color getColor() {
		if (parent != null && parent.bestMoveState == this) {
			return Color.RED;
		} else {
			return Color.BLACK;
		}
	}
}
