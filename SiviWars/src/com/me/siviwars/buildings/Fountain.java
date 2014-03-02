package com.me.siviwars.buildings;

import com.me.siviwars.GameField;
import com.me.siviwars.Sivi;

public class Fountain extends ConstructedBuilding {

	public float spawn;

	public Fountain(GameField gf, int row, int col, Sivi owner, float spawn) {
		super(gf, row, col, owner, Building.BUILDING_FOUNTAIN);
		this.spawn = spawn;
	}

	@Override
	public void routineAction(float time) {
		// System.out.println(time);
		gf.addSiviSoft(row, col, spawn * time, owner);
	}

	/*@Override
	public float getPrice() {
		return 10;
	}//*/
}
