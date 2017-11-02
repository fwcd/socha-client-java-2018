package com.thedroide.sc18;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.antelmann.game.GameDriver;
import com.thedroide.sc18.bindings.HUIEnumPlayer;
import com.thedroide.sc18.bindings.HUIGamePlay;
import com.thedroide.sc18.bindings.HUIMove;
import com.thedroide.sc18.debug.GUILogger;

import sc.player2018.SochaClientMain;
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
	
	// === Parameters that may be tweaked and tested: ==
	
	private final int minSearchDepth = 1; // Used during beginning to slow JVM startup
	private final int maxSearchDepth = 6; // Used for all subsequent moves
	
	private final boolean dynamicSearchDepth = true; // Dynamically modifies search depth based off response times
	private final int minTime = 200; // in ms - Minimum move time
	private final int softMaxTime = 1200; // in ms - Maximum move time
	private final int hardMaxTime = 1800; // in ms - Hard move time limit
	
	// == End of parameters ==
	
	private SochaClientMain client;
	private GameState gameState;
	private Player currentPlayer;

	private int depth = minSearchDepth;
	private int committedMoves = 0;
	
	private final HUIGamePlay game = new HUIGamePlay();
	private final GameDriver ai = new GameDriver(game, HUIEnumPlayer.getPlayers(), depth);
	
	/**
	 * Creates a new AI-player that commits moves.
	 * 
	 * @param SochaClientMain - The client itself
	 */
	public SmartLogic(SochaClientMain client) {
		this.client = client;
		
		ai.setResponseTime(hardMaxTime);
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
		
		GUILogger.log(
				"Player turn: "
				+ gameState.getCurrentPlayerColor()
				+ " with board "
				+ game.toString()
				+ " and tree depth "
				+ Integer.toString(depth)
		);
		
		if (!dynamicSearchDepth) {
			if (committedMoves == 1) {
				setDepth(maxSearchDepth);
			}
		}
		
		// Picks the best move from the AI
		HUIMove huiMove = (HUIMove) ai.autoMove();
		Move scMove = huiMove.getSCMove();
		
		scMove.orderActions();
		sendAction(scMove);

		committedMoves++;
		int responseTime = (int) (System.currentTimeMillis() - startTime);
		
		if (dynamicSearchDepth) {
			if (responseTime < minTime && depth < maxSearchDepth) {
				setDepth(++depth);
			} else if (responseTime > softMaxTime && depth > minSearchDepth) {
				setDepth(--depth);
			};
		}
		
		GUILogger.log("Committed " + huiMove + " in " + Integer.toString(responseTime) + "ms");
	}

	private void setDepth(int depth) {
		this.depth = depth;
		ai.setLevel(depth);
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
