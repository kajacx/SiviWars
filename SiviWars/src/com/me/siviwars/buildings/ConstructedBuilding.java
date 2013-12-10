package com.me.siviwars.buildings;

import com.me.siviwars.GameField;
import com.me.siviwars.Sivi;
import com.me.siviwars.pools.TexturePool;

public abstract class ConstructedBuilding extends Building {

	public float price; // how much to build this

	public float healtCur; // how much cur health
	public float healtMax; // how much max health

	public ConstructedBuilding(GameField gf, int row, int col, Sivi owner,
			int id) {
		super(gf, row, col, owner, id);

		texture = TexturePool.getTexturePool().getBuildingTexture(id, owner,
				false);

		price = getPrice();

		healtMax = healtCur = price;
	}

	public abstract float getPrice();

	@Override
	public float getHealthbar() {
		return healtCur / healtMax;
	}
}
