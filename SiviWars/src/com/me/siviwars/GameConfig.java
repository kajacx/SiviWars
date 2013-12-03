package com.me.siviwars;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

public class GameConfig {

	/* not computable from the others */
	int rows, cols;

	float screenHeight, screenWidth;

	/**
	 * inclusive, e.g. 5 means 0 to 5 inclusive
	 */
	int maxTerrainHeight;

	/**
	 * precision blocks for sivi rendering: how many levels on 1 ground layer
	 */
	int scaleCount;

	/**
	 * inclusive, maximum level for sivi, stacked with terrain height, must be
	 * bigger than maxTerrainHeight
	 */
	float maxSiviLevel;

	/**
	 * for example, 4 means that from height 4 (apart from terrain height), the
	 * colors of sivi will be same on all ground levels
	 */
	float maxPaintCapacity;

	/**
	 * basic colors used by colorPool
	 */
	Color redSiviColor, greenSiviColor;

	/*computable from others*/
	/**
	 * used for drawing recompute
	 */
	float rowsCoef, colsCoef;

	/**
	 * used by drawing
	 */
	float renderStep;

	/**
	 * cols and rows must be set properly before calling this
	 */
	void recomputeScreenWidthHeight() {
		screenHeight = Gdx.graphics.getHeight();
		screenWidth = Gdx.graphics.getWidth();

		rowsCoef = screenHeight / rows;
		colsCoef = screenWidth / cols;
	}

	void fillDefault() {
		rows = 20;
		cols = 30;

		recomputeScreenWidthHeight();

		maxTerrainHeight = 4;
		scaleCount = 8;

		recomputeRenderStep();

		maxSiviLevel = 10f;
		maxPaintCapacity = 4f;

		redSiviColor = new Color(1, 0, 0, 1);
		greenSiviColor = new Color(0, .9f, 0, 1);
	}

	void recomputeRenderStep() {
		renderStep = 1f / scaleCount;
	}
}
