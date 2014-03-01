package com.me.siviwars.drawing;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.me.siviwars.GameConfig;
import com.me.siviwars.Sivi;
import com.me.siviwars.buildings.Building;
import com.me.siviwars.pools.ColorPool;

public class BuildingHPBars {

	private final GameConfig gc;
	private final float[] muchBar = new float[5];
	private final float[][] weryCoordinates = new float[5][4];
	// private final float[][] rotatedCoordinates = new float[5][4];

	private final float[] tmpRotationBuffer = new float[2];

	private final Rotator rightR = new RotationRight(),
			leftR = new RotationLeft();

	public BuildingHPBars(GameConfig gc) {
		this.gc = gc;
	}

	/**
	 * takes naive parameters, draws acctual line on screen
	 * 
	 * @param fromX
	 * @param fromY
	 * @param toX
	 * @param toY
	 * @param vertical
	 */
	private void drawLines(Building b, ShapeRenderer sh) {
		final float offsetX = gc.menuHeight + b.col * gc.colsCoef, offsetY = b.row
				* gc.rowsCoef;
		sh.setColor(b.owner == Sivi.RED ? ColorPool.getColorPool().baseRed
				: ColorPool.getColorPool().baseGreen);
		for (int i = 0; i < 5; i++) {
			sh.line(weryCoordinates[i][0] + offsetX, weryCoordinates[i][1]
					+ offsetY, weryCoordinates[i][2] + offsetX,
					weryCoordinates[i][3] + offsetY);
		}
	}

	private void rotateAndScaleCoords(final Sivi owner) {
		final float height = gc.rowsCoef, width = gc.colsCoef;
		final Rotator r = owner == Sivi.RED ? rightR : leftR;
		for (int i = 0; i < 5; i++) {
			for (int o = 0; o <= 2; o += 2) {
				// System.out.format("I: %d O: %d\n", i, o);
				r.rotate(weryCoordinates[i][o], weryCoordinates[i][o + 1],
						tmpRotationBuffer);
				weryCoordinates[i][o] = tmpRotationBuffer[0] * width;
				weryCoordinates[i][o + 1] = tmpRotationBuffer[1] * height;
			}
		}
	}

	private interface Rotator {
		public void rotate(float x, float y, float[] result);
	}

	/**
	 * for red sivi
	 * 
	 * @author kajacx
	 * 
	 */
	private class RotationRight implements Rotator {
		// float height = gc.rowsCoef;

		@Override
		public void rotate(float x, float y, float[] result) {
			result[0] = y;
			result[1] = 1 - x;
		}

	}

	/**
	 * for green sivi
	 * 
	 * @author kajacx
	 * 
	 */
	private class RotationLeft implements Rotator {
		// float height = gc.rowsCoef;

		@Override
		public void rotate(float x, float y, float[] result) {
			result[0] = 1 - y;
			result[1] = x;
		}

	}

	/**
	 * basic draw, draws on a 1x1 rectangle left bot corner 0,0
	 * 
	 */
	private void muchBarToWeryCoordinates() {
		weryCoordinates[0][0] = 0.5f;
		weryCoordinates[0][1] = 1;
		weryCoordinates[0][2] = 0.5f + muchBar[0];
		weryCoordinates[0][3] = 1;

		weryCoordinates[1][0] = 1;
		weryCoordinates[1][1] = 1;
		weryCoordinates[1][2] = 1;
		weryCoordinates[1][3] = 1 - muchBar[1];

		weryCoordinates[2][0] = 1;
		weryCoordinates[2][1] = 0;
		weryCoordinates[2][2] = 1 - muchBar[2];
		weryCoordinates[2][3] = 0;

		weryCoordinates[3][0] = 0;
		weryCoordinates[3][1] = 0;
		weryCoordinates[3][2] = 0;
		weryCoordinates[3][3] = muchBar[3];

		weryCoordinates[4][0] = 0;
		weryCoordinates[4][1] = 1;
		weryCoordinates[4][2] = muchBar[4];
		weryCoordinates[4][3] = 1;
		/*drawLine(1, 1, 1, 1 - muchBar[1], true);
		drawLine(1, 0, 1 - muchBar[2], 0, false);
		drawLine(0, 0, 0, muchBar[3], true);
		drawLine(0, 1, muchBar[4], 1, false);*/
	}

	private void fillMuchBar(float hp) {
		// top, right, bot, left, top
		muchBar[0] = Math.min(.5f, hp * 4);
		muchBar[4] = Math.max(0, .5f * (8 * hp - 7)); // op lagrange
		for (int i = 1; i <= 3; i++) {
			muchBar[i] = 4 * hp + 0.5f - i; // linear
			muchBar[i] = Math.max(muchBar[i], 0);
			muchBar[i] = Math.min(1, muchBar[i]);
		}
	}

	public void drawHPBar(Building bu, ShapeRenderer sh) {
		fillMuchBar(bu.getHealthbar());
		muchBarToWeryCoordinates();
		rotateAndScaleCoords(bu.owner);
		drawLines(bu, sh);
		// System.out.println(Arrays.deepToString(new Object[] { muchBar }));
	}

}
