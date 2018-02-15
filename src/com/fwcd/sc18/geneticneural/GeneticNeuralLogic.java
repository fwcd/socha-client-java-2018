package com.fwcd.sc18.geneticneural;

import com.fwcd.sc18.TemplateLogic;

import sc.plugin2018.AbstractClient;
import sc.plugin2018.GameState;
import sc.plugin2018.Move;
import sc.plugin2018.Player;

public class GeneticNeuralLogic extends TemplateLogic {
	private final int[] layerSizes = {10, 10, 1};
	private final Population population = new Population(10, () -> Perceptron.generateWeights(layerSizes));
	private final Perceptron neuralNet;
	
	public GeneticNeuralLogic(AbstractClient client) {
		super(client);
	}

	@Override
	protected Move selectMove(GameState gameBeforeMove, Player me) {
		
	}
}
