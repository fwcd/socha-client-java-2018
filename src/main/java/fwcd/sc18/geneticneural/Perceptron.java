package fwcd.sc18.geneticneural;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import fwcd.sc18.utils.HUIUtils;
import fwcd.sc18.utils.IntSet;

/**
 * A feed-forward multi-layer perceptron.
 */
public class Perceptron {
	private final int[] layerSizes;
	private float[] weights;
	
	private IntSet dropoutIndices;
	private float dropoutPercent = 0.1F;
	private boolean dropoutEnabled = false;
	
	/**
	 * Constructs a new Perceptron using the given
	 * layer sizes.
	 */
	public Perceptron(int... layerSizes) {
		this.layerSizes = layerSizes;
		weights = HUIUtils.generateWeights(layerSizes);
	}
	
	/**
	 * Computes the output vector for a given input.
	 */
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
					if (isWeightEnabled(weightIndex)) {
						dot += neuron * weights[weightIndex];
					}
					weightIndex++;
				}
				
				float bias = isWeightEnabled(weightIndex) ? weights[weightIndex] : 0;
				weightIndex++;
				
				nextLayer[nextNeuronI] = relu(dot + bias);
			}
			
			layer = nextLayer;
		}
		
		return layer;
	}
	
	private boolean isWeightEnabled(int weightIndex) {
		return !dropoutEnabled || !dropoutIndices.contains(weightIndex);
	}
	
	public void setDropoutEnabled(boolean dropoutEnabled) {
		this.dropoutEnabled = dropoutEnabled;
		
		if (dropoutEnabled && dropoutIndices == null) {
			Random random = ThreadLocalRandom.current();
			int dropoutNeurons = (int) (dropoutPercent * weights.length);
			int[] indices = new int[dropoutNeurons];
			
			// Randomly sample the neuron indices
			
			for (int i=0; i<dropoutNeurons; i++) {
				indices[i] = i;
			}
			
			for (int i=dropoutNeurons; i<weights.length; i++) {
				int j = random.nextInt(i);
				if (j < dropoutNeurons) {
					indices[j] = i;
				}
			}
			
			dropoutIndices = new IntSet(indices);
		} else if (!dropoutEnabled && dropoutIndices != null) {
			dropoutIndices = null;
		}
	}
	
	public void setWeights(float[] weights) {
		this.weights = weights;
	}
	
	public float[] getWeights() {
		return weights;
	}
	
	/**
	 * Seralizes the weights of this perceptron
	 * to the provided {@link OutputStream}.
	 */
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
	
	/**
	 * Deserializes the weights from a given {@link InputStream}.
	 * Make sure that the data read from the source is valid as
	 * no further checks are performed and subtle bugs could
	 * occur when inconsistent data is read into the network.
	 */
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
	
	/**
	 * Applies the ReLU activation function to
	 * a given input.
	 */
	private float relu(float x) {
		return x >= 0 ? x : 0;
	}
}
