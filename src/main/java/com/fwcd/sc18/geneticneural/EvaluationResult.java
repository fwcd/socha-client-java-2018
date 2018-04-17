package com.fwcd.sc18.geneticneural;

public class EvaluationResult {
	private final float fitness;
	private final int counterDelta;
	private final boolean skipToNextGeneration;
	
	public EvaluationResult(float fitness) {
		this(fitness, 0);
	}
	
	public EvaluationResult(float fitness, int counterDelta) {
		this(fitness, counterDelta, false);
	}
	
	public EvaluationResult(float fitness, int counterDelta, boolean skipToNextGeneration) {
		this.fitness = fitness;
		this.counterDelta = counterDelta;
		this.skipToNextGeneration = skipToNextGeneration;
	}
	
	public float getFitness() { return fitness; }
	
	public int getCounterDelta() { return counterDelta; }
	
	public boolean shouldSkipToNextGeneration() { return skipToNextGeneration; }
}
