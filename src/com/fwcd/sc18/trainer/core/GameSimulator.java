package com.fwcd.sc18.trainer.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

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
	private final IGameHandler redLogic;
	private final IGameHandler blueLogic;
	private final VirtualClient redClient;
	private final VirtualClient blueClient;
	private final long matches;
	private final List<Runnable> gameEndListeners;
	
	private GameState state = new GameState();
	private boolean started = false;
	private boolean stopped = false;
	
	public GameSimulator(Function<VirtualClient, IGameHandler> red, Function<VirtualClient, IGameHandler> blue, long matches) {
		this.matches = matches;
		view = Optional.empty();
		gameEndListeners = Collections.emptyList();
		
		redClient = new VirtualClient(PlayerColor.RED);
		redLogic = red.apply(redClient);
		blueClient = new VirtualClient(PlayerColor.BLUE);
		blueLogic = blue.apply(blueClient);
	}
	
	public GameSimulator(
			Optional<GameView> view,
			IGameHandler redLogic,
			IGameHandler blueLogic,
			VirtualClient redClient,
			VirtualClient blueClient,
			long matches,
			List<Runnable> gameEndListeners
	) {
		this.view = view;
		this.redLogic = redLogic;
		this.blueLogic = blueLogic;
		this.redClient = redClient;
		this.blueClient = blueClient;
		this.matches = matches;
		this.gameEndListeners = gameEndListeners;
	}
	
	public void start() {
		if (started) {
			throw new IllegalStateException("GameSimulator already started.");
		} else if (stopped) {
			throw new IllegalStateException("GameSimulator already stopped.");
		} else {
			started = true;
		}
		
		for (long match=0; match<matches; match++) {
			state = new GameState();
			updateState();
			for (int round=0; round<=Constants.ROUND_LIMIT; round++) {
				redLogic.onRequestAction();
				boolean success1 = perform(redClient.getLastMove());
				if (!success1) {
					break;
				}
				
				blueLogic.onRequestAction();
				boolean success2 = perform(blueClient.getLastMove());
				if (!success2) {
					break;
				}
			}
			
			PlayerColor winner = getWinner();
			
			ScoreDefinition scoreDef = new ScoreDefinition();
			scoreDef.add("RED");
			scoreDef.add("BLUE");
			List<PlayerScore> scores = new ArrayList<>();
			scores.add(new PlayerScore(winner == PlayerColor.RED, ""));
			scores.add(new PlayerScore(winner == PlayerColor.BLUE, ""));
			List<SimplePlayer> winners = new ArrayList<>();
			winners.add(winner == PlayerColor.RED ? state.getRedPlayer() : state.getBluePlayer());
			GameResult result = new GameResult(scoreDef, scores, winners);
			PlayerColor color = winner;
			String msg = winner + " won the game";
			
			redLogic.gameEnded(result, color, msg);
			blueLogic.gameEnded(result, color, msg);
			
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
		redLogic.onUpdate(state);
		redLogic.onUpdate(state.getCurrentPlayer(), state.getOtherPlayer());
		blueLogic.onUpdate(state);
		blueLogic.onUpdate(state.getCurrentPlayer(), state.getOtherPlayer());
	}

	public void stop() {
		stopped = true;
	}
}
