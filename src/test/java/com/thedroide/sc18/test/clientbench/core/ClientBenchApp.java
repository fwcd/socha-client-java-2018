package com.thedroide.sc18.test.clientbench.core;

import java.awt.BorderLayout;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;

import com.antelmann.game.Player;
import com.thedroide.sc18.alphabeta.AlphaBetaPlayer;
import com.thedroide.sc18.core.HUIGameState;
import com.thedroide.sc18.core.TreeSearchPlayer;
import com.thedroide.sc18.heuristics.HUIHeuristic;
import com.thedroide.sc18.heuristics.StatsHeuristic;
import com.thedroide.sc18.test.clientbench.utils.ConfigDialog;
import com.thedroide.sc18.test.clientbench.utils.ConfigPanel;
import com.thedroide.sc18.test.clientbench.utils.ConsolePane;
import com.thedroide.sc18.test.clientbench.utils.SimpleButton;
import com.thedroide.sc18.utils.ClosingExecutor;

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
		view.setSize(900, 700);
		view.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		view.setLayout(new BorderLayout());
		
		JSplitPane centerPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		centerPane.setDividerLocation(500);
		
		game = new GameView();
		centerPane.add(game.getView());
		
		console = new ConsolePane();
		System.setOut(console.getOutStream());
		System.setErr(console.getErrStream());
		centerPane.add(console.getView());
		
		view.add(centerPane, BorderLayout.CENTER);
		
		toolBar = new JToolBar();
		toolBar.add(new SimpleButton("New Game", () -> game.update(new HUIGameState(new GameState()))));
		toolBar.add(new SimpleButton("Simulate", this::simulate));
		toolBar.add(new SimpleButton("Genetic optimization", this::geneticOptimization));
		view.add(toolBar, BorderLayout.NORTH);
		
		view.setVisible(true);
	}
	
	private void geneticOptimization() {
		ConfigPanel config = new ConfigPanel(true, false);
		Supplier<Integer> depthPerRound = config.addIntOption("Depth per round", 0, 0, 256);
		Supplier<Integer> roundsPerIndividual = config.addIntOption("Rounds per individual", 5, 1, 512);
		Supplier<Integer> generations = config.addIntOption("Generations", 256, 1, 100000);
		
		ConfigDialog dialog = new ConfigDialog("Configurate genetic heuristic optimizer...", config, view);
		dialog.show();
		
		if (dialog.wasAccepted()) {
			try (ClosingExecutor executor = new ClosingExecutor(1)) {
				executor.submit(() -> {
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
								.setDepth(depthPerRound.get())
								.setMuted(true)
//								.bind(game)
								.setGameRounds(roundsPerIndividual.get())
								.setSoftMaxTime(2000);
						b.start();
						b.waitFor();
						return b.getScore(testedPlayer);
					};
					System.out.println("Evolving " + generations.get().toString() + " generations!");
					System.out.println(
							decoder.apply(Engine
									.builder(fitness, genotypeFactory)
									.build()
									.stream()
									.limit(generations.get())
									.peek(item -> System.out.println("Generation " + Long.toString(item.getGeneration())))
									.collect(EvolutionResult.toBestEvolutionResult())
									.getBestPhenotype()
									.getGenotype()
									.getChromosome()
							)
					);
				});
			}
		}
	}
	
	private void simulate() {
		ConfigPanel config = new ConfigPanel(true, false);
		Supplier<String> class1 = config.addStringOption("Class 1", "com.thedroide.sc18.alphabeta.AlphaBetaPlayer");
		Supplier<String> class2 = config.addStringOption("Class 2", "com.thedroide.sc18.alphabeta.AlphaBetaPlayer");
		config.nextSection();
		Supplier<Integer> depth = config.addIntOption("Depth", 0, 0, 100);
		Supplier<Integer> rounds = config.addIntOption("Rounds", 200, 1, 1000);
		
		config.nextSuperSection();
		
		ConfigPanel heuristic1 = config.addSubPanel("Heuristic 1 (optional)");
		Supplier<Integer> h1CarrotWeight = heuristic1.addIntOption("Carrot weight", 70, 0, 16384);
		Supplier<Integer> h1SaladWeight = heuristic1.addIntOption("Salad weight", 16384, 0, 16384);
		Supplier<Integer> h1FieldIndexWeight = heuristic1.addIntOption("Field index weight", 404, 0, 16384);
		Supplier<Integer> h1TurnWeight = heuristic1.addIntOption("Turn weight", 404, 0, 16384);
		config.nextSection();
		
		ConfigPanel heuristic2 = config.addSubPanel("Heuristic 2 (optional)");
		Supplier<Integer> h2CarrotWeight = heuristic2.addIntOption("Carrot weight", 70, 0, 16384);
		Supplier<Integer> h2SaladWeight = heuristic2.addIntOption("Salad weight", 16384, 0, 16384);
		Supplier<Integer> h2FieldIndexWeight = heuristic2.addIntOption("Field index weight", 404, 0, 16384);
		Supplier<Integer> h2TurnWeight = heuristic2.addIntOption("Turn weight", 404, 0, 16384);
		
		ConfigDialog dialog = new ConfigDialog("Configurate simulation...", config, view);
		dialog.show();
		
		if (dialog.wasAccepted()) {
			Player p1 = getPlayer(
					class1.get(),
					new StatsHeuristic(h1CarrotWeight.get(), h1SaladWeight.get(), h1FieldIndexWeight.get(), h1TurnWeight.get())
			);
			Player p2 = getPlayer(
					class2.get(),
					new StatsHeuristic(h2CarrotWeight.get(), h2SaladWeight.get(), h2FieldIndexWeight.get(), h2TurnWeight.get())
			);
			
			if (p1 != null && p2 != null) {
				bench(p1, p2, depth.get(), rounds.get());
			} else {
				JOptionPane.showMessageDialog(view, "Invalid class names.", "Warning", JOptionPane.WARNING_MESSAGE);
			}
		}
	}
	
	private Player getPlayer(String playerClass, HUIHeuristic heuristic) {
		try {
			Player player = (Player) Class.forName(playerClass).newInstance();
			
			if (player instanceof TreeSearchPlayer) {
				((TreeSearchPlayer) player).setHeuristic(heuristic);
			}
			
			return player;
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			return null;
		}
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
