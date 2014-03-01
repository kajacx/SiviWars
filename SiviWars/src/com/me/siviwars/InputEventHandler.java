package com.me.siviwars;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.me.siviwars.buildings.Building;
import com.me.siviwars.buildings.BuildingConstruction;
import com.me.siviwars.buildings.ConstructedBuilding;

public class InputEventHandler implements InputProcessor {

	public static final boolean debug = true;

	private static final float epsilon = .001f; // hack for rounding down

	public static final int RED_SIVI = 0, GREEN_SIVI = 1, ROW = 0, COL = 1,
			ROW_PAINT = 2, COL_PAINT = 3, X = 0, Y = 1; // various array
														// indexing

	public float[][] cursors = new float[2][4];

	private final float[][] movepadVectors = new float[2][2];

	public float cursorSize = 32;
	public float cursorSizeH = cursorSize / 2;
	public float movepadSpeed = 150; // per second

	public final GameConfig gc;

	public final GameField gf;

	public void routineAction(float sec) {
		/*System.out.format("red: x: %.2f y: %.2f\n",
				movepadVectors[RED_SIVI][X], movepadVectors[RED_SIVI][Y]);*/
		for (int owner = 0; owner < 2; owner++) {
			float row = cursors[owner][ROW] + movepadSpeed * sec
					* movepadVectors[owner][Y];
			float col = cursors[owner][COL] + movepadSpeed * sec
					* movepadVectors[owner][X];
			setCursorHotspot(owner, row, col);
		}
	}

	InputEventHandler(GameConfig gc, GameField gf) {
		this.gc = gc;
		this.gf = gf;
		setCursorHotspot(RED_SIVI, 50, 50);
		setCursorHotspot(GREEN_SIVI, 50, 50);
	}

	public void buildBuilding(int buildingID, Sivi owner, int row, int col) {
		if (gf.getField(owner)[row][col] <= 0) { // cannot build here
			return;
		}
		if (gf.getBuilding(row, col) != null) { // cannot build there
			return;
		}
		ConstructedBuilding cb = (ConstructedBuilding) Building.createBuilding(
				buildingID, gf, row, col, owner);
		BuildingConstruction bc = new BuildingConstruction(cb);
		gf.addBuilding(bc);
	}

	/**
	 * measured from top, range checked
	 * 
	 * @param cursor
	 * @param row
	 * @param col
	 */
	void setCursorHotspot(int cursorOwner, float row, float col) {
		if (row < 0) {
			row = 0;
		} else if (row >= gc.fieldHeight) {
			row = gc.fieldHeight - epsilon;
		}
		if (col < gc.menuHeight) {
			col = gc.menuHeight;
		} else if (col >= gc.menuHeight + gc.fieldWidth) {
			col = gc.menuHeight + gc.fieldWidth - epsilon;
		}
		cursors[cursorOwner][ROW] = row;
		cursors[cursorOwner][COL] = col;
		cursors[cursorOwner][ROW_PAINT] = row - cursorSizeH;
		cursors[cursorOwner][COL_PAINT] = col - cursorSizeH;
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (debug) {
			if (screenX >= gc.menuHeight
					&& screenX <= gc.fieldWidth + gc.menuHeight) {
				switch (button) {
				case Buttons.LEFT:
					setCursorHotspot(RED_SIVI, gc.screenHeight - screenY,
							screenX);
					break;
				case Buttons.RIGHT:
					setCursorHotspot(GREEN_SIVI, gc.screenHeight - screenY,
							screenX);
					break;
				}
			}
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		/*if (screenX >= gc.menuHeight
				&& screenX <= gc.fieldWidth + gc.menuHeight) {
			setCursorHotspot(RED_SIVI, gc.screenHeight - screenY, screenX);
			setCursorHotspot(GREEN_SIVI, gc.screenHeight - screenY, screenX);
		}*/
		// return true;
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * in order row, col
	 * 
	 * @param row
	 * @param col
	 * @return
	 */
	public Vector2 getCenterOfField(int row, int col) {
		float x = (row + .5f) * gc.rowsCoef;
		float y = (col + .5f) * gc.colsCoef + gc.menuHeight;
		return new Vector2(x, y);
	}

	public int[] getRowAndColOfCursor(int cursorOwner) {
		float row = cursors[cursorOwner][ROW];
		float col = cursors[cursorOwner][COL];
		int rowi = (int) (row / gc.rowsCoef);
		int coli = (int) ((col - gc.menuHeight) / gc.colsCoef);
		return new int[] { rowi, coli };
	}

	public class ConstructBuildingListener extends ClickListener {
		private final Sivi ownerS;
		private final int ownerI;
		private final int buildingID;

		public ConstructBuildingListener(Sivi owner, int buildingID) {
			this.buildingID = buildingID;
			ownerS = owner;
			ownerI = owner.ordinal;
		}

		@Override
		public void clicked(InputEvent event, float x, float y) {
			super.clicked(event, x, y);
			int[] pos = getRowAndColOfCursor(ownerI);
			buildBuilding(buildingID, ownerS, pos[0], pos[1]);
		}
	}

	public class MovepadInputListener extends InputListener {
		private final Sivi owner;

		private final float size, sizeHalf;

		public MovepadInputListener(Sivi owner) {
			this.owner = owner;
			size = gc.menuHeight;
			sizeHalf = size / 2;
		}

		@Override
		public boolean touchDown(InputEvent event, float x, float y,
				int pointer, int button) {
			// System.out.format("down x: %.2f y: %.2f\n", x, y);
			setUnrecomputedVector(x, y);
			return true;
		}

		@Override
		public void touchDragged(InputEvent event, float x, float y, int pointer) {
			// System.out.format("dragged x: %.2f y: %.2f\n", x, y);
			setUnrecomputedVector(x, y);
		}

		@Override
		public void touchUp(InputEvent event, float x, float y, int pointer,
				int button) {
			// System.out.format("touchup x: %.2f y: %.2f\n", x, y);
			setRecomputedVec(0, 0);
		}

		/**
		 * range checked
		 * 
		 * @param x
		 * @param y
		 */
		private void setUnrecomputedVector(float x, float y) {
			x = Math.max(x, 0);
			x = Math.min(x, size);
			y = Math.max(y, 0);
			y = Math.min(y, size);

			x = x / sizeHalf - 1; // from 0_size to -1_1
			y = y / sizeHalf - 1; // y = y/size; y = y*2 - 1;

			setRecomputedVec(x, y);
		}

		/**
		 * from -1 to 1
		 * 
		 * @param x
		 * @param y
		 */
		private void setRecomputedVec(float x, float y) {
			movepadVectors[owner.ordinal][X] = x;
			movepadVectors[owner.ordinal][Y] = y;
		}
	}

	public class PauseListener extends ClickListener {
		private final Sivi owner;

		public PauseListener(Sivi owner) {
			this.owner = owner;
		}

		@Override
		public void clicked(InputEvent e, float x, float y) {
			super.clicked(e, x, y);
			System.out.println(owner + " has paused the game");
		}
	}

}
