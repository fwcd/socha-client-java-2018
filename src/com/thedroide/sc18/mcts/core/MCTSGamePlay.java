package com.thedroide.sc18.mcts.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import com.thedroide.sc18.choosers.MoveChooser;
import com.thedroide.sc18.choosers.SimpleMoveChooser;
import com.thedroide.sc18.utils.TreeNode;

import sc.plugin2018.GameState;
import sc.plugin2018.Move;
import sc.plugin2018.Player;
import sc.shared.InvalidMoveException;
import sc.shared.PlayerColor;

/**
 * Represents a node in the game tree which can operate on it
 * using monte-carlo-tree-search.
 */
public class MCTSGamePlay implements Comparable<MCTSGamePlay>, TreeNode {
	private static final int EXPLORATION_WEIGHT = 2;
	private static final int MAX_SIMULATION_DEPTH = 100;
	private static final Random RANDOM = ThreadLocalRandom.current();
	private static final float EPSILON = 1e-6F;
	
	private final MoveChooser moveChooser = new SimpleMoveChooser();
	private final PlayerColor ourPlayerColor;
	private final MCTSGamePlay parent;
	private final Move move;
	private final GameState stateAfterMove;
	private List<MCTSGamePlay> exploredChilds = null;
	
	private int wins = 0;
	private int simulations = 0;
	
	/**
	 * Creates a new root MCTS-node.
	 * 
	 * @param ourPlayerColor - The player we're playing for
	 * @param state - The game state
	 */
	public MCTSGamePlay(PlayerColor ourPlayerColor, GameState state) {
		parent = null;
		move = null;
		stateAfterMove = state;
		this.ourPlayerColor = ourPlayerColor;
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
		ourPlayerColor = parent.ourPlayerColor;
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
		return getValue()
				+ (RANDOM.nextFloat() * EPSILON)
				+ (EXPLORATION_WEIGHT * (float) Math.sqrt(Math.log(parent.simulations + 1) / (float) simulations + EPSILON));
	}
	
	private float getValue() {
		return (float) wins / (float) simulations + EPSILON;
	}
	
	public boolean isRoot() {
		return parent == null;
	}
	
	@Override
	public boolean isLeaf() {
		return exploredChilds == null || exploredChilds.isEmpty();
	}
	
	public void performIteration() {
		expand();
		
		MCTSGamePlay leaf = select();
		leaf.expand();
		
		int result = leaf.simulate();
		
		if (result > 0) {
			leaf.backpropagate(1);
		} else if (result < 0) {
			leaf.backpropagate(0);
		}
	}
	
	private MCTSGamePlay select() {
		MCTSGamePlay child = Collections.max(exploredChilds);
		
		if (child.isLeaf()) {
			return child;
		} else {
			return child.select();
		}
	}
	
	private void backpropagate(int winsDelta) {
		if (!isRoot()) {
			addWins(winsDelta);
			parent.backpropagate(winsDelta);
		}
	}
	
	/**
	 * Simulates this game.
	 * 
	 * @return 1: Win for our player - 0: Not determined - -1: Win for opponent
	 */
	private int simulate() {
		GameState simulation;
		
		try {
			simulation = stateAfterMove.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
		
		try {
			int i = 0;
			while (!simulation.getRedPlayer().inGoal()
					&& !simulation.getBluePlayer().inGoal()
					&& i < MAX_SIMULATION_DEPTH) {
				moveChooser.chooseMove(simulation).perform(simulation);
				
				i++;
			}
		} catch (InvalidMoveException e) {}
		
		return winnerOf(simulation);
	}
	
	private int winnerOf(GameState state) {
		Player red = state.getRedPlayer();
		Player blue = state.getBluePlayer();
		
		boolean redInGoal = red.inGoal();
		boolean blueInGoal = blue.inGoal();
		boolean redAhead = red.getFieldIndex() > blue.getFieldIndex();
		boolean blueAhead = blue.getFieldIndex() > red.getFieldIndex();
		
		switch (ourPlayerColor) {
		
		case RED:
			return redInGoal ? 1 : (blueInGoal ? -1 : (redAhead ? 1 : -1));
		case BLUE:
			return blueInGoal ? 1 : (redInGoal ? -1 : (blueAhead ? 1 : -1));
		default:
			throw new UnsupportedOperationException("Invalid player color.");
		
		}
	}
	
	private void expand() {
		if (exploredChilds == null) {
			exploredChilds = new ArrayList<>();
			
			for (Move move : stateAfterMove.getPossibleMoves()) {
				try {
					GameState newState = stateAfterMove.clone();
					move.perform(newState);
					exploredChilds.add(new MCTSGamePlay(this, move, newState));
				} catch (InvalidMoveException | CloneNotSupportedException e) {}
			}
		}
	}

	public Move getMove() {
		return move;
	}
	
	@Override
	public int compareTo(MCTSGamePlay o) {
		return Float.compare(uct(), o.uct());
	}
	
	public MCTSGamePlay mostExploredChild() {
		return Collections.max(exploredChilds, (a, b) -> Integer.compare(a.simulations, b.simulations));
	}

	@Override
	public List<? extends TreeNode> getChildren() {
		return exploredChilds;
	}

	@Override
	public String getNodeDescription() {
		return Integer.toString(wins) + "/" + Integer.toString(simulations);
	}
	
	@Override
	public String toString() {
		if (exploredChilds == null) {
			return getNodeDescription();
		} else {
			return getNodeDescription() + " -> " + exploredChilds.toString();
		}
	}
}
