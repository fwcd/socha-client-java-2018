package com.fwcd.sc18.test;

import java.util.Arrays;
import java.util.List;

import sc.plugin2018.CardType;

public class Playground {
	public static void main(String[] args) {
		System.out.println(Float.toString(encodeCards(Arrays.asList(CardType.TAKE_OR_DROP_CARROTS, CardType.HURRY_AHEAD))));
	}
	
	private static float encodeCards(List<CardType> cards) {
		int raw = 0;
		raw |= (cards.contains(CardType.EAT_SALAD) ? 1 : 0);
		raw |= (cards.contains(CardType.FALL_BACK) ? 1 : 0) << 1;
		raw |= (cards.contains(CardType.HURRY_AHEAD) ? 1 : 0) << 2;
		raw |= (cards.contains(CardType.TAKE_OR_DROP_CARROTS) ? 1 : 0) << 3;
		return normalize(raw, 0, 0b1111);
	}
	
	private static float normalize(float x, float min, float max) {
		return (x - min) / (max - min);
	}
}
