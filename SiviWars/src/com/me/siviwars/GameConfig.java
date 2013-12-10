package com.me.siviwars;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

public class GameConfig {

	/* not computable from the others */
	public int rows, cols;

	public float screenHeight, screenWidth, menuHeight, fieldHeight,
			fieldWidth;

	/**
	 * inclusive, e.g. 5 means 0 to 5 inclusive
	 */
	public int maxTerrainHeight;

	/**
	 * precision blocks for sivi rendering: how many levels on 1 ground layer
	 */
	public int scaleCount;

	/**
	 * inclusive, maximum level for sivi, stacked with terrain height, must be
	 * bigger than maxTerrainHeight
	 */
	public float maxSiviLevel;

	/**
	 * for example, 4 means that from height 4 (apart from terrain height), the
	 * colors of sivi will be same on all ground levels
	 */
	public float maxPaintCapacity;

	/**
	 * basic colors used by colorPool
	 */
	public Color redSiviColor, greenSiviColor;

	/*computable from others*/
	/**
	 * used for drawing recompute
	 */
	public float rowsCoef, colsCoef;

	/**
	 * used by drawing
	 */
	public float renderStep;

	/**
	 * cols and rows must be set properly before calling this
	 */
	void recomputeScreenWidthHeight() {
		screenHeight = Gdx.graphics.getHeight();
		screenWidth = Gdx.graphics.getWidth();

		menuHeight = screenWidth / 10;

		fieldHeight = screenHeight;
		fieldWidth = screenWidth - 2 * menuHeight;

		rowsCoef = fieldHeight / rows;
		colsCoef = fieldWidth / cols;
	}

	void fillDefault() {
		rows = 20;
		cols = 25;

		recomputeScreenWidthHeight();

		maxTerrainHeight = 4;
		scaleCount = 8;

		recomputeRenderStep();

		maxSiviLevel = 10f;
		maxPaintCapacity = 4f;

		redSiviColor = new Color(1, 0, 0, 1);
		// greenSiviColor = new Color(0, .9f, 0, 1);
		greenSiviColor = new Color(0, 0, 1, 1);
	}

	void recomputeRenderStep() {
		renderStep = 1f / scaleCount;
	}
}
