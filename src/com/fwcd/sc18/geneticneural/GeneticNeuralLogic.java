package com.fwcd.sc18.geneticneural;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fwcd.sc18.core.CopyableLogic;
import com.fwcd.sc18.core.EvaluatingLogic;
import com.fwcd.sc18.trainer.core.VirtualClient;
import com.fwcd.sc18.utils.HUIUtils;

import sc.plugin2018.AbstractClient;
import sc.plugin2018.CardType;
import sc.plugin2018.FieldType;
import sc.plugin2018.GameState;
import sc.plugin2018.Move;
import sc.plugin2018.Player;
import sc.plugin2018.util.Constants;
import sc.shared.GameResult;
import sc.shared.InvalidMoveException;

public class GeneticNeuralLogic extends EvaluatingLogic {
	private static final int MAX_FIELD = 64;
	private static final int CARROT_THRESHOLD = 360;
	private static final int FITNESS_BIAS = 5;
	private static final int WIN_FITNESS_BIAS = 10;
	private static final Logger GENETIC_LOG = LoggerFactory.getLogger("geneticlog");

	private final int populationSize = 10;
	private final int[] layerSizes = {19, 30, 10, 1};
	private final Population population;
	private final Perceptron neuralNet;
	
	public GeneticNeuralLogic(VirtualClient client) {
		super(client);
		population = new Population(populationSize, () -> Perceptron.generateWeights(layerSizes), new File("."));
		neuralNet = new Perceptron(layerSizes);
	}
	
	/*
	 * Submission TODO-list:
	 * 
	 * - Set autoRelaunch flag in PhantomClient to false
	 */
	
	public GeneticNeuralLogic(AbstractClient client) {
		super(client);
		population = new Population(populationSize, () -> Perceptron.generateWeights(layerSizes), new File("."));
		neuralNet = new Perceptron(layerSizes);
	}

	/**
	 * Copy constructor.
	 */
	public GeneticNeuralLogic(AbstractClient client, GeneticNeuralLogic other) {
		super(client);
		population = other.population;
		neuralNet = other.neuralNet;
	}

	@Override
	protected void onGameStart(GameState gameState) {
		neuralNet.setWeights(population.sample());
	}

	@Override
	protected void onGameEnd(GameState gameState, boolean won, GameResult result, String errorMessage) {
		Player me = getMe(gameState);

		int carrots = me.getCarrots();
		int field = me.getFieldIndex();
		int turn = gameState.getTurn();
		
		boolean inGoal = me.inGoal();
		float fitness;
		
		if (inGoal) {
			fitness = WIN_FITNESS_BIAS - HUIUtils.normalize(gameState.getRound(), 0, Constants.ROUND_LIMIT + 1);
		} else {
			float normCarrots = HUIUtils.normalize(me.getCarrots(), 0, CARROT_THRESHOLD);
			float normSalads = HUIUtils.normalize(me.getSalads(), 0, Constants.SALADS_TO_EAT);
			float normField = HUIUtils.normalize(me.getFieldIndex(), 0, MAX_FIELD);
			
			fitness = FITNESS_BIAS - normSalads + normField - normCarrots;
		}
		
		int counter = population.getCounter();
		int streak = population.getStreak();
		
		float totalFitness = population.updateFitness(won, neuralNet.getWeights(), fitness);
		GENETIC_LOG.debug("[{}:{}] - Carrots: {}, Field: {}, Turns: {}, Fitness: {} ({})", new Object[] {
				counter, streak, carrots, field, turn, totalFitness, (won ? (inGoal ? "won + in goal" : "won") : "lost")
		});
		
		population.evolve(won);
	}
	
	@Override
	protected float evaluateMove(Move move, GameState gameBeforeMove, Player me) {
		try {
			return neuralNet.compute(encode(HUIUtils.spawnChild(gameBeforeMove, move)))[0];
		} catch (InvalidMoveException e) {
			return Float.NEGATIVE_INFINITY;
		}
	}
	
	private float[] encode(GameState gameState) {
		Player me = getMe(gameState);
		Player opponent = getOpponent(gameState);
		List<CardType> myCards = me.getCards();
		FieldType myField = gameState.getTypeAt(me.getFieldIndex());
		
		float[] encoded = new float[layerSizes[0]];
		
		encoded[0] = 1; // Bias weight
		encoded[1] = HUIUtils.normalize(me.getCarrots(), 0, CARROT_THRESHOLD);
		encoded[2] = HUIUtils.normalize(me.getSalads(), 0, Constants.SALADS_TO_EAT);
		encoded[3] = HUIUtils.normalize(me.getFieldIndex(), 0, MAX_FIELD);
		encoded[4] = HUIUtils.normalize(gameState.getRound(), 0, Constants.ROUND_LIMIT);
		encoded[5] = HUIUtils.normalize(opponent.getFieldIndex(), 0, MAX_FIELD);
		encoded[6] = HUIUtils.normalize(opponent.getSalads(), 0, Constants.SALADS_TO_EAT);
		encoded[7] = HUIUtils.normalize(opponent.getCarrots(), 0, CARROT_THRESHOLD);
		encoded[8] = myField == FieldType.CARROT ? 1 : 0;
		encoded[9] = myField == FieldType.HARE ? 1 : 0;
		encoded[10] = myField == FieldType.HEDGEHOG ? 1 : 0;
		encoded[11] = myField == FieldType.GOAL ? 1 : 0;
		encoded[12] = myField == FieldType.POSITION_1 ? 1 : 0;
		encoded[13] = myField == FieldType.POSITION_2 ? 1 : 0;
		encoded[14] = myField == FieldType.SALAD ? 1 : 0;
		encoded[15] = myCards.contains(CardType.EAT_SALAD) ? 1 : 0;
		encoded[16] = myCards.contains(CardType.FALL_BACK) ? 1 : 0;
		encoded[17] = myCards.contains(CardType.HURRY_AHEAD) ? 1 : 0;
		encoded[18] = myCards.contains(CardType.TAKE_OR_DROP_CARROTS) ? 1 : 0;
		
		return encoded;
	}

	@Override
	public CopyableLogic copy(AbstractClient client) {
		return new GeneticNeuralLogic(client, this);
	}
}
