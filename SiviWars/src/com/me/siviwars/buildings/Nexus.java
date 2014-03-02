package com.me.siviwars.buildings;

import com.me.siviwars.GameField;
import com.me.siviwars.Sivi;
import com.me.siviwars.SiviWars;

public class Nexus extends ConstructedBuilding {

	public Nexus(GameField gf, int row, int col, Sivi owner) {
		super(gf, row, col, owner, Building.BUILDING_NEXUS);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void destroy() {
		super.destroy();
		SiviWars.getInstance().setWinner(owner.invert());
	}

	@Override
	public void routineAction(float time) {
		// TODO Auto-generated method stub

	}// */

}
