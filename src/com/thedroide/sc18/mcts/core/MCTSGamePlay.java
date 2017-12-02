package com.thedroide.sc18.mcts.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.thedroide.sc18.utils.GUILogger;

import sc.plugin2018.GameState;
import sc.plugin2018.Move;
import sc.shared.InvalidMoveException;

/**
 * Represents a node in the MCTS game tree.
 */
public class MCTSGamePlay implements Comparable<MCTSGamePlay> {
	private static final int EXPLORATION_WEIGHT = 1;
	private static final int MAX_SIMULATION_DEPTH = 64;
	private static final Random RANDOM = new Random();
	
	private final MCTSGamePlay parent;
	private final Move move;
	private final GameState stateAfterMove;
	private List<MCTSGamePlay> exploredChilds = null;
	
	private int wins = 0;
	private int simulations = 0;
	
	/**
	 * Creates a new root MCTS-node.
	 * 
	 * @param state - The game state
	 */
	public MCTSGamePlay(GameState state) {
		parent = null;
		move = null;
		stateAfterMove = state;
	}
	
	/**
	 * Creates a new non-root MCTS-node.
	 * 
	 * @param parent - The parent node
	 * @param state - The game state
	 */
	private MCTSGamePlay(MCTSGamePlay parent, Move move, GameState state) {
		this.parent = parent;
		this.move = move;
		stateAfterMove = state;
	}
	
	private void addWins(int wins) {
		simulations++;
		this.wins += wins;
	}
	
	/**
	 * Fetches an upper confidence bound value for this node.
	 * 
	 * @return An UCT value
	 */
	private float uct() {
		return getValue() + (
				EXPLORATION_WEIGHT * (float) Math.sqrt(Math.log(parent.getValue()) / (float) simulations)
		);
	}
	
	private float getValue() {
		return (float) wins / (float) simulations;
	}
	
	public boolean isRoot() {
		return parent == null;
	}
	
	private void simulate() {
		try {
			GameState simulation = stateAfterMove.clone();
			
			int i = 0;
			while (!simulation.getRedPlayer().inGoal()
					&& !simulation.getBluePlayer().inGoal()
					&& i < MAX_SIMULATION_DEPTH) {
				// FIXME TODO: Continue work here
			}
		} catch (CloneNotSupportedException e) {
			GUILogger.printStack(e);
		}
	}
	
	private void expand() {
		exploredChilds = new ArrayList<>();
		
		for (Move move : stateAfterMove.getPossibleMoves()) {
			try {
				GameState newState = stateAfterMove.clone();
				move.perform(newState);
				exploredChilds.add(new MCTSGamePlay(this, move, newState));
			} catch (InvalidMoveException | CloneNotSupportedException e) {
				GUILogger.printStack(e);
			}
		}
	}

	@Override
	public int compareTo(MCTSGamePlay o) {
		return Float.compare(uct(), o.uct());
	}
	
	public MCTSGamePlay mostExploredChild() {
		return Collections.max(exploredChilds, (a, b) -> Integer.compare(a.simulations, b.simulations));
	}
}
