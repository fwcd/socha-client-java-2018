package com.thedroide.sc18;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thedroide.sc18.algorithmics.Algorithm;
import com.thedroide.sc18.huibindings.HUIBoardState;
import com.thedroide.sc18.huibindings.HUIMove;
import com.thedroide.sc18.negamax.NegamaxAlgorithm;

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
	private Starter client;
	private GameState gameState;
	private Player currentPlayer;
	
	/**
	 * The heart of our IGameHandler.
	 */
	private final Algorithm algorithm = new NegamaxAlgorithm();

	private static final Logger LOG = LoggerFactory.getLogger(SmartLogic.class);
	// private static final Random RANDOM = new SecureRandom();

	/**
	 * Creates a new AI-player that commits moves.
	 * 
	 * @param Starter - The player's client
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
		
		Move move = ((HUIMove) algorithm.getBestMove(new HUIBoardState(gameState))).getSCMove();
		
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
