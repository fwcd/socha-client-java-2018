package com.thedroide.sc18.strategies;

import com.antelmann.game.GameRuntimeException;
import com.thedroide.sc18.bindings.HUIEnumPlayer;
import com.thedroide.sc18.bindings.HUIGamePlay;
import com.thedroide.sc18.bindings.HUIMove;
import com.thedroide.sc18.debug.GUILogger;

import sc.plugin2018.Action;
import sc.plugin2018.ExchangeCarrots;
import sc.plugin2018.FallBack;
import sc.plugin2018.FieldType;
import sc.plugin2018.Player;

/**
 * Provides a more-or-less good implementation
 * of a {@link HUIHeuristic} that is based
 * on player statistics.
 */
public class SmartHeuristic implements HUIHeuristic {
	private final int carrotWeight = 1;
	private final int saladWeight = 128;
	private final int fieldIndexWeight = 1;
	
	// TODO: Prevent "drop carrot"/"take carrot"-loop somehow
	
	@Override
	public double heuristic(HUIGamePlay gameBeforeMove, HUIMove move, HUIEnumPlayer player) {
		try {
			HUIGamePlay gameAfterMove = (HUIGamePlay) gameBeforeMove.spawnChild(move);
			
			Player playerAfterMove = player.getSCPlayer(gameAfterMove.getSCState());
			
			if (playerAfterMove.inGoal()) {
				// Obviously a very good rating if the player reaches the goal:
				return Double.MAX_VALUE;
			}
			
			int salads = playerAfterMove.getSalads();
			int carrots = playerAfterMove.getCarrots();
			int fieldIndex = playerAfterMove.getFieldIndex(); // Maximum field index is 64
			
			int saladRating = -(salads * saladWeight); // Less salads: better
			int fieldRating = fieldIndex * fieldIndexWeight; // Higher field: better
			int carrotRating = -Math.abs((carrots - carrotOptimum(fieldIndex)) * carrotWeight) / 4; // More or less carrots than optimum: worse
			
			double rating = saladRating + fieldRating + carrotRating;
			
//			GUILogger.log(player + ": " + rating + " results in the board " + gameAfterMove + " using " + move);
			
			return rating;
		} catch (GameRuntimeException e) {
			GUILogger.log("Warning: " + e.getMessage());
			return Double.MIN_VALUE;
		}
	}
	
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

	@Override
	public boolean pruneMove(HUIGamePlay gameBeforeMove, HUIMove move, HUIEnumPlayer player) {
		Player playerBeforeMove = player.getSCPlayer(gameBeforeMove.getSCState());
		
		for (Action action : move.getSCMove().getActions()) {
			if (action instanceof ExchangeCarrots) {
				ExchangeCarrots carrotAction = (ExchangeCarrots) action;
				
				if (carrotAction.getValue() > 0 // When picking up carrots
						|| playerBeforeMove.getCarrots() > 30
						|| playerBeforeMove.getFieldIndex() > 40
						|| playerBeforeMove.getLastNonSkipAction() instanceof ExchangeCarrots) {
					// Bad multiplier if player is near goal, wants to pick up
					// too many carrots or already has commited a carrot move previously
					return true;
				} else if (carrotAction.getValue() < 0 // When dropping carrots
						|| playerBeforeMove.getCarrots() < 30
						|| playerBeforeMove.getFieldIndex() < 40) {
					// Bad multiplier if player has to few carrots or is near
					// the start and wants to drop carrots.
					return true;
				}
				
			} else if (action instanceof FallBack) {
				final int lastSaladField = 56;
				
				if (playerBeforeMove.getFieldIndex() > lastSaladField
						|| playerBeforeMove.getFieldIndex() - gameBeforeMove.getSCState()
								.getPreviousFieldByType(FieldType.HEDGEHOG, playerBeforeMove.getFieldIndex()) >= 5) {
					// Be cautious when falling back because we
					// don't want to pick up too many carrots.
					return true;
				}
			}
		}
		
		return false;
	}
}
