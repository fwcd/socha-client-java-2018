package com.thedroide.sc18.mcts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import com.thedroide.sc18.choosers.MoveChooser;
import com.thedroide.sc18.choosers.SimpleMoveChooser;
import com.thedroide.sc18.core.HUIGameState;
import com.thedroide.sc18.core.HUIMove;
import com.thedroide.sc18.core.HUIPlayerColor;
import com.thedroide.sc18.utils.TreeNode;

import sc.plugin2018.Player;

/**
 * Represents a node/state in the game tree which can operate on it
 * using monte-carlo-tree-search.
 */
public class MCTSNode implements Comparable<MCTSNode>, TreeNode {
	private static final int EXPLORATION_WEIGHT = 2;
	private static final int MAX_SIMULATION_DEPTH = 36;
	private static final Random RANDOM = ThreadLocalRandom.current();
	private static final float EPSILON = 1e-6F;
	
	private final MoveChooser moveChooser = new SimpleMoveChooser();
	private final HUIPlayerColor ourPlayerColor;
	private final MCTSNode parent;
	private final HUIMove move;
	private final HUIGameState stateAfterMove;
	private List<MCTSNode> exploredChilds = null;
	
	private int wins = 0;
	private int simulations = 0;
	
	/**
	 * Creates a new root MCTS-node.
	 * 
	 * @param ourPlayerColor - The player we're playing for
	 * @param state - The game state
	 */
	public MCTSNode(HUIPlayerColor ourPlayerColor, HUIGameState state) {
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
	private MCTSNode(MCTSNode parent, HUIMove move, HUIGameState state) {
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
				+ (EXPLORATION_WEIGHT * (float) Math.sqrt(Math.log(parent.simulations + 1) / simulations + EPSILON));
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
		
		MCTSNode leaf = select();
		leaf.expand();
		
		int result = leaf.simulate();
		
		if (result > 0) {
			leaf.backpropagate(1);
		} else if (result < 0) {
			leaf.backpropagate(0);
		}
	}
	
	private MCTSNode select() {
		MCTSNode child = Collections.max(exploredChilds);
		
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
		HUIGameState simulation;
		
		try {
			simulation = stateAfterMove.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
		
		int i = 0;
		while (simulation.getWinner() == null && i < MAX_SIMULATION_DEPTH) {
			simulation.makeMove(moveChooser.chooseMove(simulation));
			i++;
		}
		
		return winnerOf(simulation);
	}
	
	private int winnerOf(HUIGameState state) {
		Player red = HUIPlayerColor.RED.getSCPlayer(state);
		Player blue = HUIPlayerColor.BLUE.getSCPlayer(state);
		
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
			
			for (HUIMove move : stateAfterMove.getLegalMoves()) {
				try {
					HUIGameState newState = stateAfterMove.clone();
					newState.makeMove(move);
					exploredChilds.add(new MCTSNode(this, move, newState));
				} catch (CloneNotSupportedException e) {}
			}
		}
	}

	public HUIMove getMove() {
		return move;
	}
	
	@Override
	public int compareTo(MCTSNode o) {
		return Float.compare(uct(), o.uct());
	}
	
	public MCTSNode mostExploredChild() {
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
