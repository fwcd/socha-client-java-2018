package com.thedroide.sc18.huibindings;

import com.thedroide.sc18.algorithmics.AField;

import sc.plugin2018.FieldType;

public class HUIField implements AField {
	private FieldType type;
	private int index;
	
	public HUIField(FieldType type, int index) {
		this.type = type;
		this.index = index;
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public FieldType getType() {
		return type;
	}
	
	@Override
	public String toString() {
		return type.toString() + " Field at " + Integer.toString(index);
	}
}
