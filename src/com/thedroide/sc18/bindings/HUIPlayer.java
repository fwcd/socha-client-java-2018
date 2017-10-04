package com.thedroide.sc18.bindings;

import com.antelmann.game.CannotPlayGameException;
import com.antelmann.game.GameMove;
import com.antelmann.game.GamePlay;
import com.antelmann.game.GameRuntimeException;
import com.antelmann.game.TemplatePlayer;
import com.thedroide.sc18.debug.GUILogger;

import sc.plugin2018.Player;

/**
 * Represents an abstract player containing AI
 * logic that can evaluate a given game state.
 */
public class HUIPlayer extends TemplatePlayer {
	private static final long serialVersionUID = -2746100695353269130L;

	private final int carrotWeight = 5;
	private final int saladWeight = 25;
	private final int fieldIndexWeight = 1;
	private final int carrotOptimum = 8;
	
	/**
	 * Checks if the given GamePlay is a valid
	 * "Hase und Igel"-GamePlay.
	 */
	@Override
	public boolean canPlayGame(GamePlay game) {
		if (game instanceof HUIGamePlay) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Calculates a domain-specific heuristic for
	 * "Hase und Igel". Might need some improvement.
	 */
	@Override
	public double heuristic(GamePlay game, GameMove move, int[] role) throws CannotPlayGameException, GameRuntimeException {
		try {
			HUIGamePlay huiGame = (HUIGamePlay) game.spawnChild(move);
			
			// Choosing the opponent here (otherwise it won't work), is this a bug? TODO
			HUIEnumPlayer huiEnumPlayer = HUIEnumPlayer.of(role).getOpponent();
			Player scPlayer = huiEnumPlayer.getSCPlayer(huiGame.getSCState());
			
			if (scPlayer.inGoal()) {
				return Double.MAX_VALUE; // Obviously a very good rating when the player reaches the goal
			} else {
				int salads = scPlayer.getSalads();
				int carrots = scPlayer.getCarrots();
				int fieldIndex = scPlayer.getFieldIndex();
				
				double rating = (fieldIndex * fieldIndexWeight) // Large field-index: better
						- (salads * saladWeight) // Less salads: better
						- Math.abs((carrots - carrotOptimum) * carrotWeight); // More or less carrots than optimum: worse
				
				GUILogger.log(huiEnumPlayer + ": " + rating + " results in the board " + huiGame + " using " + move);
				
				return rating;
			}
		} catch (ClassCastException e) {
			throw new CannotPlayGameException(this, game, "Invalid game type.");
		}
	}
}
