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
import sc.shared.PlayerColor;

public class GeneticNeuralLogic extends EvaluatingLogic {
	private static final Logger GENETIC_LOG = LoggerFactory.getLogger("geneticlog");

	private final int encodedBoardSize = 14;
	private final int[] layerSizes = {encodedBoardSize, 10, 1};
	private final Population population;
	private final Perceptron neuralNet;

	private final float winFactor = 2048;
	private int alphaBetaDepth = 0; // FIXME: Tweak this parameter

	private float fieldWeight = 8;
	private float saladWeight = 2;
	private float carrotWeight = 1;
	private float cardsWeight = 1;
	
	/*
	 * Submission TODO-list:
	 * 
	 * - Increase alpha beta to a feasible depth
	 * - Change population sample strategy to a more greedy one
	 * - Set autoRelaunch flag in PhantomClient to false
	 */
	
	public GeneticNeuralLogic(AbstractClient client) {
		super(client);
		population = new Population(40, () -> Perceptron.generateWeights(layerSizes), new File("."));
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
		Player opponent = getOpponent();
		float fitness;
		
		GENETIC_LOG.debug("=========================");
		GENETIC_LOG.debug("Turn {}", gameState.getTurn());
		
		if (me.inGoal()) {
			fitness = invertNormalize(gameState.getTurn(), 0, 60) * winFactor;
		} else {
			int carrots = me.getCarrots();
			int field = me.getFieldIndex();
			
			float normCarrots = invertNormalize(me.getCarrots(), 0, 360) * carrotWeight;
			float normSalads = invertNormalize(me.getSalads(), 0, 5) * saladWeight;
			float normField = normalize(me.getFieldIndex(), 0, 64) * fieldWeight;
			float normCards = invertNormalize(me.getCards().size(), 0, 4) * cardsWeight;
			float winBias = (won ? 8 : (opponent.inGoal() ? -16 : -8));
			
			fitness = normCarrots + normSalads + normField + normCards + winBias;
			GENETIC_LOG.debug("Carrots: {}, field: {}", carrots, field);
		}
		
		population.updateFitness(neuralNet.getWeights(), fitness);
		population.evolve();
		GENETIC_LOG.info("Finished game with fitness {} ({})", fitness, (won ? "won" : "lost"));
		GENETIC_LOG.debug("Individuals: {}", population);
	}

	private float evaluateLeaf(Move move, GameState gameBeforeMove, GameState gameAfterMove) {
		PlayerColor winner = HUIUtils.getWinnerOrNull(gameBeforeMove);
		PlayerColor me = getMyColor();
		PlayerColor opponent = getOpponentColor();
		
		if (winner == me) {
			return 10000000 - gameAfterMove.getTurn();
		} else if (winner == opponent) {
			return -10000000 + gameAfterMove.getTurn();
		} else {
			return neuralNet.compute(encode(gameAfterMove))[0];
		}
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
			return Float.NaN;
		}
		
		if (depth <= 0 || HUIUtils.isGameOver(gameAfterMove)) {
			return evaluateLeaf(move, gameBeforeMove, gameAfterMove);
		} else {
			float bestRating = maximizing ? alpha : beta;
			
			for (Move childMove : gameAfterMove.getPossibleMoves()) {
				// TODO: Timer?
				float rating;
				
				if (maximizing) {
					rating = alphaBeta(!maximizing, childMove, gameAfterMove, depth - 1, bestRating, beta);
					if (!Float.isNaN(rating) && rating > bestRating) {
						bestRating = rating;
						if (bestRating >= beta) {
							break; // Beta-cutoff
						}
					}
				} else {
					rating = alphaBeta(!maximizing, childMove, gameAfterMove, depth - 1, alpha, bestRating);
					if (!Float.isNaN(rating) && rating < bestRating) {
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
