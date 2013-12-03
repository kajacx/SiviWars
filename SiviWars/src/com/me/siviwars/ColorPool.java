package com.me.siviwars;

import com.badlogic.gdx.graphics.Color;

public class ColorPool {

	private GameConfig gc;

	Color[] groundColors;

	Color[][] redSiviColors;
	Color[][] greenSiviColors;

	ColorPool(GameConfig gc) {
		setGameConfig(gc);
	}

	/**
	 * Game config must be set before calling this
	 */
	private void recomputeGroundColors() {
		groundColors = new Color[gc.maxTerrainHeight + 1];
		for (int i = 0; i <= gc.maxTerrainHeight; i++) {
			// lower index = darker color = lower c
			float c = .5f + (i / (2f * gc.maxTerrainHeight));
			groundColors[i] = new Color(c, c, c, 1);
			// System.out.println(groundColors[i]);
		}
	}

	private Color[] computeColorsByTerrainHeight(int terrainHeight,
			Color baseCol) {
		Color[] ret = new Color[(int) ((gc.maxSiviLevel - terrainHeight) * gc.scaleCount)];

		float maxDif = gc.maxPaintCapacity * gc.scaleCount;
		// float bottomBound = terrainHeight;

		// minimal percentage of new color
		float minPer = .25f;

		for (int i = 0; i < (gc.maxSiviLevel - terrainHeight) * gc.scaleCount; i++) {
			// now note that 0 means 1/scalecount
			float ratioNew = (i + 1f) / maxDif;

			// lets add those min percentage
			ratioNew += minPer;

			// now nerf a bit to match 1:1
			ratioNew *= 1f / (1f + minPer);

			// upper bound check after surpassing max paint capacity
			if (ratioNew > 1f) {
				ratioNew = 1f;
			}

			// System.out.format("%d: %1.2f\n", i, ratioNew);

			// now create that new color
			float r = ratioNew * baseCol.r + (1 - ratioNew)
					* groundColors[terrainHeight].r;
			float g = ratioNew * baseCol.g + (1 - ratioNew)
					* groundColors[terrainHeight].g;
			float b = ratioNew * baseCol.b + (1 - ratioNew)
					* groundColors[terrainHeight].b;

			// and add it to ret array
			ret[i] = new Color(r, g, b, 1);
		}

		return ret;
	}

	private void recomputeRedSiviColors() {
		redSiviColors = new Color[gc.maxTerrainHeight + 1][];
		for (int i = 0; i <= gc.maxTerrainHeight; i++) {
			redSiviColors[i] = computeColorsByTerrainHeight(i, gc.redSiviColor);
		}
	}

	private void recomputeGreenSiviColors() {
		greenSiviColors = new Color[gc.maxTerrainHeight + 1][];
		for (int i = 0; i <= gc.maxTerrainHeight; i++) {
			greenSiviColors[i] = computeColorsByTerrainHeight(i,
					gc.greenSiviColor);
		}
	}

	void setGameConfig(GameConfig gc) {
		this.gc = gc;
		recomputeGroundColors();
		recomputeRedSiviColors();
		recomputeGreenSiviColors();
	}

}
