package com.thedroide.sc18.minmax;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.antelmann.game.GameDriver;
import com.thedroide.sc18.minmax.core.HUIEnumPlayer;
import com.thedroide.sc18.minmax.core.HUIGamePlay;
import com.thedroide.sc18.minmax.core.HUIMove;
import com.thedroide.sc18.minmax.strategies.ShallowStrategy;
import com.thedroide.sc18.minmax.strategies.SimpleStrategy;
import com.thedroide.sc18.utils.GUILogger;

import sc.plugin2018.AbstractClient;
import sc.plugin2018.GameState;
import sc.plugin2018.IGameHandler;
import sc.plugin2018.Move;
import sc.plugin2018.Player;
import sc.shared.GameResult;
import sc.shared.PlayerColor;

// TODO: Clean up GUILogger calls before handing in the client

/**
 * A "smart" logic combining the best of alpha-beta tree search,
 * game heuristics and simple (shallow) logic.
 */
public class MinmaxLogic implements IGameHandler {
	private static final Logger STD_LOG = LoggerFactory.getLogger(MinmaxLogic.class);
	
	// === Parameters that may be tweaked and tested: ==
	
	private int minSearchDepth = 1; // Used at the beginning because of slow JVM startup
	private int maxSearchDepth = 12;
	private boolean dynamicSearchDepth = true; // Whether to dynamically modify search depth based off response times
	
	private int minTime = 200; // in ms - Minimum move time, causes dynamic search to increate depth at next iteration
	private int softMaxTime = 1200; // in ms - Soft time limit, causes dynamic search to decrease depth at next iteration
	private int stdMaxTime = 1500; // in ms - Standard time limit, causes AI to finish all current evaluations and return
	private int hardMaxTime = 1800; // in ms - Hard move time limit, instantly returns a move (evaluator threads finish in background)
	
	// == End of parameters ==
	
	private AbstractClient client;
	private Player currentPlayer;
	
	private int depth = minSearchDepth;
	private int committedMoves = 0;
	
	private final ShallowStrategy shallowStrategy = new SimpleStrategy();
	private final HUIGamePlay game = new HUIGamePlay();
	private final GameDriver ai = new GameDriver(game, HUIEnumPlayer.getPlayers(), depth);
	
	private AIThread aiThread = null;
	private HUIMove aiMove = null;
	
	/**
	 * Creates a new AI-player that commits moves.
	 * 
	 * @param SochaClientMain - The client itself
	 */
	public MinmaxLogic(AbstractClient client) {
		this.client = client;
		
		ai.setResponseTime(stdMaxTime);
	}
	
	/**
	 * An event handler for the game ending.
	 */
	@Override
	public void gameEnded(GameResult data, PlayerColor color, String errorMessage) {
		GUILogger.println("  ======================  ");
		GUILogger.println("    " + data.getWinners().get(0).getDisplayName().toString() + " WINS!");
		GUILogger.println("  ======================  ");
		STD_LOG.info("Game ended.");
	}
	
	/**
	 * Called whenever a turn is requested.
	 * This method contains the relevant code.
	 */
	@Override
	public void onRequestAction() {
		long startTime = System.currentTimeMillis();
		STD_LOG.info("Move requested.");
		
		GUILogger.println(
				"[Turn]\t"
				+ game.nextHUIEnumPlayer().name()
				+ " with board "
				+ game.toString()
				+ " and tree depth "
				+ Integer.toString(depth)
		);
		
		if (!dynamicSearchDepth && committedMoves == 1) {
			setDepth(maxSearchDepth);
		}
		
		// Picks the best move from the AI
		
		if (aiThread != null) {
			aiThread.discard(); // Discard old thread
		}
		
		aiThread = new AIThread(ai);
		aiThread.start();
		
		if (aiThread.join(hardMaxTime)) {
			aiMove = aiThread.getNullableMove();
		} else {
			aiMove = null;
		}
		
		if (aiMove == null) {
			// This is a "Killswitch" to handle the case where the AI doesn't return in time
			aiMove = shallowStrategy.bestMove(game);
		}
		
		// Some boilerplate required to send the move
		
		Move scMove = aiMove.getSCMove();
		scMove.orderActions();
		sendAction(scMove);
		
		// Dynamic customization of response times
		
		committedMoves++;
		int responseTime = (int) (System.currentTimeMillis() - startTime);
		
		if (dynamicSearchDepth) {
			if (responseTime < minTime && depth < maxSearchDepth) {
				setDepth(++depth);
			} else if (responseTime > softMaxTime && depth > minSearchDepth) {
				setDepth(--depth);
			};
		}
		
		GUILogger.println("[Committed]\t" + aiMove + " in " + Integer.toString(responseTime) + "ms");
	}
	
	/**
	 * Sets the game tree depth.
	 * 
	 * @param depth - The new depth of the search tree
	 */
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
		STD_LOG.info("Switching turns: " + player.getPlayerColor());
	}

	/**
	 * An event handler that get's called whenever the board
	 * updates.
	 */
	@Override
	public void onUpdate(GameState gameState) {
		currentPlayer = gameState.getCurrentPlayer();

		// TODO: Let the GameDriver calculate silently while the opponent moves (multithreading?)
		
		game.setState(gameState);
		
		STD_LOG.info("New move: {}", gameState.getTurn());
		STD_LOG.info("Player: {}", currentPlayer.getPlayerColor());
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
