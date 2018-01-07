package com.thedroide.sc18.clientsimulator;

import com.thedroide.sc18.alphabeta.AlphaBetaPlayer;
import com.thedroide.sc18.alphabeta.IterativeDeepeningABPlayer;
import com.thedroide.sc18.choosers.BasicPlayer;
import com.thedroide.sc18.choosers.SimpleMoveChooser;
import com.thedroide.sc18.clientsimulator.core.ClientBench;
import com.thedroide.sc18.mcts.MCTSPlayer;
import com.thedroide.sc18.utils.WIP;

/**
 * A basic client simulator that builds upon the abstractions of the
 * Antelmann-Game-API.
 */
@WIP(usable = false)
public class ClientSimulatorMain {
	// FIXME: The implementation seems to be very buggy and does not produce correct results
	// Use testclient instead!
	
	public static void main(String[] args) {
		new ClientBench()
				.add(new BasicPlayer(new SimpleMoveChooser()))
				.add(new MCTSPlayer())
				.add(new AlphaBetaPlayer())
				.add(new IterativeDeepeningABPlayer())
				.setDepth(4)
				.setGameRounds(800)
				.setSoftMaxTime(2000)
				.setThreadCount(Runtime.getRuntime().availableProcessors())
				.start();
	}
}
