package com.thedroide.sc18.strategies;

import com.antelmann.game.GameRuntimeException;
import com.thedroide.sc18.bindings.HUIEnumPlayer;
import com.thedroide.sc18.bindings.HUIGamePlay;
import com.thedroide.sc18.bindings.HUIMove;
import com.thedroide.sc18.debug.GUILogger;

import sc.plugin2018.Action;
import sc.plugin2018.ExchangeCarrots;
import sc.plugin2018.FallBack;
import sc.plugin2018.Player;

/**
 * Provides a more-or-less good implementation
 * of a {@link HUIHeuristic} that is based
 * on player statistics.
 */
public class SmartHeuristic implements HUIHeuristic {
	private static final float GOOD_HEURISTIC = Float.POSITIVE_INFINITY;
	private static final float BAD_HEURISTIC = Float.NEGATIVE_INFINITY;
	
	private final int carrotWeight = 1;
	private final int saladWeight = 256;
	private final int fieldIndexWeight = 2;
	
	@Override
	public float heuristic(HUIGamePlay gameBeforeMove, HUIMove move, HUIEnumPlayer player) {
		if (move.isDiscarded()) {
			return BAD_HEURISTIC;
		}
		
		try {
			HUIGamePlay gameAfterMove = (HUIGamePlay) gameBeforeMove.spawnChild(move);
			Player playerAfterMove = player.getSCPlayer(gameAfterMove.getSCState());
			Player playerBeforeMove = player.getSCPlayer(gameBeforeMove.getSCState());
			Action lastAction = playerBeforeMove.getLastNonSkipAction();
			
			if (playerAfterMove.inGoal()) {
				// Obviously a very good rating if the player reaches the goal:
				GUILogger.log(playerAfterMove.getPlayerColor() + " in goal");
				return GOOD_HEURISTIC;
			} else if (lastAction instanceof ExchangeCarrots || lastAction instanceof FallBack) {
				return BAD_HEURISTIC;
			}
			
			int salads = playerAfterMove.getSalads();
			int carrots = playerAfterMove.getCarrots();
			int fieldIndex = playerAfterMove.getFieldIndex(); // Maximum field index is 64
			
			int saladRating = -(salads * saladWeight); // Less salads: better
			int fieldRating = fieldIndex * fieldIndexWeight; // Higher field: better
			int carrotRating = -Math.abs((carrots - carrotOptimum(fieldIndex)) * carrotWeight) / 4; // More or less carrots than optimum: worse
			
			float rating = saladRating + fieldRating + carrotRating;
			
//			GUILogger.log(player + ": " + rating + " results in the board " + gameAfterMove + " using " + move);
			
			return rating;
		} catch (GameRuntimeException e) {
			GUILogger.log("Warning: " + e.getMessage());
			return BAD_HEURISTIC;
		}
	}
	
	private int carrotOptimum(int fieldIndex) {
		int fieldsToGoal = 64 - fieldIndex;
		
		/*
		 * A linear function is used to determine the carrot optimum,
		 * because we want to have a bunch of carrots in the beginning,
		 * but as we approach the end of the track, we need to drop at
		 * least below 10 carrots or we won't be able to reach the goal.
		 */
		
		return (fieldsToGoal + 6) / 2;
	}

	@Override
	public boolean pruneMove(HUIGamePlay gameBeforeMove, HUIMove move, HUIEnumPlayer player) {
		try {
			HUIGamePlay gameAfterMove = (HUIGamePlay) gameBeforeMove.spawnChild(move);
			
			if (gameAfterMove.getWinner() != null) {
				GUILogger.log(
						"Found winning move: "
						+ move
						+ " by "
						+ HUIEnumPlayer.of(gameAfterMove.getWinner())
				);
				return true;
			}
			
			return false;
		} catch (Exception e) {
			GUILogger.log("ERROR: " + e.getMessage());
			return false;
		}
	}
}
