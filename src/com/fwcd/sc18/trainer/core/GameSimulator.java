package com.fwcd.sc18.trainer.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fwcd.sc18.trainer.ui.GameView;

import sc.framework.plugins.SimplePlayer;
import sc.plugin2018.GameState;
import sc.plugin2018.IGameHandler;
import sc.plugin2018.Move;
import sc.plugin2018.util.Constants;
import sc.shared.GameResult;
import sc.shared.InvalidMoveException;
import sc.shared.PlayerColor;
import sc.shared.PlayerScore;
import sc.shared.ScoreDefinition;

public class GameSimulator {
	private final Optional<GameView> view;
	private final IGameHandler logicRed;
	private final IGameHandler logicBlue;
	private final VirtualClient clientRed;
	private final VirtualClient clientBlue;
	private final String nameRed;
	private final String nameBlue;
	private final int matches;
	private final List<Runnable> gameEndListeners;
	
	private GameState state = new GameState();
	private boolean started = false;
	private boolean stopped = false;
	
	public GameSimulator(
			Optional<GameView> view,
			IGameHandler logicRed,
			IGameHandler logicB,
			VirtualClient clientRed,
			VirtualClient clientB,
			int matches,
			List<Runnable> gameEndListeners
	) {
		this.view = view;
		this.logicRed = logicRed;
		this.logicBlue = logicB;
		this.clientRed = clientRed;
		this.clientBlue = clientB;
		this.matches = matches;
		this.gameEndListeners = gameEndListeners;
		
		nameRed = logicRed.getClass().getName();
		nameBlue = logicB.getClass().getName();
	}
	
	public void start() {
		if (started) {
			throw new IllegalStateException("GameSimulator already started.");
		} else if (stopped) {
			throw new IllegalStateException("GameSimulator already stopped.");
		} else {
			started = true;
		}
		
		for (int match=0; match<matches; match++) {
			state = new GameState();
			updateState();
			for (int round=0; round<=Constants.ROUND_LIMIT; round++) {
				logicRed.onRequestAction();
				boolean success1 = perform(clientRed.getLastMove());
				if (!success1) {
					break;
				}
				
				logicBlue.onRequestAction();
				boolean success2 = perform(clientBlue.getLastMove());
				if (!success2) {
					break;
				}
			}
			
			PlayerColor winner = getWinner();
			
			ScoreDefinition scoreDef = new ScoreDefinition();
			scoreDef.add(nameRed);
			scoreDef.add(nameBlue);
			List<PlayerScore> scores = new ArrayList<>();
			scores.add(new PlayerScore(winner == PlayerColor.RED, ""));
			scores.add(new PlayerScore(winner == PlayerColor.BLUE, ""));
			List<SimplePlayer> winners = new ArrayList<>();
			winners.add(winner == PlayerColor.RED ? state.getRedPlayer() : state.getBluePlayer());
			GameResult result = new GameResult(scoreDef, scores, winners);
			PlayerColor color = winner;
			String msg = winner + " won the game";
			
			logicRed.gameEnded(result, color, msg);
			logicBlue.gameEnded(result, color, msg);
			
			for (Runnable listener : gameEndListeners) {
				listener.run();
			}
		}
	}

	private PlayerColor getWinner() {
		if (state.getRedPlayer().inGoal()) {
			return PlayerColor.RED;
		} else if (state.getBluePlayer().inGoal()) {
			return PlayerColor.BLUE;
		} else {
			return state.getRedPlayer().getFieldIndex() > state.getBluePlayer().getFieldIndex() ? PlayerColor.RED : PlayerColor.BLUE;
		}
	}

	private boolean perform(Move move) {
		try {
			move.perform(state);
			updateState();
			return true;
		} catch (InvalidMoveException e) {
			return false;
		}
	}

	private void updateState() {
		view.ifPresent(view -> view.update(state));
		logicRed.onUpdate(state);
		logicRed.onUpdate(state.getCurrentPlayer(), state.getOtherPlayer());
		logicBlue.onUpdate(state);
		logicBlue.onUpdate(state.getCurrentPlayer(), state.getOtherPlayer());
	}

	public void stop() {
		stopped = true;
	}
}
