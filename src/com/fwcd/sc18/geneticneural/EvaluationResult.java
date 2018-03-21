package com.fwcd.sc18.geneticneural;

public class EvaluationResult {
	private final float fitness;
	private final int counterDelta;
	
	public EvaluationResult(float fitness) {
		this.fitness = fitness;
		counterDelta = 0;
	}
	
	public EvaluationResult(float fitness, int counterDelta) {
		this.fitness = fitness;
		this.counterDelta = counterDelta;
	}
	
	public float getFitness() { return fitness; }
	
	public int getCounterDelta() { return counterDelta; }
}
