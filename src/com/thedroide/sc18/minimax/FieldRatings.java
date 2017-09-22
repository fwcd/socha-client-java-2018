package com.thedroide.sc18.minimax;

import com.thedroide.sc18.utils.IndexedHashMap;
import com.thedroide.sc18.utils.IndexedMap;

import sc.plugin2018.Board;
import sc.plugin2018.Field;

// TODO: Use this class somehow

public class FieldRatings {
	private static final int BOARD_SIZE = 65;
	private final IndexedMap<Field, IntRating> ratings = new IndexedHashMap<>();
	
	public FieldRatings(Board board) {
		for (int i=0; i<BOARD_SIZE; i++) {
			Field field = new Field(board.getTypeAt(i));
			field.setIndex(i);
			ratings.put(field, IntRating.getEmpty());
		}
	}
	
	public void setRating(int fieldIndex, IntRating rating) {
		ratings.setValue(fieldIndex, rating);
	}
	
	public IntRating getRating(int fieldIndex) {
		return ratings.getValue(fieldIndex);
	}
}
