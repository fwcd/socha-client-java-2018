package com.thedroide.sc18.mcts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
	private AbstractClient client;
	private GameState game;
	private Player currentPlayer;
	
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
		// TODO: Implement MCTS here
		
		Move scMove = game.getPossibleMoves().get(0); // FIXME: Horrible hack
		scMove.orderActions();
		sendAction(scMove);
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
		game = gameState;
		
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
