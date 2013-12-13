package com.me.siviwars.buildings;

import com.badlogic.gdx.graphics.Texture;
import com.me.siviwars.GameField;
import com.me.siviwars.Sivi;
import com.me.siviwars.interfaces.Healthbar;
import com.me.siviwars.interfaces.RoutineAction;

public abstract class Building implements Healthbar, RoutineAction {

	public static final int BUILDING_FOUNTAIN = 0;

	public int row, col, id;

	public GameField gf;

	public Sivi owner;

	public Texture texture;

	public Building(GameField gf, int row, int col, Sivi owner, int id) {
		this.gf = gf;
		this.row = row;
		this.col = col;
		this.owner = owner;
		this.id = id;
	}

	/**
	 * Takes halfs of allied Sivi this building stands on per second. Need to
	 * call depositSivi() if all Sivi isnt consumed
	 * 
	 * @param time
	 *            Seconds elapsed from last collection
	 * 
	 * @return Amount of sivi collected / withdraw
	 */
	public float withdrawSivi(float time) {
		/*float howMuch = gf.getField(owner)[row][col] / 2;
		howMuch *= time;*/
		float howMuch = time / 2;
		howMuch = Math.min(howMuch, .75f); // to not withdraw too much
		howMuch *= gf.getField(owner)[row][col];
		gf.getField(owner)[row][col] -= howMuch;
		return howMuch;
	}

	/**
	 * puts sivi on the field where buildings stands
	 * 
	 * @param amount
	 *            how much sivi to put
	 * @return how much sivi wasn't added due to overflow
	 */
	public float depositSivi(float amount) {
		float before = gf.getField(owner)[row][col];
		gf.addSiviSoft(row, col, amount, owner);
		return before - gf.getField(owner)[row][col] + amount;
	}

	public static Building createBuilding(int id, GameField gf, int row,
			int col, Sivi owner) {
		switch (id) {
		case BUILDING_FOUNTAIN:
			return new Fountain(gf, row, col, owner, 1);
		default:
			return null;
		}
	}

	@Override
	public Sivi getOwner() {
		return owner;
	}
}
