package com.thedroide.sc18.minimax;

import com.thedroide.sc18.algorithmics.Rating;

public class IntRating implements Rating {
	private static final IntRating EMPTY_RATING = new IntRating(Integer.MIN_VALUE);
	private static final IntRating MAX_RATING = new IntRating(Integer.MAX_VALUE);
	
	private final int value;
	
	/**
	 * Constructs an empty rating.
	 */
	public IntRating(int value) {
		this.value = value;
	}
	
	public static IntRating getEmpty() {
		return EMPTY_RATING;
	}
	
	public static IntRating getMax() {
		return MAX_RATING;
	}

	@Override
	public int compareTo(Rating o) {
		if (o instanceof IntRating) {
			return Integer.compare(value, ((IntRating) o).value);
		} else {
			throw new IllegalArgumentException("Compared MoveRating needs to be an instance of IntRating!");
		}
	}
	
	@Override
	public String toString() {
		return Integer.toString(value);
	}

	@Override
	public Rating add(Rating other) {
		if (other instanceof IntRating) {
			return new IntRating(value + ((IntRating) other).value);
		} else {
			throw new IllegalArgumentException("Compared MoveRating needs to be an instance of IntRating");
		}
	}
}
