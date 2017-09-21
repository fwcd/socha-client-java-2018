package com.thedroide.sc18;

import java.security.SecureRandom;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thedroide.sc18.algorithmics.Algorithm;
import com.thedroide.sc18.implementation.MinimaxAlgorithm;

import sc.player2018.Starter;
import sc.plugin2018.GameState;
import sc.plugin2018.IGameHandler;
import sc.plugin2018.Move;
import sc.plugin2018.Player;
import sc.shared.GameResult;
import sc.shared.PlayerColor;

/**
 * Unsere Logik.
 */
public class OurLogic implements IGameHandler {
	private Starter client;
	private GameState gameState;
	private Player currentPlayer;

	/**
	 * Das Herzstück unserer Logik.
	 */
	private final Algorithm algorithm = new MinimaxAlgorithm();

	private static final Logger LOG = LoggerFactory.getLogger(OurLogic.class);
	/*
	 * Klassenweit verfuegbarer Zufallsgenerator der beim Laden der klasse einmalig
	 * erzeugt wird und darin immer zur Verfuegung steht.
	 */
	private static final Random RANDOM = new SecureRandom();

	/**
	 * Erzeugt ein neues Strategieobjekt, das Zuege taetigt.
	 *
	 * @param client
	 *            Der Zugrundeliegende Client der mit dem Spielserver kommunizieren
	 *            kann.
	 */
	public OurLogic(Starter client) {
		this.client = client;
	}

	/**
	 * {@inheritDoc}
	 */
	public void gameEnded(GameResult data, PlayerColor color, String errorMessage) {
		LOG.info("Game ended.");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onRequestAction() {
		long startTime = System.nanoTime();
		LOG.info("Move requested.");
		
		Move move = algorithm.getBestMove(gameState);
		
		move.orderActions();
		LOG.info("Sending move {}", move);
		long nowTime = System.nanoTime();
		sendAction(move);
		LOG.warn("Time needed for turn: {}", (nowTime - startTime) / 1000000);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onUpdate(Player player, Player otherPlayer) {
		currentPlayer = player;
		LOG.info("Switching turns: " + player.getPlayerColor());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onUpdate(GameState gameState) {
		this.gameState = gameState;
		currentPlayer = gameState.getCurrentPlayer();
		LOG.info("New move: {}", gameState.getTurn());
		LOG.info("Player: {}", currentPlayer.getPlayerColor());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendAction(Move move) {
		client.sendMove(move);
	}
}
