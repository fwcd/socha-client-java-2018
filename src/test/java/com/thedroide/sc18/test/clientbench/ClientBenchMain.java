package com.thedroide.sc18.test.clientbench;

import com.thedroide.sc18.test.clientbench.core.ClientBenchApp;
import com.thedroide.sc18.utils.WIP;

/**
 * A basic client benchmarker that builds upon the abstractions of the
 * Antelmann-Game-API.
 */
@WIP(usable = false)
public class ClientBenchMain {
	// FIXME: The implementation seems to be very buggy and does not produce correct results
	
	public static void main(String[] args) {
//		new ClientBenchEngine()
//				.add(new BasicPlayer(new SimpleMoveChooser()))
//				.add(new MCTSPlayer())
//				.add(new AlphaBetaPlayer())
//				.add(new IterativeDeepeningABPlayer())
//				.setDepth(4)
//				.setGameRounds(800)
//				.setSoftMaxTime(2000)
//				.setThreadCount(Runtime.getRuntime().availableProcessors())
//				.start();
		
		new ClientBenchApp();
	}
}
