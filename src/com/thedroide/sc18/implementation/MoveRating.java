package com.thedroide.sc18.implementation;

import sc.plugin2018.Move;

public class MoveRating implements Comparable<MoveRating> {
	private static final MoveRating EMPTY_RATING = new MoveRating();
	
	/**
	 * Constructs an empty rating.
	 */
	private MoveRating() {
		// TODO
	}
	
	public static MoveRating getEmpty() {
		return EMPTY_RATING;
	}
	
	/**
	 * Evaluates this move.
	 * 
	 * @param move - The move
	 */
	public static MoveRating evaluate(Move move) {
		// TODO
		return new MoveRating();
	}

	@Override
	public int compareTo(MoveRating o) {
		// TODO Auto-generated method stub
		return 0;
	}
}
