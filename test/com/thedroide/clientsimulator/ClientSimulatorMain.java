package com.thedroide.clientsimulator;

import com.thedroide.clientsimulator.core.ClientBench;
import com.thedroide.sc18.alphabeta.AlphaBetaPlayer;
import com.thedroide.sc18.choosers.BasicPlayer;
import com.thedroide.sc18.choosers.RandomMoveChooser;
import com.thedroide.sc18.choosers.SimpleMoveChooser;
import com.thedroide.sc18.mcts.MCTSPlayer;

/**
 * A basic client simulator that builds upon the abstractions of the
 * Antelmann-Game-API.
 */
public class ClientSimulatorMain {
	// TODO: The results seem to be inaccurate
	
	public static void main(String[] args) {
		new ClientBench()
				.add(new BasicPlayer(new RandomMoveChooser()))
				.add(new BasicPlayer(new SimpleMoveChooser()))
				.add(new MCTSPlayer())
				.add(new AlphaBetaPlayer())
				.setDepth(4)
				.setGameRounds(800)
				.setSoftMaxTime(2000)
				.setThreadCount(Runtime.getRuntime().availableProcessors())
				.start();
	}
}
