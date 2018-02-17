package com.fwcd.sc18.geneticneural;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Perceptron {
	private final int[] layerSizes;
	private float[] weights;
	
	public Perceptron(int... layerSizes) {
		this.layerSizes = layerSizes;
		weights = generateWeights(layerSizes);
	}
	
	public static float[] generateWeights(int... layerSizes) {
		int weightCount = 0;
		for (int i=1; i<layerSizes.length; i++) {
			weightCount += (layerSizes[i - 1] + 1) * layerSizes[i];
		}
		
		float[] newWeights = new float[weightCount];
		return initWeights(newWeights);
	}

	public static float[] initWeights(float[] newWeights) {
		Random random = ThreadLocalRandom.current();
		for (int i=0; i<newWeights.length; i++) {
			// Gaussian weight initialization
			newWeights[i] = (float) random.nextGaussian();
		}
		
		return newWeights;
	}
	
	public float[] compute(float[] input) {
		if (input.length != layerSizes[0]) {
			throw new RuntimeException("Input vector size does not match input layer size.");
		}
		
		int weightIndex = 0;
		float[] layer = input;
		
		for (int nextLayerI=1; nextLayerI<layerSizes.length; nextLayerI++) {
			float[] nextLayer = new float[layerSizes[nextLayerI]];
			
			for (int nextNeuronI=0; nextNeuronI<nextLayer.length; nextNeuronI++) {
				float dot = 0;
				
				for (float neuron : layer) {
					dot += neuron * weights[weightIndex++];
				}
				
				float bias = weights[weightIndex++];
				nextLayer[nextNeuronI] = relu(dot + bias);
			}
			
			layer = nextLayer;
		}
		
		return layer;
	}
	
	public void setWeights(float[] weights) {
		this.weights = weights;
	}
	
	public float[] getWeights() {
		return weights;
	}
	
	public void saveWeights(OutputStream os) {
		try (DataOutputStream dos = new DataOutputStream(os)) {
			dos.writeInt(weights.length);
			
			for (float weight : weights) {
				dos.writeFloat(weight);
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	public void loadWeights(InputStream is) {
		try (DataInputStream dis = new DataInputStream(is)) {
			float[] newWeights = new float[dis.readInt()];
			
			int i = 0;
			while (dis.available() > 0) {
				newWeights[i++] = dis.readFloat();
			}
			
			weights = newWeights;
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	private float relu(float x) {
		return x >= 0 ? x : 0;
	}
}
