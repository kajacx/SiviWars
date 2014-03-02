package com.me.siviwars;

public enum Sivi {
	RED(0), GREEN(1), NEUTRAL(-1);
	public final int ordinal;

	private Sivi(int o) {
		ordinal = o;
	}

	public Sivi invert() {
		switch (this) {
		case RED:
			return GREEN;
		case GREEN:
			return RED;
		default:
			return NEUTRAL;
		}
	}
}
