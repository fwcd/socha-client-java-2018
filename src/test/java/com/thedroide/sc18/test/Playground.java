package com.thedroide.sc18.test;

import burlap.domain.singleagent.gridworld.GridWorldDomain;
import burlap.domain.singleagent.gridworld.GridWorldVisualizer;
import burlap.domain.singleagent.gridworld.state.GridAgent;
import burlap.domain.singleagent.gridworld.state.GridLocation;
import burlap.domain.singleagent.gridworld.state.GridWorldState;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.shell.visual.VisualExplorer;
import burlap.visualizer.Visualizer;

/**
 * A class where one can freely experiment with code without
 * having to worry about breaking something.
 */
public class Playground {
	public static void main(String[] args) {
		GridWorldDomain gw = new GridWorldDomain(11, 11);
		gw.setMapToFourRooms();
		gw.setProbSucceedTransitionDynamics(0.8D);
		SADomain domain = gw.generateDomain();
		
		State s = new GridWorldState(new GridAgent(0, 0), new GridLocation(10, 10, "loc0"));
		Visualizer v = GridWorldVisualizer.getVisualizer(gw.getMap());
		VisualExplorer exp = new VisualExplorer(domain, v, s);
		
		exp.addKeyAction("w", GridWorldDomain.ACTION_NORTH, "");
		exp.addKeyAction("s", GridWorldDomain.ACTION_SOUTH, "");
		exp.addKeyAction("a", GridWorldDomain.ACTION_WEST, "");
		exp.addKeyAction("d", GridWorldDomain.ACTION_EAST, "");
		
		exp.initGUI();
	}
}
