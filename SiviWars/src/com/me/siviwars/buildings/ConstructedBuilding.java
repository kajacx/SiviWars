package com.me.siviwars.buildings;

import com.me.siviwars.GameField;
import com.me.siviwars.Sivi;
import com.me.siviwars.pools.TexturePool;

public abstract class ConstructedBuilding extends Building {

	public ConstructedBuilding(GameField gf, int row, int col, Sivi owner,
			int id) {
		super(gf, row, col, owner, id);

		texture = TexturePool.getTexturePool().getBuildingTexture(id, owner,
				false);

		healthCur = healthMax;
	}

	// public abstract float getPrice();

	/*@Override
	public float getHealthbar() {
		return healtCur / healtMax;
	}//*/
}
