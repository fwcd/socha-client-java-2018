package com.thedroide.sc18.test.clientbench.core;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.concurrent.Executors;
import java.util.function.Function;

import javax.swing.JFrame;
import javax.swing.JToolBar;

import com.antelmann.game.Player;
import com.thedroide.sc18.alphabeta.AlphaBetaPlayer;
import com.thedroide.sc18.core.HUIGameState;
import com.thedroide.sc18.core.TreeSearchPlayer;
import com.thedroide.sc18.heuristics.StatsHeuristic;
import com.thedroide.sc18.test.clientbench.utils.ConsolePane;
import com.thedroide.sc18.test.clientbench.utils.SimpleButton;

import io.jenetics.Chromosome;
import io.jenetics.Genotype;
import io.jenetics.IntegerChromosome;
import io.jenetics.IntegerGene;
import io.jenetics.engine.Engine;
import io.jenetics.engine.EvolutionResult;
import io.jenetics.util.Factory;
import sc.plugin2018.GameState;

public class ClientBenchApp {
	private final JFrame view;
	
	private final JToolBar toolBar;
	private final GameView game;
	private final ConsolePane console;
	
	public ClientBenchApp() {
		view = new JFrame("ClientBench");
		view.setSize(800, 600);
		view.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		view.setLayout(new BorderLayout());

		game = new GameView();
		view.add(game.getView(), BorderLayout.CENTER);
		
		toolBar = new JToolBar();
		toolBar.add(new SimpleButton("New Game", () -> game.update(new HUIGameState(new GameState()))));
		toolBar.add(new SimpleButton("Simulate (50x depth 2)", this::simulate));
		toolBar.add(new SimpleButton("Genetic optimization", this::geneticOptimization));
		view.add(toolBar, BorderLayout.NORTH);
		
		console = new ConsolePane();
		System.setOut(console.getOutStream());
		System.setErr(console.getErrStream());
		console.getView().setPreferredSize(new Dimension(100, 100));
		view.add(console.getView(), BorderLayout.SOUTH);
		
		view.setVisible(true);
	}
	
	private void geneticOptimization() {
		Executors.newSingleThreadExecutor().submit(() -> {
			Player referencePlayer = new AlphaBetaPlayer();
			
			Function<Chromosome<IntegerGene>, StatsHeuristic> decoder = c -> new StatsHeuristic(
					c.getGene(0).intValue(),
					c.getGene(1).intValue(),
					c.getGene(2).intValue(),
					c.getGene(3).intValue()
			);
			Factory<Chromosome<IntegerGene>> chromosomeFactory = () -> IntegerChromosome.of(
					IntegerGene.of(0, 512),
					IntegerGene.of(0, 512),
					IntegerGene.of(0, 512),
					IntegerGene.of(0, 512)
			);
			Factory<Genotype<IntegerGene>> genotypeFactory = () -> Genotype.of(chromosomeFactory, 1);
			Function<Genotype<IntegerGene>, Integer> fitness = g -> {
				TreeSearchPlayer testedPlayer = new AlphaBetaPlayer();
				testedPlayer.setHeuristic(decoder.apply(g.getChromosome()));
				Benchmarker b = new Benchmarker()
						.add(referencePlayer)
						.add(testedPlayer)
						.setDepth(0)
						.setMuted(true)
//						.bind(game)
						.setGameRounds(5)
						.setSoftMaxTime(2000);
				b.start();
				b.waitFor();
				return b.getScore(testedPlayer);
			};
			int generations = 2048;
			System.out.println("Evolving " + Integer.toString(generations) + " generations!");
			System.out.println(
					decoder.apply(Engine
							.builder(fitness, genotypeFactory)
							.build()
							.stream()
							.limit(generations)
							.peek(item -> System.out.println("Generation " + Long.toString(item.getGeneration())))
							.collect(EvolutionResult.toBestEvolutionResult())
							.getBestPhenotype()
							.getGenotype()
							.getChromosome()
					)
			);
		});
	}
	
	private void simulate() {
		bench(new AlphaBetaPlayer(), new AlphaBetaPlayer(), 2, 50);
	}
	
	private void bench(Player a, Player b, int depth, int rounds) {
		new Benchmarker()
				.add(a)
				.add(b)
				.setDepth(depth)
				.bind(game)
				.setGameRounds(rounds)
				.setSoftMaxTime(2000)
				.start();
	}
}
