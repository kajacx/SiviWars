package com.me.siviwars.interfaces;

public interface Healthbar extends Ownable {
	/**
	 * ranges from 0 to 1 inclusive
	 * 
	 * @return how much health you have for 0 to 1
	 */
	public float getHealthbar();
}
