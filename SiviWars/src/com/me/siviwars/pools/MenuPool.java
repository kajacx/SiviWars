package com.me.siviwars.pools;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.me.siviwars.GameConfig;
import com.me.siviwars.InputEventHandler;
import com.me.siviwars.Sivi;
import com.me.siviwars.buildings.Building;

public class MenuPool {

	private final Sivi owner;
	private final InputEventHandler ieh;
	// private final TexturePool tp;
	private final GameConfig gc;

	public MenuPool(Sivi owner, InputEventHandler ieh, GameConfig gc) {
		this.owner = owner;
		this.ieh = ieh;
		// this.tp = TexturePool.getTexturePool();
		this.gc = gc;
	}

	/*private Actor createElevationMeter() {
		Actor em = /*new Actor();//ieh.new ElevationMeter(owner);
		em.setSize(gc.screenHeight - gc.menuHeight, gc.menuHeight);
		// em.setRotation(90);
		return em;
	}*/

	private Actor createPauseButton() {
		Image i = new Image(TexturePool.getTexturePool().pauseButton);
		i.addListener(ieh.new PauseListener(owner));
		return i;
	}

	private Actor createBuildingButton(int id) {
		Image i = new Image(TexturePool.getTexturePool().getBuildingTexture(id));
		/*i.setSize(s, s);
		i.setScaling(Scaling.none);//*/
		i.addListener(ieh.new ConstructBuildingListener(owner, id));
		return i;
	}

	public Table createTestTable() {
		Table test = new Table();

		test.align(Align.center);

		float width = gc.screenHeight - gc.menuHeight;
		float height = gc.menuHeight - gc.elemetHeight;

		int noOfElements = 4;

		test.setSize(width, height);

		test.add(createPauseButton()).size(width / noOfElements, height);
		test.add(createBuildingButton(Building.BUILDING_FOUNTAIN)).size(
				width / noOfElements, height);

		test.setFillParent(false);

		test.setTransform(true);

		float pos = owner == Sivi.RED ? gc.screenHeight : gc.screenWidth;
		pos *= .5f;
		test.setOrigin(pos, pos);// */

		/*test.setHeight(gc.menuHeight - gc.elemetHeight);
		test.setWidth(gc.menuHeight - gc.elemetHeight);*/
		test.setRotation(owner == Sivi.RED ? -90 : 90);

		return test;
	}

	public Table createRootTable() {
		Table root = new Table();

		/*root.add(createMovepad());
		root.add(createMovepad());
		root.add(createMovepad());
		root.add(createMovepad());// */
		// root.add(createElevationMeter());

		root.setFillParent(false);

		root.setTransform(true);
		root.setHeight(gc.menuHeight);
		root.setWidth(gc.screenHeight);
		root.setRotation(owner == Sivi.RED ? -90 : 90);

		float pos = owner == Sivi.RED ? gc.screenHeight : gc.screenWidth;
		pos *= .5f;
		root.setOrigin(pos, pos);// */

		return root;
	}
}
