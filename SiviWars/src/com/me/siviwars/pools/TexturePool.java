package com.me.siviwars.pools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.glutils.PixmapTextureData;
import com.me.siviwars.Sivi;

/**
 * singleton
 * 
 * @author kajacx
 * 
 */
public class TexturePool {

	private static TexturePool instance;

	public static TexturePool getTexturePool() {
		if (instance == null) {
			synchronized (TexturePool.class) {
				if (instance == null) {
					instance = new TexturePool();
				}
			}
		}
		return instance;
	}

	private static final String[] buildingTextureNames = { "fountain0.jpg" };

	private int baseBuildingsCount;

	/**
	 * 0) builded red 1) builded green 2) building red 3) building green
	 */
	private Texture[] buildingTextures;

	private Texture rotate90(Texture t, boolean cw) {
		int width = t.getWidth();
		int height = t.getHeight();

		Pixmap pm = new Pixmap(height, width, Pixmap.Format.RGB888);

		TextureData td = t.getTextureData();
		if (!(td instanceof PixmapTextureData)) {
			td.prepare();
		}
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

	private Texture rotateBuildingByOwner(Texture t, Sivi owner) {
		switch (owner) {
		case RED:
			return rotate90(t, true);
		case GREEN:
			return rotate90(t, false);
		default:
			return null;
		}
	}

	/**
	 * warning: orig may not survive
	 * 
	 * @param orig
	 *            original texture to be rawn on
	 * @param newT
	 *            new texture to draw on the original one
	 * @return original texture with now drawn new texterue on it
	 */
	private Texture combineTextures(Texture orig, Texture newT) {
		TextureData td = newT.getTextureData();
		td.prepare();
		Pixmap pm = td.consumePixmap();

		td = orig.getTextureData();
		td.prepare();
		Pixmap pm2 = td.consumePixmap();

		Texture ret = new Texture(pm2);

		ret.draw(pm, 0, 0);
		pm.dispose();
		pm2.dispose();
		return ret;
	}

	public void initBuildingTextures() {
		baseBuildingsCount = buildingTextureNames.length;
		buildingTextures = new Texture[4 * baseBuildingsCount];

		// unrotated for red and green
		for (int i = 0; i < 2 * baseBuildingsCount; i++) {
			// Sivi owner = i < baseBuildingsCount ? Sivi.RED : Sivi.GREEN;
			// Texture base = new
			// Texture(Gdx.files.internal("textures/buildings/"
			// + buildingTextureNames[i % baseBuildingsCount]));
			// buildingTextures[i] = rotateBuildingByOwner(base, owner);
			String name = "textures/buildings/"
					+ buildingTextureNames[i % baseBuildingsCount];
			buildingTextures[i] = new Texture(Gdx.files.internal(name));
		}

		// now add unrotated construction for red and green
		Texture buildSign = new Texture("textures/buildings/constr.png");
		for (int i = 0; i < 2 * baseBuildingsCount; i++) {
			buildingTextures[i + 2 * baseBuildingsCount] = combineTextures(
					buildingTextures[i], buildSign);
		}

		// now rotate all textures in the correct way
		for (int i = 0; i < 4 * baseBuildingsCount; i++) {
			Sivi owner = (i / baseBuildingsCount) % 2 == 0 ? Sivi.RED
					: Sivi.GREEN; // magic
			buildingTextures[i] = rotateBuildingByOwner(buildingTextures[i],
					owner);
		}
	}

	public Texture getBuildingTexture(int id, Sivi owner, boolean construction) {
		int index = id;
		if (construction) {
			index += 2 * baseBuildingsCount;
		}
		switch (owner) {
		case RED:
			break;
		case GREEN:
			index += baseBuildingsCount;
			break;
		default:
			return null;
		}
		return buildingTextures[index];
	}
}
