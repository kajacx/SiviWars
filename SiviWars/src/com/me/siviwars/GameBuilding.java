package com.me.siviwars;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;

public abstract class GameBuilding {

	public static final int BUILDING_FOUNTAIN = 0;

	private static final String[] textureNames = { "fountain0.jpg" };

	protected int row, col, id;

	protected GameField gf;

	protected Sivi owner;

	Texture texture;

	protected float price; // how much to build this

	private Texture ttest(Texture t, boolean cw) {
		int width = t.getWidth();
		int height = t.getHeight();

		Pixmap pm = new Pixmap(height, width, Pixmap.Format.RGB888);

		TextureData td = texture.getTextureData();
		td.prepare();
		Pixmap orig = td.consumePixmap();

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (cw) {
					pm.setColor(orig.getPixel(j, height - i));
				} else {
					pm.setColor(orig.getPixel(width - j, i));
				}
				pm.drawPixel(i, j);
			}
		}

		Texture out = new Texture(pm);
		pm.dispose();
		return out;
	}

	@SuppressWarnings("incomplete-switch")
	protected GameBuilding(GameField gf, int row, int col, Sivi owner, int id) {
		this.gf = gf;
		this.row = row;
		this.col = col;
		this.owner = owner;
		this.id = id;

		texture = new Texture(Gdx.files.internal("textures/buildings/"
				+ textureNames[id]));

		switch (owner) {
		case RED:
			texture = ttest(texture, true);
			break;
		case GREEN:
			texture = ttest(texture, false);
			break;
		}

		this.price = getPrice();
	}

	protected abstract float getPrice();

	protected abstract void routineAction(float time);

	/**
	 * Takes halfs of allied Sivi this building stands on per second. Need to
	 * call addSiviSoft() if all Sivi isnt consumed
	 * 
	 * @param time
	 *            Seconds elapsed from last collection
	 * 
	 * @return Amount of sivi collected / withdraw
	 */
	float withdrawSivi(float time) {
		float howMuch = gf.getField(owner)[row][col] / 2;
		howMuch *= time;
		gf.getField(owner)[row][col] -= howMuch;
		return howMuch;
	}
}
