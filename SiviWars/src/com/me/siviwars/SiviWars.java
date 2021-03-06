package com.me.siviwars;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.me.siviwars.buildings.Building;
import com.me.siviwars.drawing.BuildingHPBars;
import com.me.siviwars.pools.ColorPool;
import com.me.siviwars.pools.MenuPool;
import com.me.siviwars.pools.TexturePool;
import com.me.siviwars.units.Unit;

public class SiviWars implements ApplicationListener {
	private boolean paused = false;

	private static SiviWars _this;

	public static SiviWars getInstance() {
		return _this;
	}

	public static final float DEBUG_SPEED_COEF = 2.5f;// */15;

	public static final boolean debug = /*false; // */false; // for IEH

	public static final int RED_SIVI = InputEventHandler.RED_SIVI,
			GREEN_SIVI = InputEventHandler.GREEN_SIVI,
			ROW = InputEventHandler.ROW, COL = InputEventHandler.COL,
			ROW_PAINT = InputEventHandler.ROW_PAINT,
			COL_PAINT = InputEventHandler.COL_PAINT;

	private GameField gf;
	private GameConfig gc;
	private ColorPool cp;

	private SpriteBatch batch;

	private ShapeRenderer renderer;

	private Texture[][] groundTextures;

	private int rows, cols;

	private float rowsCoef, colsCoef;

	private float renderStep;

	private float spaceSmall, spaceSmall2;

	private float menuHeight;

	private Stage stage;

	private Texture pointerRed, pointerGreen;

	private InputEventHandler ieh;

	private MenuPool redMenuPool, greenMenuPool;

	private ElemetDrawer elemetRed, elemetGreen;

	private TexturePool tp;

	private BuildingHPBars bhpb;

	private BitmapFont font;// = new BitmapFont();

	private Sivi winner;

	private int pointsRed, pointsGreen;

	/*Gdx.files.internal("Calibri.fnt"),
	Gdx.files.internal("Calibri.png"), false);*/

	// private final DelayedSpreaderMultiplex dsmx = new
	// DelayedSpreaderMultiplex();

	private int valueToScale(float f) { // for sivi, round down
		return (int) (f / renderStep);
	}

	private final float maxDelta = 1f;

	/**
	 * start new game, like after one player has won
	 */
	public void newGame() {
		winner = null;
		gf.clearAll();
		ieh.globalBuildEnabled = true;

		// nexuses
		gf.addBuilding(Building.redNexus = Building.createBuilding(
				Building.BUILDING_NEXUS, gf, rows / 2, 4, Sivi.RED));
		gf.addBuilding(Building.greenNexus = Building.createBuilding(
				Building.BUILDING_NEXUS, gf, rows / 2, cols - 5, Sivi.GREEN));

		// tester = new BaseUnit(Building.redNexus);

		Vector2 redStart = ieh.getCenterOfField(rows / 2, 4);
		ieh.setCursorHotspot(RED_SIVI, redStart.x, redStart.y);
		Vector2 greenStart = ieh.getCenterOfField(rows / 2, cols - 5);
		ieh.setCursorHotspot(GREEN_SIVI, greenStart.x, greenStart.y);

		Building redFountain = Building.createBuilding(
				Building.BUILDING_FOUNTAIN, gf, rows / 2, 1, Sivi.RED);
		gf.addBuilding(redFountain);

		Building greenFountain = Building.createBuilding(
				Building.BUILDING_FOUNTAIN, gf, rows / 2, cols - 2, Sivi.GREEN);
		gf.addBuilding(greenFountain);

		gf.reevaluateBuildingPlaces(0, 0, rows - 1, cols - 1);

		// setPaused(false); dont need this
	}

	@Override
	public void create() {
		_this = this;

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

		cp = ColorPool.getColorPool();
		cp.setGameConfig(gc);

		batch = new SpriteBatch();
		renderer = new ShapeRenderer();

		tp = TexturePool.getTexturePool();
		tp.init();

		pointerRed = new Texture(
				Gdx.files.internal("textures/util/pointer_red.png"));
		pointerGreen = new Texture(
				Gdx.files.internal("textures/util/pointer_green.png"));

		ieh = new InputEventHandler(gc, gf);

		groundTextures = new Texture[rows][cols];

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				groundTextures[i][j] = tp.getRandomTerrainTexture();
			}
		}

		// fountains

		// building creation moved to newGame()

		newGame();

		InputMultiplexer im = new InputMultiplexer();

		stage = new Stage();

		redMenuPool = new MenuPool(Sivi.RED, ieh, gc);
		greenMenuPool = new MenuPool(Sivi.GREEN, ieh, gc);

		stage.addActor(redMenuPool.createTable());
		stage.addActor(greenMenuPool.createTable());

		Actor redMp = createMovePad(Sivi.RED);
		stage.addActor(redMp);

		Actor greenMp = createMovePad(Sivi.GREEN);
		greenMp.setPosition(gc.screenWidth - gc.menuHeight, gc.screenHeight
				- gc.menuHeight);
		stage.addActor(greenMp);

		elemetRed = new ElemetDrawer(Sivi.RED, gc.menuHeight - gc.elemetHeight,
				gc.menuHeight, gc.elemetHeight, gc.screenHeight - gc.menuHeight);

		elemetGreen = new ElemetDrawer(Sivi.GREEN, gc.menuHeight
				+ gc.fieldWidth, 0, gc.elemetHeight, gc.screenHeight
				- gc.menuHeight);

		im.addProcessor(stage);
		im.addProcessor(ieh);

		// gf.addUnit(new Unit(Building.redNexus));

		bhpb = new BuildingHPBars(gc);

		Gdx.input.setInputProcessor(im);

		// winner = null;

		// System.out.println(Gdx.files.internal("fonts/arial-15.fnt"));
		font = new BitmapFont();
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
		redMenuPool.setPauseButton(paused);
		greenMenuPool.setPauseButton(paused);
		if (!paused && winner != null) {
			newGame();
		}
	}

	public void pauseUnpause() {
		setPaused(!paused);
	}

	@Override
	public void dispose() {
		stage.dispose();
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(.6f, 1f, .6f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		if (winner != null && !paused) {
			setPaused(true);
		}

		final float delta = Math.min(Gdx.graphics.getDeltaTime()
				* DEBUG_SPEED_COEF, maxDelta);

		if (!paused) {
			gf.routineActionBuildings(delta);
			// dsmx.addDelta(delta);
			gf.spreadSivi(delta);
			gf.siviSweep();
			gf.unitsAction(delta);
		}
		ieh.routineAction(delta); // this only moves cursors

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
					}// */
				} else if (gf.siviGreen[i][j] != 0) {
					c = cp.greenSiviColors[h][valueToScale(gf.siviGreen[i][j])];
					// c = Color.GREEN; // nope
				} else {
					c = cp.groundColors[h];
				}
				batch.setColor(c);
				batch.draw(groundTextures[i][j], j * colsCoef + menuHeight, i
						* rowsCoef, colsCoef, rowsCoef);
			}
		}

		batch.setColor(Color.WHITE);

		// for (Building b : gf.buildings) {
		Building b;
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				if ((b = gf.buildings[row][col]) != null) {
					batch.draw(b.texture, col * colsCoef + spaceSmall
							+ menuHeight, row * rowsCoef + spaceSmall, colsCoef
							- spaceSmall2, rowsCoef - spaceSmall2);
				}
			}
		}

		batch.end();

		Gdx.gl20.glLineWidth(2);
		renderer.begin(ShapeType.Line);
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {
				if ((b = gf.buildings[row][col]) != null) {
					bhpb.drawHPBar(b, renderer);
				}
			}
		}
		renderer.end();

		batch.begin();
		// paint units
		Unit u = gf.unitHead.next;
		while (u != gf.unitHead) {
			batch.setColor(cp.getBaseColor(u.owner));
			batch.draw(tp.unit, u.yPos - colsCoef / 3, u.xPos - rowsCoef / 3,
					colsCoef * 2 / 3, rowsCoef * 2 / 3);
			u = u.next;
		}
		batch.end();

		renderer.begin(ShapeType.Filled);
		elemetRed.drawElevem();
		elemetGreen.drawElevem();
		renderer.end();

		stage.act(delta);
		stage.draw();

		// pointers
		batch.begin();

		batch.setColor(Color.WHITE);
		batch.draw(pointerRed, ieh.cursors[RED_SIVI][COL_PAINT],
				ieh.cursors[RED_SIVI][ROW_PAINT], ieh.cursorSize,
				ieh.cursorSize);

		// batch.setColor(cp.baseGreen);
		batch.draw(pointerGreen, ieh.cursors[GREEN_SIVI][COL_PAINT],
				ieh.cursors[GREEN_SIVI][ROW_PAINT], ieh.cursorSize,
				ieh.cursorSize);

		batch.setColor(cp.baseRed);
		batch.draw(pointerRed, ieh.cursors[RED_SIVI][COL_PAINT],
				ieh.cursors[RED_SIVI][ROW_PAINT], ieh.cursorSize,
				ieh.cursorSize);

		batch.setColor(cp.baseGreen);
		batch.draw(pointerGreen, ieh.cursors[GREEN_SIVI][COL_PAINT],
				ieh.cursors[GREEN_SIVI][ROW_PAINT], ieh.cursorSize,
				ieh.cursorSize);
		// batch.end();

		batch.setColor(Color.WHITE);
		// font.draw(batch, Gdx.graphics.getFramesPerSecond() + "", 10, 20);
		if (winner != null) {
			String msg = String.format(
					"Player on %s has won the match, score: left=%d, right=%d",
					winner == Sivi.RED ? "left" : "right", pointsRed,
					pointsGreen);
			font.draw(batch, msg, 200, 200);
		}
		batch.end();

	}

	private class ElemetDrawer {

		private final Sivi owner;
		float x, y;
		float width;
		float height;
		float step;
		float multiplicativeConst;

		public ElemetDrawer(Sivi owner, float x, float y, float width,
				float height) {
			this.owner = owner;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			step = height / (gc.maxSiviLevel + 1); // ground 0
			multiplicativeConst = (height - step) / gc.maxSiviLevel;
			// believe it
		}

		private void drawRect(Color c, float ry, float rh) {
			if (owner == Sivi.RED) {
				ry = height - ry - rh;
			}
			renderer.setColor(c);
			renderer.rect(x, y + ry, width, rh);
		}

		private void drawElevem() {
			int[] pos = ieh.getRowAndColOfCursor(owner.ordinal);
			int rowi = pos[0];
			int coli = pos[1];

			// step *= 2;
			// System.out.format("s: %.2f\n", step);

			int terh = gf.terrainHeight[rowi][coli];

			// batch.draw(region, x + imageX, y + imageY, getOriginX() - imageX,
			// getOriginY() - imageY, imageWidth, imageHeight, scaleX,
			// scaleY, rotation);

			for (int i = 0; i <= terh; i++) {
				Color c = cp.groundColors[i];
				drawRect(c, i * step, step);
				/*renderer.setColor(c);
				// renderer.rect(i * step, 0, step, height);
				// renderer.rect(x, y, i, i, originX, originY, rotation);
				renderer.rect(i * step, 0, step, height);*/
			}

			Color c;
			float count;

			if (gf.siviRed[rowi][coli] != 0) {
				c = cp.redSiviColors[terh][valueToScale(count = gf.siviRed[rowi][coli])];
			} else if (gf.siviGreen[rowi][coli] != 0) {
				c = cp.greenSiviColors[terh][valueToScale(count = gf.siviGreen[rowi][coli])];
			} else {
				return;
			}

			count *= multiplicativeConst;

			drawRect(c, (terh + 1) * step, count);
		}
	}

	@Override
	public void resize(int width, int height) {
		stage.setViewport(width, height, true);
	}

	@Override
	public void pause() {
		// System.out.println("pause");
	}

	@Override
	public void resume() {
		// System.out.println("resume");
	}

	private Actor createMovePad(Sivi owner) {
		Image i = new Image(tp.movepad);
		i.setColor(owner == Sivi.RED ? gc.redSiviColor : gc.greenSiviColor);
		i.setHeight(gc.menuHeight);
		i.setWidth(gc.menuHeight);
		i.addListener(ieh.new MovepadInputListener(owner));
		return i;
	}

	public void setWinner(Sivi owner) {
		winner = owner;
		ieh.globalBuildEnabled = false;
		if (winner == Sivi.RED) {
			pointsRed++;
		} else {
			pointsGreen++;
		}
	}

}