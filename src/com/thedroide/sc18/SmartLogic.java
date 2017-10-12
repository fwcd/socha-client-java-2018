package com.thedroide.sc18;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.antelmann.game.GameDriver;
import com.thedroide.sc18.bindings.HUIEnumPlayer;
import com.thedroide.sc18.bindings.HUIGamePlay;
import com.thedroide.sc18.bindings.HUIMove;
import com.thedroide.sc18.debug.GUILogger;

import sc.player2018.Starter;
import sc.plugin2018.GameState;
import sc.plugin2018.IGameHandler;
import sc.plugin2018.Move;
import sc.plugin2018.Player;
import sc.shared.GameResult;
import sc.shared.PlayerColor;

// TODO: Clean up GUILogger calls before handing in the client

/**
 * My custom logic connecting the Game-API with
 * the Software Challenge API.
 */
public class SmartLogic implements IGameHandler {
	private static final Logger LOG = LoggerFactory.getLogger(SmartLogic.class);
	
	private Starter client;
	private GameState gameState;
	private Player currentPlayer;

	private int searchDepth = 4; // The depth of our search tree
	private final HUIGamePlay game = new HUIGamePlay();
	private final GameDriver ai = new GameDriver(game, HUIEnumPlayer.getPlayers(), searchDepth);

	/**
	 * Creates a new AI-player that commits moves.
	 * 
	 * @param Starter - The client itself
	 */
	public SmartLogic(Starter client) {
		this.client = client;
		
		ai.setResponseTime(500); // TODO: Tweak this value, max response time is IIRC 2000 or 3000
	}

	/**
	 * An event handler for the game ending.
	 */
	@Override
	public void gameEnded(GameResult data, PlayerColor color, String errorMessage) {
		LOG.info("Game ended.");
	}

	// TODO: Implement multithreading (GameDriver calculating silently while the opponent moves)
	
	/**
	 * Called whenever a turn is requested.
	 * This method contains the relevant code.
	 */
	@Override
	public void onRequestAction() {
		long startTime = System.currentTimeMillis();
		LOG.info("Move requested.");

		GUILogger.log("Initial player turn: " + gameState.getCurrentPlayerColor() + " with board " + game.toString());
		
		// Picks the best move either from the ShallowStrategy or the AI
		HUIMove huiMove = (HUIMove) ai.autoMove();
		Move scMove = huiMove.getSCMove();

		long nowTime = System.nanoTime();
		GUILogger.log("Committed " + huiMove + " in " + Long.toString(nowTime - startTime) + "ms");
		
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
		LOG.info("Switching turns: " + player.getPlayerColor());
	}

	/**
	 * An event handler that get's called whenever the board
	 * updates.
	 */
	@Override
	public void onUpdate(GameState gameState) {
		this.gameState = gameState;
		currentPlayer = gameState.getCurrentPlayer();
		
		// TODO: Multithreading, background calculation?
		
		game.setSCState(gameState);
		
		LOG.info("New move: {}", gameState.getTurn());
		LOG.info("Player: {}", currentPlayer.getPlayerColor());
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
