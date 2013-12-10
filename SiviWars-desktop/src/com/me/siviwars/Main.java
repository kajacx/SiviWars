package com.me.siviwars;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "SiviWars";
		cfg.useGL20 = true;
		cfg.width = 720;
		cfg.height = 480;
		// cfg.addIcon("data/icon0.jpg", FileType.Internal);

		new LwjglApplication(new SiviWars(), cfg);
	}
}
