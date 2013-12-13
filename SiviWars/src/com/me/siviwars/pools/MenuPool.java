package com.me.siviwars.pools;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.me.siviwars.GameConfig;
import com.me.siviwars.InputEventHandler;
import com.me.siviwars.Sivi;

public class MenuPool {

	private final Sivi owner;
	private final InputEventHandler ieh;
	private final TexturePool tp;
	private final GameConfig gc;

	public MenuPool(Sivi owner, InputEventHandler ieh, GameConfig gc) {
		this.owner = owner;
		this.ieh = ieh;
		this.tp = TexturePool.getTexturePool();
		this.gc = gc;
	}

	private Actor createElevationMeter() {
		Actor em = /*new Actor();//*/ieh.new ElevationMeter(owner);
		em.setSize(gc.screenHeight - gc.menuHeight, gc.menuHeight);
		// em.setRotation(90);
		return em;
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
