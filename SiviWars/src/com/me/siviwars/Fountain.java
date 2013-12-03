package com.me.siviwars;

public class Fountain extends GameBuilding {

	float spawn;

	Fountain(GameField gf, int row, int col, Sivi owner, float spawn) {
		super(gf, row, col, owner, GameBuilding.BUILDING_FOUNTAIN);
		this.spawn = spawn;
	}

	@Override
	protected void routineAction(float time) {
		gf.addSiviSoft(row, col, spawn * time, owner);
	}

	@Override
	protected float getPrice() {
		return 10;
	}
}
