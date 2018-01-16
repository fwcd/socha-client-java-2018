package com.thedroide.sc18.test.unittest;

import org.junit.Test;

import com.thedroide.sc18.heuristics.StatsHeuristic;

/**
 * A test where one can freely experiment with code without having to worry
 * about breaking something.
 */
public class Playground {
	@Test
	public void test() {
		StatsHeuristic test = new StatsHeuristic();
		for (int i=0; i<65; i++) {
			print(0, 5, i, test);
		}
	}

	private void print(int salads, int carrots, int fieldIndex, StatsHeuristic heuristic) {
		System.out.println("{salads: " + salads + ", carrots: " + carrots + ", field: " + fieldIndex + "}");
		System.out.println("    = " + heuristic.rate(salads, carrots, fieldIndex));
	}
}
