package com.fwcd.sc18.geneticneural;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fwcd.sc18.utils.HUIUtils;
import com.fwcd.sc18.utils.IndexedMap;
import com.fwcd.sc18.utils.MatchResult;

import sc.plugin2018.GameState;
import sc.plugin2018.Player;
import sc.plugin2018.util.Constants;

/**
 * A "simple" strategy that picks single individuals and computes
 * cumulative fitness values based off win streaks against the opponent.
 * 
 * <p>It can not be used to train against another individuals
 * in the same population though.</p>
 */
public class SoloStreakStrategy implements GeneticStrategy {
	private static final int FITNESS_BIAS = 5;
	private static final int WIN_FITNESS_BIAS = 10;
	private static final Logger GENETIC_LOG = LoggerFactory.getLogger("geneticlog");
	
	@Override
	public float[][] selectTrainingGenes(IndexedMap<float[], Float> individuals, int counter) {
		return new float[][] {individuals.getKey(counter)};
	}

	@Override
	public EvaluationResult evaluate(MatchResult result, float prevFitness, Population population) {
		Player me = result.getMe();
		GameState gameState = result.getState();
		int carrots = me.getCarrots();
		int salads = me.getSalads();
		int field = me.getFieldIndex();
		int turn = result.getTurn();
		boolean won = result.isWon();
		boolean inGoal = me.inGoal();
		float fitness;
		
		if (inGoal) {
			fitness = WIN_FITNESS_BIAS
					- HUIUtils.normalize(gameState.getRound(), 0, Constants.ROUND_LIMIT + 1);
		} else {
			float normCarrots = HUIUtils.normalize(carrots, 0, HUIUtils.CARROT_THRESHOLD);
			float normSalads = HUIUtils.normalize(salads, 0, Constants.SALADS_TO_EAT);
			float normField = HUIUtils.normalize(field, 0, HUIUtils.MAX_FIELD);
			
			fitness = FITNESS_BIAS - normSalads + normField - normCarrots;
		}
		
		int counter = population.getCounter();
		int streak = population.getStreak();
		
		float totalFitness = (streak > 0 ? prevFitness : 0) + fitness;
		boolean nextIndividual = !won && streak >= 1;
		
		GENETIC_LOG.debug("[{}:{}] - Carrots: {}, Field: {}, Turns: {}, Fitness: {} ({})", new Object[] {
				counter, streak, carrots, field, turn, totalFitness, (won ? (inGoal ? "won + in goal" : "won") : "lost")
		});
		
		return new EvaluationResult(totalFitness, nextIndividual ? 1 : 0);
	}
}
