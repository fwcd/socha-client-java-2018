package fwcd.sc18.trainer.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BooleanSupplier;

import fwcd.sc18.trainer.ui.GameView;

import sc.framework.plugins.SimplePlayer;
import sc.plugin2018.GameState;
import sc.plugin2018.IGameHandler;
import sc.plugin2018.Move;
import sc.plugin2018.Player;
import sc.plugin2018.util.Constants;
import sc.shared.GameResult;
import sc.shared.InvalidGameStateException;
import sc.shared.InvalidMoveException;
import sc.shared.PlayerColor;
import sc.shared.PlayerScore;
import sc.shared.ScoreDefinition;

public class GameSimulator {
	private final Optional<GameView> view;
	private final IGameHandler logicA;
	private final IGameHandler logicB;
	private final VirtualClient clientA;
	private final VirtualClient clientB;
	private final long matches;
	private final List<Runnable> gameEndListeners;

	private BooleanSupplier stopCondition = null;
	private GameState state = new GameState();
	private boolean started = false;
	private boolean stopped = false;
	
	@FunctionalInterface
	public static interface LogicConstructor {
		IGameHandler createLogic(VirtualClient client);
	}
	
	public GameSimulator(LogicConstructor a, LogicConstructor b, long matches) {
		this.matches = matches;
		view = Optional.empty();
		gameEndListeners = Collections.emptyList();
		
		clientA = new VirtualClient(PlayerColor.RED);
		logicA = a.createLogic(clientA);
		clientB = new VirtualClient(PlayerColor.BLUE);
		logicB = b.createLogic(clientB);
	}
	
	public GameSimulator(
			Optional<GameView> view,
			IGameHandler logicA,
			IGameHandler logicB,
			VirtualClient clientA,
			VirtualClient clientB,
			long matches,
			List<Runnable> gameEndListeners
	) {
		this.view = view;
		this.logicA = logicA;
		this.logicB = logicB;
		this.clientA = clientA;
		this.clientB = clientB;
		this.matches = matches;
		this.gameEndListeners = gameEndListeners;
	}
	
	public void setStopCondition(BooleanSupplier stopCondition) {
		this.stopCondition = stopCondition;
	}
	
	public void run() {
		if (started) {
			throw new IllegalStateException("GameSimulator already started.");
		} else if (stopped) {
			throw new IllegalStateException("GameSimulator already stopped.");
		} else {
			started = true;
		}
		
		Random random = ThreadLocalRandom.current();
		
		long match = 0;
		while (match < matches && !shouldStop()) {
			state = new GameState();
			updateState();
			
			IGameHandler redLogic;
			IGameHandler blueLogic;
			VirtualClient redClient;
			VirtualClient blueClient;
			
			if (random.nextBoolean()) {
				redLogic = logicA;
				redClient = clientA;
				blueLogic = logicB;
				blueClient = clientB;
			} else {
				redLogic = logicB;
				redClient = clientB;
				blueLogic = logicA;
				blueClient = clientA;
			}
			
			redClient.setColor(PlayerColor.RED);
			blueClient.setColor(PlayerColor.BLUE);
			
			int round = 0;
			while (round < Constants.ROUND_LIMIT && getWinner() == null) {
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
				
				round++;
			}
			
			PlayerColor winner = getWinner();
			
			ScoreDefinition scoreDef = new ScoreDefinition();
			scoreDef.add("RED");
			scoreDef.add("BLUE");
			List<PlayerScore> scores = new ArrayList<>();
			scores.add(new PlayerScore(winner == PlayerColor.RED, ""));
			scores.add(new PlayerScore(winner == PlayerColor.BLUE, ""));
			List<SimplePlayer> winners = new ArrayList<>();
			winners.add(state.getPlayer(winner));
			GameResult result = new GameResult(scoreDef, scores, winners);
			PlayerColor color = winner;
			String msg = winner + " won the game";
			
			redLogic.gameEnded(result, color, msg);
			blueLogic.gameEnded(result, color, msg);
			
			for (Runnable listener : gameEndListeners) {
				listener.run();
			}
			
			match++;
		}
	}

	private boolean shouldStop() {
		return stopped
				|| (stopCondition == null ? false : stopCondition.getAsBoolean())
				|| Thread.interrupted();
	}

	private PlayerColor getWinner() {
		Player red = state.getPlayer(PlayerColor.RED);
		Player blue = state.getPlayer(PlayerColor.BLUE);
		
		if (red.inGoal()) {
			return PlayerColor.RED;
		} else if (blue.inGoal()) {
			return PlayerColor.BLUE;
		} else if (state.getRound() >= Constants.ROUND_LIMIT) {
			return red.getFieldIndex() > blue.getFieldIndex() ? PlayerColor.RED : PlayerColor.BLUE;
		} else {
			return null;
		}
	}

	private boolean perform(Move move) {
		try {
			move.perform(state);
			updateState();
			return true;
		} catch (InvalidMoveException | InvalidGameStateException e) {
			return false;
		}
	}

	private void updateState() {
		view.ifPresent(view -> view.update(state));
		logicA.onUpdate(state);
		logicA.onUpdate(state.getCurrentPlayer(), state.getOtherPlayer());
		logicB.onUpdate(state);
		logicB.onUpdate(state.getCurrentPlayer(), state.getOtherPlayer());
	}

	public synchronized void stop() {
		stopped = true;
	}
}
