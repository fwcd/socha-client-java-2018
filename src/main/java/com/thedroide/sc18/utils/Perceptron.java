package com.thedroide.sc18.utils;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Perceptron {
	private List<float[][]> weightLayers;
	private List<float[]> biasLayers;
	
	public Perceptron() {
		
	}
	
	public int layerCount() {
		return weightLayers.size();
	}
	
	public int layerSize(int i) {
		return biasLayers.get(i).length;
	}
	
	public void addLayer(int size) {
		int index = layerCount();
		float[][] weights = new float[layerSize(index)][layerSize(index - 1)];
		float[] biases = new float[layerSize(index)];
		ThreadLocalRandom random = ThreadLocalRandom.current();
		
		// Uniform weight initialization
		
		for (int y=0; y<weights.length; y++) {
			for (int x=0; x<weights[0].length; x++) {
				weights[y][x] = random.nextFloat();
			}
		}
		
		for (int i=0; i<biases.length; i++) {
			biases[i] = random.nextFloat();
		}
	}
	
	public float[] matrixMultiply(float[][] left, float[] right) {
		if (right.length != left[0].length) {
			throw new IllegalArgumentException("Matrix multiply: Left width does not match right height.");
		}
		
		float[] result = new float[left.length];
		float dot = 0;
		
		for (int y=0; y<result.length; y++) {
			for (int i=0; i<right.length; i++) {
				dot += left[y][i] * right[i];
			}
			
			result[y] = dot;
		}
		
		return result;
	}
	
	public float[] feedForward(float[] input) {
		throw new RuntimeException("TODO"); // TODO
	}
}
