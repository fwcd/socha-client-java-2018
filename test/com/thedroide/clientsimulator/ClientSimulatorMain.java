package com.thedroide.clientsimulator;

import com.thedroide.clientsimulator.core.ClientSimulator;
import com.thedroide.sc18.mcts.MCTSPlayer;
import com.thedroide.sc18.minimax.MinimaxPlayer;

/**
 * A basic client simulator that builds upon the abstractions of the
 * Antelmann-Game-API.
 */
public class ClientSimulatorMain {
	public static void main(String[] args) {
		new ClientSimulator(
				new MCTSPlayer(),
				new MinimaxPlayer(),
				4, // Depth
				2000 // Soft maximum time
		).run(50); // Game rounds
	}
}
