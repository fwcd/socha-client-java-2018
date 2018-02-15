package com.fwcd.sc18.geneticneural;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fwcd.sc18.core.EvaluatingLogic;

import sc.plugin2018.AbstractClient;
import sc.plugin2018.CardType;
import sc.plugin2018.GameState;
import sc.plugin2018.Move;
import sc.plugin2018.Player;
import sc.shared.GameResult;
import sc.shared.PlayerColor;

public class GeneticNeuralLogic extends EvaluatingLogic {
	private static final Logger GENETIC_LOG = LoggerFactory.getLogger("geneticlog");
	
	private final int encodedBoardSize = 14;
	private final float winFactor = 100;
	
	private final int[] layerSizes = {encodedBoardSize, 10, 1};
	private final Population population = new Population(10, () -> Perceptron.generateWeights(layerSizes));
	private final Perceptron neuralNet = new Perceptron(layerSizes);
	
	private int alphaBetaDepth = 2; // TODO: Tweak this parameter
	
	// FIXME: Relaunching the client is useless, currently, as the population details are not preserved
	// FIXME: Some debugging thus is required
	
	public GeneticNeuralLogic(AbstractClient client) {
		super(client);
	}

	@Override
	protected void gameStarted(GameState gameState) {
		neuralNet.setWeights(population.sample());
	}

	@Override
	public void gameEnded(GameResult result, PlayerColor color, String errorMessage) {
		Player me = getMe();
		float fitness = (
				invertNormalize(me.getCarrots(), 0, 200)
				+ invertNormalize(me.getSalads(), 0, 5)
				+ normalize(me.getFieldIndex(), 0, 64)
				+ invertNormalize(me.getCards().size(), 0, 4)
				) * (color == me.getPlayerColor() ? winFactor : -winFactor);
		
		population.setFitness(neuralNet.getWeights(), fitness);
		GENETIC_LOG.info("Finished game with fitness {}", fitness);
	}
	
	private float evaluateLeaf(Move move, GameState gameAfterMove) {
		return neuralNet.compute(encode(gameAfterMove))[0];
	}
	
	private float alphaBeta(
			boolean maximizing,
			Move move,
			GameState gameBeforeMove,
			int depth,
			float alpha,
			float beta
	) {
		GameState gameAfterMove = spawnChild(gameBeforeMove, move);
		
		if (depth == 0 || isGameOver(gameAfterMove)) {
			return evaluateLeaf(move, gameAfterMove);
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
		
		encoded[0] = normalize(me.getCarrots(), 0, 200);
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
}
