package com.fwcd.sc18.trainer;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.fwcd.sc18.geneticneural.GeneticNeuralLogic;
import com.fwcd.sc18.trainer.core.GameSimulator;

import sc.player2018.SimpleLogic;

public class TrainerMain {
	public static void main(String[] args) {
		GameSimulator simulator = new GameSimulator(
				GeneticNeuralLogic::new,
				SimpleLogic::new,
				Long.MAX_VALUE
		);
		Path stopFile = Paths.get(".", "StopTraining");
		Thread simThread = new Thread(simulator::run);
		
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			try {
				System.out.println("Waiting for shutdown...");
				long start = System.currentTimeMillis();
				
				simulator.stop();
				Files.deleteIfExists(stopFile);
				simThread.join();
				
				long delta = System.currentTimeMillis() - start;
				System.out.println("Finished shutdown in " + delta + " ms");
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}));
		
		simulator.setStopCondition(() -> Files.exists(stopFile));
		simThread.start();
	}
}
