package com.fwcd.sc18.geneticneural;

import com.fwcd.sc18.utils.IndexedMap;
import com.fwcd.sc18.utils.MatchResult;

/**
 * An abstraction layer that encapsulates a training approach
 * (including a fitness function and a selection strategy).
 */
public interface GeneticStrategy {
	float[][] selectTrainingGenes(IndexedMap<float[], Float> individuals, int counter);
	
	EvaluationResult evaluate(MatchResult result, float prevFitness, Population population);
	
	default void onPreNextGeneration(Population population) {}
	
	default void onPostNextGeneration(Population population) {}
	
	// Empty implementation
	
	final class None implements GeneticStrategy {
		@Override
		public float[][] selectTrainingGenes(IndexedMap<float[], Float> individuals, int counter) {
			throw new UnsupportedOperationException();
		}

		@Override
		public EvaluationResult evaluate(MatchResult result, float prevFitness, Population population) {
			throw new UnsupportedOperationException();
		}
	}
}
