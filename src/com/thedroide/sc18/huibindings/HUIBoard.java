package com.thedroide.sc18.huibindings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thedroide.sc18.algorithmics.ABoard;
import com.thedroide.sc18.algorithmics.AField;

import sc.plugin2018.Board;

public class HUIBoard implements ABoard {
	private static final Map<Board, HUIBoard> CACHE = new HashMap<>();
	
	private Board board;
	private List<AField> fields = new ArrayList<>();
	
	private HUIBoard(Board board) {
		this.board = board;
		
		for (int i=0; i<65; i++) {
			fields.add(new HUIField(board.getTypeAt(i), i));
		}
	}

	public static ABoard of(Board board) {
		if (!CACHE.containsKey(board)) {
			CACHE.put(board, new HUIBoard(board));
		}
		
		return CACHE.get(board);
	}

	@Override
	public List<AField> getFields() {
		return fields;
	}
	
	public Board getSCBoard() {
		return board;
	}
	
	@Override
	public String toString() {
		return board.toString();
	}
}
