package com.thedroide.sc18.test.unittest;

import java.util.Arrays;

import org.junit.Test;

import com.thedroide.sc18.utils.Perceptron;

public class PerceptronTest {
	@Test
	public void test() {
		Perceptron p = new Perceptron();
		float[] vec = {3, 2};
		float[][] mat = {
				{4, 3},
				{2, 1}
		};
		System.out.println(Arrays.toString(p.matrixMultiply(mat, vec)));
	}
}
