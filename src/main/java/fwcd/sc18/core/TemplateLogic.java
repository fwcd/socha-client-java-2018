package fwcd.sc18.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.antelmann.game.GameMove;
import com.antelmann.game.GamePlay;
import fwcd.sc18.agbinds.AGGameState;
import fwcd.sc18.agbinds.AGMove;
import fwcd.sc18.agbinds.AGPlayerColor;
import fwcd.sc18.trainer.core.VirtualClient;
import fwcd.sc18.utils.HUIUtils;

import sc.plugin2018.AbstractClient;
import sc.plugin2018.GameState;
import sc.plugin2018.IGameHandler;
import sc.plugin2018.Move;
import sc.plugin2018.Player;
import sc.shared.GameResult;
import sc.shared.PlayerColor;

/**
 * Provides a skeletal implementation for
 * game logics that are compatible with
 * both the "Software Challenge"-API and the
 * Antelmann-Game-API.
 */
public abstract class TemplateLogic implements IGameHandler, CopyableLogic, com.antelmann.game.Player {
	protected static final Logger LOG = LoggerFactory.getLogger("ownlog");
	
	private final VirtualClient virtualClient;
	private final AbstractClient client;
	private GameState gameState;
	private Player currentPlayer;
	private PlayerColor me;
	private boolean firstMove = true;
	
	public TemplateLogic(VirtualClient virtualClient) {
		this.virtualClient = virtualClient;
		client = null;
	}
	
	public TemplateLogic(AbstractClient client) {
		this.client = client;
		virtualClient = null;
	}
	
	protected void onGameStart(GameState gameState) {}
	
	protected void onGameEnd(GameState gameState, boolean won, GameResult result, String errorMessage) {}
	
	protected void onMoveSend(GameState gameBeforeMove, Move move) {}
	
	@Override
	public void gameEnded(GameResult result, PlayerColor color, String errorMessage) {
		PlayerColor winner = HUIUtils.getWinnerOrNull(gameState);
		boolean won = (winner != null) && (winner == me);
		onGameEnd(gameState, won, result, errorMessage);
		firstMove = true;
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
		
		onMoveSend(gameState, move);
		long endTime = System.currentTimeMillis();
		sendAction(move);
		
		LOG.info("Committed move in {} ms", endTime - startTime);
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
		if (client != null) {
			client.sendMove(move);
		} else {
			virtualClient.sendMove(move);
		}
	}
	
	public Player getMe() {
		return getMe(gameState);
	}
	
	public Player getOpponent() {
		return getOpponent(gameState);
	}
	
	public Player getMe(GameState state) {
		return state.getPlayer(me);
	}
	
	public Player getOpponent(GameState state) {
		return state.getPlayer(me.opponent());
	}
	
	public PlayerColor getMyColor() {
		return me;
	}
	
	public PlayerColor getOpponentColor() {
		return me.opponent();
	}

	@Override
	public String getPlayerName() {
		return getClass().getSimpleName();
	}

	@Override
	public boolean canPlayGame(GamePlay game) {
		return game instanceof AGGameState;
	}

	@Override
	public double evaluate(GamePlay game, GameMove move, int[] role, int level, long milliseconds) {
		return 0;
	}

	@Override
	public double heuristic(GamePlay game, GameMove move, int[] role) {
		return 0;
	}

	@Override
	public GameMove selectMove(GamePlay game, int[] role, int level, long milliseconds) {
		GameState state = ((AGGameState) game).getState();
		AGPlayerColor color = AGPlayerColor.of(role[0]);
		return new AGMove(selectMove(state, state.getPlayer(color.asPlayerColor())), color);
	}

	@Override
	public boolean pruneMove(GamePlay game, GameMove move, int[] role) {
		return false;
	}
}
