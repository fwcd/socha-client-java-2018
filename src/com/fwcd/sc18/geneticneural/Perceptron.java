package com.fwcd.sc18.geneticneural;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;

import com.fwcd.sc18.utils.HUIUtils;

public class Perceptron {
	private final int[] layerSizes;
	private float[] weights;
	
	public Perceptron(int... layerSizes) {
		this.layerSizes = layerSizes;
		weights = HUIUtils.generateWeights(layerSizes);
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
				
				float extra = weights[weightIndex++];
				nextLayer[nextNeuronI] = relu(dot) * extra;
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
