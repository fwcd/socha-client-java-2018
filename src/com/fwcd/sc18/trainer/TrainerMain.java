package com.fwcd.sc18.trainer;

import com.fwcd.sc18.geneticneural.GeneticNeuralLogic;
import com.fwcd.sc18.trainer.core.GameSimulator;

import sc.player2018.RandomLogic;

public class TrainerMain {
	public static void main(String[] args) {
		GameSimulator sim = new GameSimulator(GeneticNeuralLogic::new, RandomLogic::new, Long.MAX_VALUE);
		sim.start();
	}
}
