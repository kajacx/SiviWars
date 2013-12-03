package com.me.siviwars;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class SiviWars implements ApplicationListener {

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

	private int valueToScale(float f) { // for sivi, round down
		return (int) (f / renderStep);
	}

	@Override
	public void create() {

		gc = new GameConfig();
		gc.fillDefault();

		rows = gc.rows;
		cols = gc.cols;
		rowsCoef = gc.rowsCoef;
		colsCoef = gc.colsCoef;

		spaceSmall = (rowsCoef + colsCoef) / 20; // avr and /10
		spaceSmall2 = spaceSmall * 2;

		renderStep = gc.renderStep;

		gf = new GameField(gc);

		cp = new ColorPool(gc);

		batch = new SpriteBatch();

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

		/*for (int i = 0; i < 3; i++) {
			gf.addBuilding(new Fountain(gf, (int) (Math.random() * rows),
					(int) (Math.random() * cols), Sivi.RED, 1));
			gf.addBuilding(new Fountain(gf, (int) (Math.random() * rows),
					(int) (Math.random() * cols), Sivi.GREEN, 1));
		}//*/

		GameBuilding redFountain = new Fountain(gf, rows / 2, 1, Sivi.RED, 1);

		gf.addBuilding(redFountain);

		GameBuilding greenFountain = new Fountain(gf, rows / 2, cols - 2,
				Sivi.GREEN, 1);

		gf.addBuilding(greenFountain);

		redFountain = new Fountain(gf, rows / 2, 5, Sivi.RED, 1);
		redFountain = new BuildingConstruction(redFountain);
		gf.addBuilding(redFountain);

		/*Sound sound = Gdx.audio.newSound(Gdx.files.internal("data/got.mp3"));
		long id = sound.play(0.5f);

		sound.setLooping(id, true); // keeps the sound looping

		/*long id = sound.play(1.0f); // play new sound and keep handle for further manipulation
		sound.stop(id);             // stops the sound instance immediately
		sound.setPitch(id, 2);      // increases the pitch to 2x the original pitch

		id = sound.play(1.0f);      // plays the sound a second time, this is treated as a different instance
		sound.setPan(id, -1, 1);    // sets the pan of the sound to the left side at full volume
		sound.setLooping(id, true); // keeps the sound looping
		sound.stop(id);             // stops the looping sound */

		/*Music music = Gdx.audio.newMusic(Gdx.files.internal("data/got.mp3"));

		music.setVolume(0.5f); // sets the volume to half the maximum volume
		music.setLooping(true); // will repeat playback until music.stop() is
								// called
		music.play(); // resumes the playback

		/*music.setVolume(0.5f);                 // sets the volume to half the maximum volume
		music.setLooping(true);                // will repeat playback until music.stop() is called
		music.stop();                          // stops the playback
		music.pause();                         // pauses the playback
		music.play();                          // resumes the playback
		boolean isPlaying = music.isPlaying(); // obvious :)
		boolean isLooping = music.isLooping(); // obvious as well :)
		float position = music.getPosition();  // returns the playback position in seconds*/
	}

	@Override
	public void dispose() {

	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		final float delta = Gdx.graphics.getDeltaTime();

		gf.routineActionBuildings(delta);
		gf.spreadSivi(delta);
		gf.siviSweep();

		Color c;
		batch.begin();
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				int h = gf.terrainHeight[i][j];
				if (gf.siviRed[i][j] != 0) {
					c = cp.redSiviColors[h][valueToScale(gf.siviRed[i][j])];
				} else if (gf.siviGreen[i][j] != 0) {
					c = cp.greenSiviColors[h][valueToScale(gf.siviGreen[i][j])];
				} else {
					c = cp.groundColors[h];
				}
				batch.setColor(c);
				batch.draw(groundTextures[i][j], j * colsCoef, i * rowsCoef,
						colsCoef, rowsCoef);
				if (gf.buildings[i][j] != null) {
					batch.setColor(Color.WHITE);
					batch.draw(gf.buildings[i][j].texture, j * colsCoef, i
							* rowsCoef, colsCoef, rowsCoef);
				}
			}
		}
		batch.end();

	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}