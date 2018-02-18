package com.fwcd.sc18.geneticneural;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fwcd.sc18.core.CopyableLogic;
import com.fwcd.sc18.core.EvaluatingLogic;
import com.fwcd.sc18.utils.HUIUtils;

import sc.plugin2018.AbstractClient;
import sc.plugin2018.CardType;
import sc.plugin2018.GameState;
import sc.plugin2018.Move;
import sc.plugin2018.Player;
import sc.shared.GameResult;
import sc.shared.InvalidMoveException;

public class GeneticNeuralLogic extends EvaluatingLogic {
	private static final Logger GENETIC_LOG = LoggerFactory.getLogger("geneticlog");

	private final int encodedBoardSize = 14;
	private final int[] layerSizes = {encodedBoardSize, 10, 1};
	private final Population population;
	private final Perceptron neuralNet;

	private final float winFactor = 2048;
	private int alphaBetaDepth = 0; // FIXME: Tweak this parameter
	
	/*
	 * Submission TODO-list:
	 * 
	 * - Increase alpha beta to a feasible depth
	 * - Change population sample strategy to a more greedy one
	 * - Set autoRelaunch flag in PhantomClient to false
	 */
	
	public GeneticNeuralLogic(AbstractClient client) {
		super(client);
		population = new Population(20, () -> Perceptron.generateWeights(layerSizes), new File("."));
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
		Player me = getMe();
		
		int turn = gameState.getTurn();
		boolean inGoal = me.inGoal();
		float fitness;
		
		GENETIC_LOG.debug("=========================");
		GENETIC_LOG.debug("Turns: {}", turn);
		
		if (inGoal) {
			fitness = (invertNormalize(turn, 0, 60) + 1) * winFactor;
		} else {
			int carrots = me.getCarrots();
			int field = me.getFieldIndex();
			
			float normCarrots = invertNormalize(me.getCarrots(), 0, 200);
			float normSalads = invertNormalize(me.getSalads(), 0, 5);
			float normField = normalize(me.getFieldIndex(), 0, 64);
			float normCards = invertNormalize(me.getCards().size(), 0, 4);
			
			fitness = normCarrots + normSalads + normField + normCards;
			GENETIC_LOG.debug("Carrots: {}, field: {}", carrots, field);
		}
		
		population.updateFitness(neuralNet.getWeights(), fitness);
		population.evolve(!inGoal);
		GENETIC_LOG.info("Finished game with fitness {} ({})", fitness, (won ? (inGoal ? "won + in goal" : "won") : "lost"));
		GENETIC_LOG.debug("Individuals: {}", population);
	}

	private float evaluateLeaf(boolean maximizing, Move move, GameState gameBeforeMove, GameState gameAfterMove) {
		Player me = getMe(gameAfterMove);
		Player opponent = getOpponent(gameAfterMove);
		float myRating;
		
		if (me.inGoal()) {
			myRating = 10000000 - gameAfterMove.getTurn();
		} else if (opponent.inGoal()) {
			myRating = -10000000 + gameAfterMove.getTurn();
		} else {
			myRating = neuralNet.compute(encode(gameAfterMove))[0];
		}
		
		return maximizing ? myRating : -myRating;
	}
	
	private float alphaBeta(
			boolean maximizing,
			Move move,
			GameState gameBeforeMove,
			int depth,
			float alpha,
			float beta
	) {
		GameState gameAfterMove;
		try {
			gameAfterMove = HUIUtils.spawnChild(gameBeforeMove, move);
		} catch (InvalidMoveException e) {
			if (maximizing) {
				return Float.NEGATIVE_INFINITY;
			} else {
				return Float.POSITIVE_INFINITY;
			}
		}
		
		if (depth <= 0 || HUIUtils.isGameOver(gameAfterMove)) {
			return evaluateLeaf(maximizing, move, gameBeforeMove, gameAfterMove);
		} else {
			float bestRating = maximizing ? alpha : beta;
			
			for (Move childMove : gameAfterMove.getPossibleMoves()) {
				// TODO: Timer?
				float rating;
				
				if (maximizing) {
					rating = alphaBeta(!maximizing, childMove, gameAfterMove, depth - 1, bestRating, beta);
					if (rating > bestRating) {
						bestRating = rating;
						if (bestRating >= beta) {
							break; // Beta-cutoff
						}
					}
				} else {
					rating = alphaBeta(!maximizing, childMove, gameAfterMove, depth - 1, alpha, bestRating);
					if (rating < bestRating) {
						bestRating = rating;
						if (bestRating <= alpha) {
							break; // Alpha-cutoff
						}
					}
				}
			}
			
			return bestRating;
		}
	}
	
	@Override
	protected float evaluateMove(Move move, GameState gameBeforeMove, Player me) {
		return alphaBeta(true, move, gameBeforeMove, alphaBetaDepth, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
	}
	
	private float[] encode(GameState gameState) {
		Player me = getMe(gameState);
		Player opponent = getOpponent(gameState);
		List<CardType> myCards = me.getCards();
		List<CardType> opponentsCards = opponent.getCards();
		float[] encoded = new float[encodedBoardSize];
		
		encoded[0] = normalize(me.getCarrots(), 0, 360);
		encoded[1] = normalize(me.getSalads(), 0, 5);
		encoded[2] = normalize(me.getFieldIndex(), 0, 64);
		encoded[3] = myCards.contains(CardType.EAT_SALAD) ? 1 : 0;
		encoded[4] = myCards.contains(CardType.FALL_BACK) ? 1 : 0;
		encoded[5] = myCards.contains(CardType.HURRY_AHEAD) ? 1 : 0;
		encoded[6] = myCards.contains(CardType.TAKE_OR_DROP_CARROTS) ? 1 : 0;
		encoded[7] = normalize(opponent.getCarrots(), 0, 200);
		encoded[8] = normalize(opponent.getSalads(), 0, 5);
		encoded[9] = normalize(opponent.getFieldIndex(), 0, 64);
		encoded[10] = opponentsCards.contains(CardType.EAT_SALAD) ? 1 : 0;
		encoded[11] = opponentsCards.contains(CardType.FALL_BACK) ? 1 : 0;
		encoded[12] = opponentsCards.contains(CardType.HURRY_AHEAD) ? 1 : 0;
		encoded[13] = opponentsCards.contains(CardType.TAKE_OR_DROP_CARROTS) ? 1 : 0;
		
		return encoded;
	}
	
	private float invertNormalize(float x, float min, float max) {
		return normalize(max - x, min, max);
	}
	
	private float normalize(float x, float min, float max) {
		return (x - min) / (max - min);
	}

	@Override
	public CopyableLogic copy(AbstractClient client) {
		return new GeneticNeuralLogic(client, this);
	}
}
