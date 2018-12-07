package fwcd.sc18.agbinds;

import sc.shared.PlayerColor;

public enum AGPlayerColor {
	RED, BLUE;
	
	public static AGPlayerColor of(int role) {
		return values()[role];
	}
	
	public static AGPlayerColor of(PlayerColor color) {
		return color == PlayerColor.RED ? RED : BLUE;
	}
	
	public PlayerColor asPlayerColor() {
		return this == RED ? PlayerColor.RED : PlayerColor.BLUE;
	}
	
	public int asRole() {
		return ordinal();
	}
}
