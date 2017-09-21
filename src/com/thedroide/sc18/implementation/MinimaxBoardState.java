package com.thedroide.sc18.implementation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.RecursiveAction;

import sc.plugin2018.Action;
import sc.plugin2018.GameState;
import sc.plugin2018.Move;
import sc.shared.InvalidMoveException;

/**
 * A tree of possible board states using recursive
 * fork/join computation.
 */
public class MinimaxBoardState extends RecursiveAction implements Comparable<MinimaxBoardState> {
	private static final long serialVersionUID = 1L;
	
	private MinimaxBoardState parent = null; // Will be null, if this is the root
	private final List<MinimaxBoardState> possibleMoveStates = new ArrayList<>();
	private MinimaxBoardState bestMoveState;
	
	private boolean maximize; // Only relevant for non-leafs
	private Move bestMove; // Only relevant for leafs
	private GameState state;
	private int depth;
	
	/**
	 * Convenience constructor for generating a
	 * minimax tree starting with maximizing (player's
	 * turn).
	 * 
	 * @param state - The (initial) state of the board
	 * @param depth - The number of moves predicted. Complexity grows exponentially to this.
	 */
	public MinimaxBoardState(GameState state, int depth) {
		this(state, depth, true, null);
	}
	
	/**
	 * Generates a new tree of possible board states.
	 * 
	 * @param state - The (initial) state of the board
	 * @param depth - The number of moves predicted. Complexity grows exponetially to this.
	 * @param maximize - Whether this move should be maximized (or not)
	 * @param parent - The parent node (may be null in case of root)
	 */
	public MinimaxBoardState(GameState state, int depth, boolean maximize, MinimaxBoardState parent) {
		this.state = state;
		this.depth = depth;
		this.maximize = maximize;
		this.parent = parent;
	}
	
	/**
	 * Computes the best move for a leaf.
	 */
	private void computeBestMove() {
		// TODO: Implement Alpha-Beta and FieldRatings storing
		
		Move chosenMove = null;
		MoveRating chosenRating = null;
		
		for (Move move : state.getPossibleMoves()) {
			MoveRating rating = MoveRating.evaluate(move);
			
			if (maximize ? rating.compareTo(chosenRating) > 0 : rating.compareTo(chosenRating) < 0) {
				chosenMove = move;
				chosenRating = rating;
			}
		}
		
		bestMove = chosenMove;
	}
	
	/**
	 * Minimizes/maximized the best move for a non-leaf.
	 */
	private void optimize() {
		if (maximize) {
			bestMoveState = Collections.max(possibleMoveStates);
		} else {
			bestMoveState = Collections.min(possibleMoveStates);
		}
	}
	
	public Move getBestMove() {
		return bestMove != null ? bestMove : bestMoveState.getBestMove();
	}
	
	@Override
	protected void compute() {
		if (depth > 0) {
			for (Move move : state.getPossibleMoves()) {
				try {
					GameState newState = state.clone();
					
					for (Action action : move.getActions()) {
						action.perform(newState);
					}
					
					newState.switchCurrentPlayer();
					possibleMoveStates.add(new MinimaxBoardState(newState, depth - 1, !maximize, this));
				} catch (CloneNotSupportedException | InvalidMoveException e) {
					e.printStackTrace();
				}
			}
			
			invokeAll(possibleMoveStates);
			join();
			
			optimize();
		} else {
			computeBestMove();
		}
	}
	
	@Override
	public String toString() {
		return "R" + Integer.toString(state.getRedPlayer().getFieldIndex()) + "|"
				+ "B" + Integer.toString(state.getBluePlayer().getFieldIndex()) + "\n"
				+ "[" + possibleMoveStates.toString() + "]";
	}

	@Override
	public int compareTo(MinimaxBoardState o) {
		return MoveRating.evaluate(getBestMove()).compareTo(MoveRating.evaluate(o.getBestMove()));
	}
}