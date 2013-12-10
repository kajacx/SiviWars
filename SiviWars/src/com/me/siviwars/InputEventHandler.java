package com.me.siviwars;

import com.badlogic.gdx.InputProcessor;

public class InputEventHandler implements InputProcessor {

	public static final int RED_SIVI = 0, GREEN_SIVI = 1, ROW = 0, COL = 1,
			ROW_PAINT = 2, COL_PAINT = 3;

	public float[][] cursors = new float[2][4];

	public float cursorSize = 32;
	public float cursorSizeH = cursorSize / 2;

	private final GameConfig gc;

	InputEventHandler(GameConfig gc) {
		this.gc = gc;
		setCursorHotspot(RED_SIVI, 50, 50);
		setCursorHotspot(GREEN_SIVI, 50, 50);
	}

	/**
	 * measured from top
	 * 
	 * @param cursor
	 * @param row
	 * @param col
	 */
	void setCursorHotspot(int cursorOwner, float row, float col) {
		cursors[cursorOwner][ROW] = row;
		cursors[cursorOwner][COL] = col;
		cursors[cursorOwner][ROW_PAINT] = gc.fieldHeight - row - cursorSizeH;
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
		// TODO Auto-generated method stub
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
		setCursorHotspot(RED_SIVI, screenY, screenX);
		setCursorHotspot(GREEN_SIVI, screenY, screenX);
		return true;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

}
