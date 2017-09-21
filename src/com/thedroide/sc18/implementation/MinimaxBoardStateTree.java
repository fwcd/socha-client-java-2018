package com.thedroide.sc18.implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveAction;

import sc.plugin2018.GameState;
import sc.plugin2018.Move;

/**
 * A tree of possible board states using recursive
 * fork/join computation.
 */
public class MinimaxBoardStateTree extends RecursiveAction {
	private static final long serialVersionUID = 1L;
	
	private final List<MinimaxBoardStateTree> possibleMoves = new ArrayList<>();
	private MinimaxBoardStateTree bestMove;
	
	private boolean maximize;
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
	public MinimaxBoardStateTree(GameState state, int depth) {
		this(state, depth, true);
	}
	
	/**
	 * Generates a new tree of possible board states.
	 * 
	 * @param state - The (initial) state of the board
	 * @param depth - The number of moves predicted. Complexity grows exponetially to this.
	 */
	public MinimaxBoardStateTree(GameState state, int depth, boolean maximize) {
		this.state = state;
		this.depth = depth;
		this.maximize = maximize;
	}
	
	@Override
	protected void compute() {
		if (depth > 0) {
			for (Move move : state.getPossibleMoves()) {
				try {
					GameState newState = state.clone();
					newState.switchCurrentPlayer();
					
					possibleMoves.add(new MinimaxBoardStateTree(newState, depth - 1, !maximize));
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
			}
			
			invokeAll(possibleMoves);
		}
		
		// TODO: Evaluation!!
	}
}
