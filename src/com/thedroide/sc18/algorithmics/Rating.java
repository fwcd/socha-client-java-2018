package com.thedroide.sc18.algorithmics;

public interface Rating extends Comparable<Rating> {
	public Rating add(Rating other);
	
	public Rating invert();
	
	public default Rating min(Rating other) {
		return (compareTo(other) <= 0) ? this : other;
	}
	
	public default Rating max(Rating other) {
		return (compareTo(other) >= 0) ? this : other;
	}
}
