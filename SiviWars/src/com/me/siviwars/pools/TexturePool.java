package com.me.siviwars.pools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
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

	public Texture movepad;

	/**
	 * 0) builded red 1) builded green 2) building red 3) building green
	 */
	private Texture[] buildingTextures;

	private Texture rotate90(Texture orig, boolean cw) {
		int width = orig.getWidth();
		int height = orig.getHeight();
		// System.out.format("w: %d, h: %d\n", width, height);

		FrameBuffer fb = new FrameBuffer(Format.RGBA8888, width, height, false);

		SpriteBatch batch = new SpriteBatch();
		Matrix4 matrix = new Matrix4();
		matrix.setToOrtho2D(0, 0, width, height);
		batch.setProjectionMatrix(matrix);

		/*Matrix4 m4 = new Matrix4();
		m4.val[Matrix4.M11] = -1;
		batch.setTransformMatrix(m4);// */
		batch.setTransformMatrix(new Matrix4().setToRotation(new Vector3(0, 0,
				1), 90));
		fb.begin();

		batch.begin();

		TextureRegion tr = new TextureRegion(orig);

		tr.flip(!cw, cw);

		batch.draw(tr, 0, -height);

		batch.end();

		fb.end();

		return fb.getColorBufferTexture();
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
		int width = orig.getWidth();
		int height = orig.getHeight();
		// System.out.format("w: %d, h: %d\n", width, height);

		FrameBuffer fb = new FrameBuffer(Format.RGBA8888, width, height, false);

		SpriteBatch batch = new SpriteBatch();
		Matrix4 matrix = new Matrix4();
		matrix.setToOrtho2D(0, 0, width, height);
		batch.setProjectionMatrix(matrix);

		Matrix4 m4 = new Matrix4();
		m4.val[Matrix4.M11] = -1;
		batch.setTransformMatrix(m4);// */
		fb.begin();

		batch.begin();

		// batch.draw(orig, -256, 0);
		// batch.draw(orig, 0, 0);
		batch.setColor(1, 1, 1, 1f);

		batch.draw(orig, 0, -height);

		batch.setColor(1, 1, 1, .8f);

		batch.draw(newT, 0, -height, width, height);

		batch.end();

		fb.end();

		return fb.getColorBufferTexture();
	}

	public void init() {
		movepad = new Texture(Gdx.files.internal("textures/util/movepad.png"));
		initBuildingTextures();
	}

	private void initBuildingTextures() {
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
