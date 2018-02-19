package com.fwcd.sc18.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fwcd.sc18.utils.HUIUtils;

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
public abstract class TemplateLogic implements IGameHandler, CopyableLogic {
	private static final Logger LOG = LoggerFactory.getLogger("ownlog");
	
	private final AbstractClient client;
	private GameState gameState;
	private Player currentPlayer;
	private PlayerColor me;
	private boolean firstMove = true;
	
	public TemplateLogic(AbstractClient client) {
		this.client = client;
	}
	
	protected void onGameStart(GameState gameState) {}
	
	protected void onGameEnd(GameState gameState, boolean won, GameResult result, String errorMessage) {}
	
	@Override
	public void gameEnded(GameResult result, PlayerColor color, String errorMessage) {
		PlayerColor winner = HUIUtils.getWinnerOrNull(gameState);
		boolean won = (winner != null) && (winner == me);
		onGameEnd(gameState, won, result, errorMessage);
	}

	@Override
	public void onRequestAction() {
		if (firstMove) {
			onGameStart(gameState);
			firstMove = false;
			me = currentPlayer.getPlayerColor();
		}
		
		long startTime = System.currentTimeMillis();
		
		Move move = selectMove(gameState, currentPlayer);
		move.orderActions();
		
		long endTime = System.currentTimeMillis();
		sendAction(move);
		
		LOG.info("Committed move {} in {} ms", HUIUtils.toString(move) /* FIXME: Remove this later for performance */, endTime - startTime);
		LOG.debug("Carrots: {}, field: {}", getMe().getCarrots(), getMe().getFieldIndex());
	}
	
	protected abstract Move selectMove(GameState gameBeforeMove, Player me);
	
	@Override
	public void onUpdate(GameState gameState) {
		this.gameState = gameState;
		currentPlayer = gameState.getCurrentPlayer();
	}

	@Override
	public void onUpdate(Player player, Player opponent) {
		currentPlayer = player;
	}

	@Override
	public void sendAction(Move move) {
		client.sendMove(move);
	}
	
	public Player getMe() {
		return getMe(gameState);
	}
	
	public Player getOpponent() {
		return getOpponent(gameState);
	}
	
	public Player getMe(GameState state) {
		return me == PlayerColor.BLUE ? state.getBluePlayer() : state.getRedPlayer();
	}
	
	public Player getOpponent(GameState state) {
		return me == PlayerColor.BLUE ? state.getRedPlayer() : state.getBluePlayer();
	}
	
	public PlayerColor getMyColor() {
		return me;
	}
	
	public PlayerColor getOpponentColor() {
		return me.opponent();
	}
}
