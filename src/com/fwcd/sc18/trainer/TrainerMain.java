package com.fwcd.sc18.trainer;

import java.io.File;

import com.fwcd.sc18.geneticneural.GeneticNeuralLogic;
import com.fwcd.sc18.trainer.core.GameSimulator;

import sc.player2018.RandomLogic;

public class TrainerMain {
	public static void main(String[] args) {
		GameSimulator sim = new GameSimulator(GeneticNeuralLogic::new, RandomLogic::new, Long.MAX_VALUE);
		File stopFile = new File("." + File.separator + "StopTraining");
		stopFile.deleteOnExit();
		sim.setStopCondition(stopFile::exists);
		sim.run();
	}
}
