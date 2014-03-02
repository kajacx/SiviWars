package com.me.siviwars.buildings;

import com.me.siviwars.pools.TexturePool;

public class BuildingConstruction extends Building {

	private final ConstructedBuilding constructedBuilding;

	public BuildingConstruction(ConstructedBuilding constructedBuilding) {
		super(constructedBuilding.gf, constructedBuilding.row,
				constructedBuilding.col, constructedBuilding.owner,
				constructedBuilding.id);
		this.constructedBuilding = constructedBuilding;

		texture = TexturePool.getTexturePool().getBuildingTexture(
				constructedBuilding.id, constructedBuilding.owner, true);

		healthCur = 0;// healthMax * InputEventHandler.epsilon;
	}

	private void constructionComplete() {
		gf.addBuilding(constructedBuilding);
		// don't worry about anything else, garbage collector will handle that
	}

	@Override
	public void routineAction(float time) {
		/*float collected = withdrawSivi(time);
		healthCur += collected;//*/// now done is building damageOrHeal method
		// System.out.format("%.3f, %.3f\n", collected, constructionProcess);
		if (healthCur >= healthMax) {
			constructionComplete();
		}
	}

}
