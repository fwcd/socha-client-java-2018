package com.thedroide.sc18.minimax;

import com.thedroide.sc18.algorithmics.MoveRating;

public class IntRating implements MoveRating {
	private static final IntRating EMPTY_RATING = new IntRating(Integer.MIN_VALUE);
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

	@Override
	public int compareTo(MoveRating o) {
		if (o instanceof IntRating) {
			return Integer.compare(value, ((IntRating) o).value);
		} else {
			throw new IllegalArgumentException("Compared MoveRating needs to be an instance of IntRating!");
		}
	}
}
