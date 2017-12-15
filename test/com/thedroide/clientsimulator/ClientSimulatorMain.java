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
				new MinimaxPlayer()
		)
				.setDepth(4)
				.setGameRounds(2000)
				.setSoftMaxTime(2000)
				.setThreadCount(4)
				.start();
	}
}
