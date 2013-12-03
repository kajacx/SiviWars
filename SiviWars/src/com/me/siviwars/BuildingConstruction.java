package com.me.siviwars;

public class BuildingConstruction extends GameBuilding {

	GameBuilding constructedBuilding;

	float constructionProcess; // from 0 to 1

	float price;

	public BuildingConstruction(GameBuilding constructedBuilding) {
		super(constructedBuilding.gf, constructedBuilding.row,
				constructedBuilding.col, constructedBuilding.owner,
				constructedBuilding.id);
		price = constructedBuilding.price;
		this.constructedBuilding = constructedBuilding;
	}

	private void constructionComplete() {
		gf.addBuilding(constructedBuilding);
		// don't worry about anything else, garbage collector will handle that
	}

	@Override
	protected void routineAction(float time) {
		float collected = withdrawSivi(time);
		constructionProcess += collected / price;
		System.out.format("%.3f, %.3f\n", collected, constructionProcess);
		if (constructionProcess >= 1) {
			constructionComplete();
		}
	}

	@Override
	protected float getPrice() {
		// nobody should need this value, maybe re-do the building hierarchy?
		return -1;
	}

}
