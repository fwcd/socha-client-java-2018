package com.thedroide.sc18.heuristics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thedroide.sc18.core.HUIGameState;
import com.thedroide.sc18.core.HUIMove;
import com.thedroide.sc18.core.HUIPlayerColor;

import sc.plugin2018.Player;
import sc.plugin2018.util.Constants;

/**
 * Provides a more-or-less good implementation
 * of a {@link HUIHeuristic} that is based
 * on player statistics.
 */
public class StatsHeuristic implements HUIHeuristic {
	private static final Logger LOG = LoggerFactory.getLogger("ownlog");
	private static final double GOOD_HEURISTIC = Double.POSITIVE_INFINITY;
	private static final double BAD_HEURISTIC = Double.NEGATIVE_INFINITY;
	
	// All weights should be (almost) equally effective to due normalization
	
	private final int carrotWeight; // Higher values priorize carrot optimization
	private final int saladWeight; // Higher values priorize salad reduction (which is ALWAYS a good thing)
	private final int fieldIndexWeight; // Higher values priorize advance
	private final int turnWeight; // Higher values priorize fast play
	
	public StatsHeuristic() {
		carrotWeight = 2;
		saladWeight = 16384;
		fieldIndexWeight = 1;
		turnWeight = 8;
	}
	
	public StatsHeuristic(int carrotWeight, int saladWeight, int fieldIndexWeight, int turnWeight) {
		this.carrotWeight = carrotWeight;
		this.saladWeight = saladWeight;
		this.fieldIndexWeight = fieldIndexWeight;
		this.turnWeight = turnWeight;
	}
	
	/**
	 * Calculates a domain-specific game state evaluation.
	 */
	@Override
	public double heuristic(
			HUIGameState gameBeforeMove,
			HUIGameState gameAfterMove,
			HUIMove move,
			HUIPlayerColor player
	) {
		try {
			if (move.isDiscarded()) {
				return BAD_HEURISTIC;
			}
			
			Player playerAfterMove = gameAfterMove.getSCPlayer(player);
			double playerRating;
			
			if (playerAfterMove.inGoal()) {
				playerRating = GOOD_HEURISTIC; // Obviously a very good rating if the player reaches the goal
			} else {
				playerRating = rate(
						playerAfterMove.getSalads(),
						playerAfterMove.getCarrots(),
						playerAfterMove.getFieldIndex()
				);
			}
			
			// Value normalized from approximately [0 to 64] to the range [0 to 320] where higher is better
			
			int normTurn = 320 - (gameAfterMove.getTurn() * 5); // Higher turn = worse
			
			return playerRating - (normTurn * turnWeight);
		} catch (Exception e) {
			LOG.warn("Exception while calculating heuristic: ", e);
			return BAD_HEURISTIC;
		}
	}
	
	/**
	 * Rates a player given it's current attributes. This method
	 * is not just essential to the heuristic, it allows for easy testing too.
	 */
	public double rate(int salads, int carrots, int fieldIndex) {
		// Values that are normalized to the range [0 to 320] where higher is better
		// (except for the carrots, which should be contained most of the time but are not bounded)
		
		int normSalads = (Constants.SALADS_TO_EAT - salads) * 64; // Less salads = better
		int normFields = fieldIndex * 5; // Higher field = better
		int normCarrots = -Math.abs(carrots - carrotOptimum(fieldIndex)) * 5; // More or less carrots than optimum = worse
		
		// Ratings using a weighted function
		
		int saladRating = normSalads * saladWeight;
		int fieldRating = normFields * fieldIndexWeight;
		int carrotRating = normCarrots * carrotWeight;
		
		return saladRating + fieldRating + carrotRating;
	}
	
	/**
	 * Determines the "optimal amount of carrots" given a
	 * specific field index.
	 */
	public int carrotOptimum(int fieldIndex) {
		int fieldsToGoal = 64 - fieldIndex;
		
		/*
		 * A linear function is used to determine the carrot optimum,
		 * because we want to have a bunch of carrots in the beginning,
		 * but as we approach the end of the track, we need to drop at
		 * least below 10 carrots or we won't be able to reach the goal.
		 */
		
		return (fieldsToGoal + 6) / 2;
	}

	public int getCarrotWeight() {
		return carrotWeight;
	}

	public int getSaladWeight() {
		return saladWeight;
	}

	public int getFieldIndexWeight() {
		return fieldIndexWeight;
	}

	public int getTurnWeight() {
		return turnWeight;
	}

	@Override
	public String toString() {
		return "[StatsHeuristic] "
				+ "Carrot weight: " + Integer.toString(carrotWeight)
				+ "Salad weight: " + Integer.toString(saladWeight)
				+ "Field index weight: " + Integer.toString(fieldIndexWeight)
				+ "Turn weight: " + Integer.toString(turnWeight);
	}

	@Override
	public String shortToString() {
		return "{C: " + Integer.toString(carrotWeight)
				+ ", S: " + Integer.toString(saladWeight)
				+ ", F: " + Integer.toString(fieldIndexWeight)
				+ ", T: " + Integer.toString(turnWeight)
				+ "}";
	}
}
