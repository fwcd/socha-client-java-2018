package com.thedroide.sc18.mcts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thedroide.sc18.mcts.core.MCTSGamePlay;
import com.thedroide.sc18.utils.TreePlotter;

import sc.plugin2018.AbstractClient;
import sc.plugin2018.GameState;
import sc.plugin2018.IGameHandler;
import sc.plugin2018.Move;
import sc.plugin2018.Player;
import sc.shared.GameResult;
import sc.shared.PlayerColor;

/**
 * An experimental monte-carlo-tree-search.
 * 
 * TODO: This implementation current does not contain anything at all. Implement MCTS.
 */
public class MCTSLogic implements IGameHandler {
	private static final Logger STD_LOG = LoggerFactory.getLogger(MCTSLogic.class);
	
	// === Parameters that may be tweaked and tested: ==
	
	private int softMaxTime = 1800; // in ms
	
	// == End of parameters ==
	
	private AbstractClient client;
	private MCTSGamePlay game;
	private Player currentPlayer;
	private TreePlotter plotter = new TreePlotter();
	
	public MCTSLogic(AbstractClient client) {
		this.client = client;
	}
	
	/**
	 * An event handler for the game ending.
	 */
	@Override
	public void gameEnded(GameResult data, PlayerColor color, String errorMessage) {
		STD_LOG.info("Game ended.");
	}
	
	/**
	 * Called whenever a turn is requested.
	 * This method contains the relevant code.
	 */
	@Override
	public void onRequestAction() {
		long start = System.currentTimeMillis();
		
		while ((System.currentTimeMillis() - start) < softMaxTime) {
			game.performIteration();
		}
		
		Move move = game.mostExploredChild().getMove();
		new Thread(() -> plotter.setTree(game)).start();
		
		move.orderActions();
		sendAction(move);
	}
	/**
	 * An event handler that get's called whenever turns
	 * are switched.
	 */
	@Override
	public void onUpdate(Player player, Player otherPlayer) {
		currentPlayer = player;
		STD_LOG.info("Switching turns: " + player.getPlayerColor());
	}
	
	/**
	 * An event handler that get's called whenever the board
	 * updates.
	 */
	@Override
	public void onUpdate(GameState gameState) {
		currentPlayer = gameState.getCurrentPlayer();
		game = new MCTSGamePlay(client.getColor(), gameState);
		
		STD_LOG.info("New move: {}", gameState.getTurn());
		STD_LOG.info("Player: {}", currentPlayer.getPlayerColor());
	}

	/**
	 * An API-implementation used to send a move to
	 * the server.
	 */
	@Override
	public void sendAction(Move move) {
		client.sendMove(move);
	}
}
