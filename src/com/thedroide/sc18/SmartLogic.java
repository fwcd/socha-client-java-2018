package com.thedroide.sc18;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.antelmann.game.AutoPlay;
import com.antelmann.game.GameDriver;
import com.antelmann.game.GamePlay;
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

// TODO: Clean up GUILogger calls in the end

/**
 * Our customized logic.
 */
public class SmartLogic implements IGameHandler {
	private static final Logger LOG = LoggerFactory.getLogger(SmartLogic.class);
//	private static final Random RANDOM = new SecureRandom();
	
	private Starter client;
	private GameState gameState;
	private Player currentPlayer;

	/**
	 * The depth of our search tree.
	 */
	private final int searchDepth = 4;

	/**
	 * Creates a new AI-player that commits moves.
	 * 
	 * @param Starter
	 *            - The player's client
	 */
	public SmartLogic(Starter client) {
		this.client = client;
	}

	public void gameEnded(GameResult data, PlayerColor color, String errorMessage) {
		LOG.info("Game ended.");
	}

	/**
	 * Called whenever a turn is requested.
	 */
	@Override
	public void onRequestAction() {
		long startTime = System.nanoTime();
		LOG.info("Move requested.");

		GUILogger.log("Initial player turn: " + gameState.getCurrentPlayerColor());
		
		// Relevant stuff below
		GamePlay game = new HUIGamePlay(gameState);
		AutoPlay ai = new GameDriver(game, HUIEnumPlayer.getPlayers(), searchDepth);
		Move move = ((HUIMove) ai.autoMove()).getSCMove();
		
		move.orderActions();
		LOG.info("Sending move {}", move);
		long nowTime = System.nanoTime();
		sendAction(move);
		LOG.warn("Time needed for turn: {}", (nowTime - startTime) / 1000000);
	}

	@Override
	public void onUpdate(Player player, Player otherPlayer) {
		currentPlayer = player;
		LOG.info("Switching turns: " + player.getPlayerColor());
	}

	@Override
	public void onUpdate(GameState gameState) {
		this.gameState = gameState;
		currentPlayer = gameState.getCurrentPlayer();
		LOG.info("New move: {}", gameState.getTurn());
		LOG.info("Player: {}", currentPlayer.getPlayerColor());
	}

	@Override
	public void sendAction(Move move) {
		client.sendMove(move);
	}
}
