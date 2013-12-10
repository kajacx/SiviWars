package com.me.siviwars.buildings;

import com.me.siviwars.pools.TexturePool;

public class BuildingConstruction extends Building {

	ConstructedBuilding constructedBuilding;

	float constructionProcess; // from 0 to 1

	float price;

	public BuildingConstruction(ConstructedBuilding constructedBuilding) {
		super(constructedBuilding.gf, constructedBuilding.row,
				constructedBuilding.col, constructedBuilding.owner,
				constructedBuilding.id);
		price = constructedBuilding.price;
		this.constructedBuilding = constructedBuilding;

		texture = TexturePool.getTexturePool().getBuildingTexture(
				constructedBuilding.id, constructedBuilding.owner, true);
	}

	private void constructionComplete() {
		gf.addBuilding(constructedBuilding);
		// don't worry about anything else, garbage collector will handle that
	}

	@Override
	public void routineAction(float time) {
		float collected = withdrawSivi(time);
		constructionProcess += collected / price;
		// System.out.format("%.3f, %.3f\n", collected, constructionProcess);
		if (constructionProcess >= 1) {
			constructionComplete();
		}
	}

	@Override
	public float getHealthbar() {
		return constructionProcess;
	}

}
