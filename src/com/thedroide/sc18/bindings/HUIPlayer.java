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

	private final int carrotWeight = 4;
	private final int saladWeight = 32;
	private final int fieldIndexWeight = 1;
	
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
			
			HUIEnumPlayer huiEnumPlayer = HUIEnumPlayer.of(huiGame.getLastPlayer());
			Player scPlayer = huiEnumPlayer.getSCPlayer(huiGame.getSCState());
			
			if (scPlayer.inGoal()) {
				// Obviously a very good rating when the player reaches the goal:
				return Double.MAX_VALUE;
			} else {
				int salads = scPlayer.getSalads();
				int carrots = scPlayer.getCarrots();
				int fieldIndex = scPlayer.getFieldIndex(); // Maximum field index is 64
				
				double rating = (fieldIndex * fieldIndexWeight) // Large field-index: better
						- (salads * saladWeight) // Less salads: better
						- Math.abs((carrots - carrotOptimum(fieldIndex)) * carrotWeight); // More or less carrots than optimum: worse
				
				GUILogger.log(huiEnumPlayer + ": " + rating + " results in the board " + huiGame + " using " + move);
				
				return rating;
			}
		} catch (ClassCastException e) {
			throw new CannotPlayGameException(this, game, "Invalid game type.");
		}
	}
	
	// TODO: Track previous moves to prevent player from being stuck in a "drop carrot"/"take carrot"-loop
	
	private int carrotOptimum(int fieldIndex) {
		int fieldsToGoal = fieldIndex - 64;
		
		/*
		 * A linear function is used to determine the carrot optimum,
		 * because we want to have a bunch of carrots in the beginning,
		 * but as we approach the end of the track, we need to drop at
		 * least below 10 carrots or we won't be able to reach the goal.
		 */
		
		return (fieldsToGoal + 6) / 2;
	}
}
