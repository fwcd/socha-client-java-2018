package com.thedroide.clientsimulator;

import com.thedroide.clientsimulator.core.ClientSimulator;
import com.thedroide.sc18.choosers.BasicPlayer;
import com.thedroide.sc18.choosers.RandomMoveChooser;
import com.thedroide.sc18.minimax.MinimaxPlayer;

/**
 * A basic client simulator that builds upon the abstractions of the
 * Antelmann-Game-API.
 */
public class ClientSimulatorMain {
	// FIXME TODO: This is in ALPHA and does not work properly yet. It outputs incorrect scores...
	
	public static void main(String[] args) {
		new ClientSimulator(
				new BasicPlayer(new RandomMoveChooser()),
				new MinimaxPlayer(),
				2, // Depth
				2000 // Soft maximum time
		).run(50); // Game rounds
	}
}
