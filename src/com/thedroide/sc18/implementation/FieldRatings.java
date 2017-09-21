package com.thedroide.sc18.implementation;

import com.thedroide.sc18.utils.IndexedHashMap;
import com.thedroide.sc18.utils.IndexedMap;

import sc.plugin2018.Board;
import sc.plugin2018.Field;

public class FieldRatings {
	private static final int BOARD_SIZE = 65;
	private final IndexedMap<Field, Rating> ratings = new IndexedHashMap<>();
	
	public FieldRatings(Board board) {
		for (int i=0; i<BOARD_SIZE; i++) {
			Field field = new Field(board.getTypeAt(i));
			field.setIndex(i);
			ratings.put(field, new Rating());
		}
	}
	
	public void setRating(int fieldIndex, Rating rating) {
		ratings.setValue(fieldIndex, rating);
	}
	
	public Rating getRating(int fieldIndex) {
		return ratings.getValue(fieldIndex);
	}
}
