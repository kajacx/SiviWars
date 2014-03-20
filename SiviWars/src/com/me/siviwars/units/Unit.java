package com.me.siviwars.units;

import com.me.siviwars.GameField;
import com.me.siviwars.InputEventHandler;
import com.me.siviwars.Sivi;
import com.me.siviwars.buildings.Building;
import com.me.siviwars.interfaces.Ownable;
import com.me.siviwars.interfaces.RoutineAction;

public class Unit implements Ownable, RoutineAction {

	public Unit next, previous; // for link list

	public float speed = 10f, siviCarried = 7.5f;

	public float xPos, yPos;

	public Sivi owner, enemy;

	private float xMov, yMov; // normalized

	// private float xDes, yDes;

	private int xDes, yDes; // will be going on field

	public Unit(Building hostBuilding) {
		if (hostBuilding == null) {
			// this is created as head element
			next = previous = this;
			return;
		}
		/*Vector2 s = InputEventHandler._this.getCenterOfField(hostBuilding.row,
				hostBuilding.col);*/
		xPos = hostBuilding.centerPoint.x;
		yPos = hostBuilding.centerPoint.y;
		owner = hostBuilding.owner;
		enemy = owner.invert();

		Building en = Building.getNexus(owner.invert()); // enemy nexus
		xDes = en.col;
		yDes = en.row;

		float xFin = en.centerPoint.x;
		float yFin = en.centerPoint.y;

		xMov = xFin - xPos;
		yMov = yFin - yPos;

		float norm = (float) Math.sqrt(xMov * xMov + yMov * yMov);
		xMov /= norm;
		yMov /= norm;
		// all done
	}

	public void destroy() {
		GameField._this.removeUnit(this);
	}

	public void explode(int row, int col) {
		GameField._this.addSiviSoft(row, col, siviCarried, owner);
		destroy();
	}

	@Override
	public void routineAction(float time) {
		float xNew = xPos + time * xMov * speed;
		float yNew = yPos + time * yMov * speed;
		int[] nPos = InputEventHandler._this.getRowAndColOfPosition(xNew, yNew);
		int nRow = nPos[0], nCol = nPos[1];
		if (!GameField._this.boundCheck(nRow, nCol)) {
			destroy();
		} else {
			if (GameField._this.getField(owner)[nRow][nCol] == 0) {
				explode(nRow, nCol);
			} else if (nRow == yDes && nCol == xDes) {
				explode(nRow, nCol);
			} else { // nothing happens
				xPos = xNew;
				yPos = yNew;
			}
		}
	}

	@Override
	public Sivi getOwner() {
		return owner;
	}

}
