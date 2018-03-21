package com.fwcd.sc18.geneticneural;

import com.fwcd.sc18.utils.IndexedMap;
import com.fwcd.sc18.utils.MatchResult;

public interface GeneticStrategy {
	float[][] selectTrainingGenes(IndexedMap<float[], Float> individuals, int counter);
	
	EvaluationResult evaluate(MatchResult result, float prevFitness, Population population);
	
	// Empty implementation
	
	public static final class None implements GeneticStrategy {
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
