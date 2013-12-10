package com.me.siviwars;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.me.siviwars.buildings.Building;
import com.me.siviwars.buildings.BuildingConstruction;
import com.me.siviwars.buildings.ConstructedBuilding;
import com.me.siviwars.buildings.Fountain;
import com.me.siviwars.pools.ColorPool;
import com.me.siviwars.pools.TexturePool;

public class SiviWars implements ApplicationListener {

	public static final int RED_SIVI = InputEventHandler.RED_SIVI,
			GREEN_SIVI = InputEventHandler.GREEN_SIVI,
			ROW = InputEventHandler.ROW, COL = InputEventHandler.COL,
			ROW_PAINT = InputEventHandler.ROW_PAINT,
			COL_PAINT = InputEventHandler.COL_PAINT;

	private GameField gf;
	private GameConfig gc;
	private ColorPool cp;

	private SpriteBatch batch;

	private Texture[][] groundTextures;
	private final int noOfGroundTextures = 32;
	private final int groundTexturesDigits = 3;
	private final String groundTextureName = "thirdtest";

	private int rows, cols;

	private float rowsCoef, colsCoef;

	private float renderStep;

	private float spaceSmall, spaceSmall2;

	private float menuHeight;

	private Stage stage;

	private Texture pointerRed, pointerGreen;

	private InputEventHandler ieh;

	private int valueToScale(float f) { // for sivi, round down
		return (int) (f / renderStep);
	}

	private final float maxDelta = 1f;

	@Override
	public void create() {

		gc = new GameConfig();
		gc.fillDefault();

		rows = gc.rows;
		cols = gc.cols;
		rowsCoef = gc.rowsCoef;
		colsCoef = gc.colsCoef;

		spaceSmall = (rowsCoef + colsCoef) / 15; // avr and /7.5
		spaceSmall2 = spaceSmall * 2;

		renderStep = gc.renderStep;

		menuHeight = gc.menuHeight;

		gf = new GameField(gc);

		cp = new ColorPool(gc);

		batch = new SpriteBatch();

		TexturePool.getTexturePool().initBuildingTextures();

		pointerRed = new Texture(
				Gdx.files.internal("textures/util/pointer_red.png"));
		pointerGreen = new Texture(
				Gdx.files.internal("textures/util/pointer_green.png"));

		ieh = new InputEventHandler(gc);

		groundTextures = new Texture[rows][cols];
		String format = "textures/ground_textures/" + groundTextureName + "%0"
				+ groundTexturesDigits + "d.jpg";
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				int id = (int) (Math.random() * noOfGroundTextures);
				String path = String.format(format, id);
				groundTextures[i][j] = new Texture(Gdx.files.internal(path));
			}
		}

		Building redFountain = new Fountain(gf, rows / 2, 1, Sivi.RED, 1);

		gf.addBuilding(redFountain);

		Building greenFountain = new Fountain(gf, rows / 2, cols - 2,
				Sivi.GREEN, 1);

		gf.addBuilding(greenFountain);

		redFountain = new Fountain(gf, rows / 2, 5, Sivi.RED, 1);
		redFountain = new BuildingConstruction(
				(ConstructedBuilding) redFountain);
		gf.addBuilding(redFountain);

		InputMultiplexer im = new InputMultiplexer();

		stage = new Stage();

		im.addProcessor(stage);
		im.addProcessor(ieh);

		Gdx.input.setInputProcessor(im);
	}

	@Override
	public void dispose() {
		stage.dispose();
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		final float delta = Math.min(Gdx.graphics.getDeltaTime(), maxDelta);

		gf.routineActionBuildings(delta);
		gf.spreadSivi(delta);
		gf.siviSweep();

		Color c;
		batch.begin();
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				int h = gf.terrainHeight[i][j];
				/*if (i == 10 && j == 1) {
					System.out.println("Hotspot get: " + gf.siviRed[i][j]);
				}*/
				if (gf.siviRed[i][j] != 0) {
					// try {
					c = cp.redSiviColors[h][valueToScale(gf.siviRed[i][j])];
					/*} catch (ArrayIndexOutOfBoundsException ex) {
						System.out.format("i: %d, j: %d, h: %d, sr: %f\n", i,
								j, h, gf.siviRed[i][j]);
						c = Color.BLUE; // nope
					}*/
				} else if (gf.siviGreen[i][j] != 0) {
					c = cp.greenSiviColors[h][valueToScale(gf.siviGreen[i][j])];
					// c = Color.GREEN; // nope
				} else {
					c = cp.groundColors[h];
				}
				batch.setColor(c);
				batch.draw(groundTextures[i][j], j * colsCoef + menuHeight, i
						* rowsCoef, colsCoef, rowsCoef);
				if (gf.buildings[i][j] != null) {
					batch.setColor(Color.WHITE);
					batch.draw(gf.buildings[i][j].texture, j * colsCoef
							+ spaceSmall + menuHeight, i * rowsCoef
							+ spaceSmall, colsCoef - spaceSmall2, rowsCoef
							- spaceSmall2);
				}
			}
		}

		// System.out.println("")

		// pointers
		batch.setColor(cp.baseRed);
		batch.draw(pointerRed, ieh.cursors[RED_SIVI][COL_PAINT],
				ieh.cursors[RED_SIVI][ROW_PAINT], ieh.cursorSize,
				ieh.cursorSize);

		batch.setColor(cp.baseGreen);
		batch.draw(pointerGreen, ieh.cursors[GREEN_SIVI][COL_PAINT],
				ieh.cursors[GREEN_SIVI][ROW_PAINT], ieh.cursorSize,
				ieh.cursorSize);

		batch.end();

		stage.act(delta);
		stage.draw();

	}

	@Override
	public void resize(int width, int height) {
		stage.setViewport(width, height, true);
	}

	@Override
	public void pause() {
		System.out.println("pause");
	}

	@Override
	public void resume() {
		System.out.println("resume");
	}
}