package com.me.siviwars.buildings;

import com.me.siviwars.GameField;
import com.me.siviwars.Sivi;
import com.me.siviwars.units.Unit;

public class Spawner extends ConstructedBuilding {

	public Spawner(GameField gf, int row, int col, Sivi owner) {
		super(gf, row, col, owner, Building.BUILDING_SPAWNER);
		healthCur = 0; // hacks
	}

	private void spawnUnit() {
		GameField._this.addUnit(new Unit(this));
	}

	@Override
	public void routineAction(float time) {
		if (healthCur >= healthMax) {
			spawnUnit();
			healthCur = 0;
		}
	}

}
