package com.fwcd.sc18;

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
 * Provides a skeletal implementation for
 * game logics.
 */
public abstract class TemplateLogic implements IGameHandler {
	private static final Logger LOG = LoggerFactory.getLogger("ownlog");
	
	private final AbstractClient client;
	private GameState gameState;
	private Player currentPlayer;
	
	public TemplateLogic(AbstractClient client) {
		this.client = client;
	}
	
	@Override
	public void gameEnded(GameResult result, PlayerColor color, String errorMessage) {}

	@Override
	public void onRequestAction() {
		long startTime = System.currentTimeMillis();
		LOG.info("A move has been requested.");
		
		Move move = selectMove(gameState, currentPlayer);
		move.orderActions();
		
		LOG.info("Sending move {}", move);
		long endTime = System.currentTimeMillis();
		sendAction(move);
		
		LOG.warn("Time needed for turn: {} ms", endTime - startTime);
	}
	
	protected abstract Move selectMove(GameState gameBeforeMove, Player me);
	
	@Override
	public void onUpdate(GameState gameState) {
		this.gameState = gameState;
		currentPlayer = gameState.getCurrentPlayer();
	}

	@Override
	public void onUpdate(Player player, Player otherPlayer) {
		currentPlayer = player;
	}

	@Override
	public void sendAction(Move move) {
		client.sendMove(move);
	}
}
