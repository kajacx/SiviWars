package com.me.siviwars;

import com.me.siviwars.buildings.Building;
import com.me.siviwars.units.Unit;

public class GameField {
	public static GameField _this;

	public int rows;
	public int cols;
	public float maxValue;
	public float maxTerrainHeight;

	public float baseMinValue; // min value of sivi that can be on a field
	public float flowSpeed = .2f; // lower then half, how much % will flow over
									// 1 sec

	public float[][] siviRed;
	public float[][] siviGreen;

	public int[][] terrainHeight;

	public Building[][] buildings;

	public boolean[][] canBuild;
	// public ArrayList<Building> buildings = new ArrayList<Building>();
	public Unit /*tester, */unitHead;

	SiviSpreader spreaderRed, spreaderGreen;

	public void clearAll() {
		// units
		unitHead.next = unitHead.previous = unitHead;

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				canBuild[i][j] = true;
				buildings[i][j] = null;
				siviRed[i][j] = siviGreen[i][j] = 0;
			}
		}
	}

	/**
	 * checks bounds
	 * 
	 * @param row
	 *            row
	 * @param col
	 *            col
	 * @return true if valid position
	 */
	public boolean boundCheck(int row, int col) {
		return row >= 0 && row < rows && col >= 0 && col < cols;
	}

	private void genRandTerrain() {
		new FloatTerrainGenerator().generate();
	}

	/**
	 * makes explosion in field and surrounding area. GameField overflow ignored
	 * 
	 * @param row
	 * @param col
	 * @param howMuch
	 *            total sivi to be delivered
	 * @param owner
	 */
	public void siviExplosion(int row, int col, float howMuch, Sivi owner) {
		float center = howMuch * .25f; // one fourth
		float adj = center * .5f; // one eighth
		float diag = adj * .5f; // one sixteenth
		addSiviSoft(row, col, center, owner);
		addSiviSoftRangeChecked(row + 1, col, adj, owner);
		addSiviSoftRangeChecked(row, col + 1, adj, owner);
		addSiviSoftRangeChecked(row - 1, col, adj, owner);
		addSiviSoftRangeChecked(row, col - 1, adj, owner);
		addSiviSoftRangeChecked(row + 1, col + 1, diag, owner);
		addSiviSoftRangeChecked(row + 1, col - 1, diag, owner);
		addSiviSoftRangeChecked(row - 1, col + 1, diag, owner);
		addSiviSoftRangeChecked(row - 1, col - 1, diag, owner);
	}

	public void addSiviSoftRangeChecked(int row, int col, float howMuch,
			Sivi owner) {
		if (boundCheck(row, col)) {
			addSiviSoft(row, col, howMuch, owner);
		}
	}

	public void addSiviSoft(int row, int col, float howMuch, Sivi owner) {
		/*System.out.println("Adding " + howMuch + " at row " + row + " and col "
				+ col + " for " + owner);//*/
		float val = getField(owner)[row][col];
		val += howMuch;
		if (val + terrainHeight[row][col] > maxValue) {
			val = maxValue - terrainHeight[row][col];
			// System.out.println("OF " + row + " " + col);
		}
		/*if (row == 10 && col == 1) {
			System.out.println("hotspot set: " + val);
		}*/
		getField(owner)[row][col] = val;
	}

	/**
	 * returns null if there is no building
	 * 
	 * @param row
	 * @param col
	 * @return
	 */
	public Building getBuilding(int row, int col) {
		/*for (Building b : buildings) {
			if (b.row == row && b.col == col) {
				return b;
			}
		}
		return null;//*/
		return buildings[row][col];
	}

	public Sivi getOccupator(int row, int col) {
		if (getField(Sivi.RED)[row][col] > 0) {
			return Sivi.RED;
		}
		if (getField(Sivi.GREEN)[row][col] > 0) {
			return Sivi.GREEN;
		}
		return Sivi.NEUTRAL;
	}

	/**
	 * this will remove the old building
	 * 
	 * @param b
	 */
	public void addBuilding(Building b) {
		/*Building toRemove = getBuilding(b.row, b.col);
		if (toRemove != null) {
			buildings.remove(toRemove);
		}
		buildings.add(b);//*/
		buildings[b.row][b.col] = b;
		for (int i = -1; i <= 1; i++) {
			if ((b.row + i) < 0 || (b.row + i) >= rows) {
				continue;
			}
			for (int j = -1; j <= 1; j++) {
				if ((b.col + j) < 0 || (b.col + j) >= cols) {
					continue;
				}
				canBuild[b.row + i][b.col + j] = false;
			}
		}
	}

	public void removeBuilding(Building b) {
		buildings[b.row][b.col] = null;
		for (int i = -1; i <= 1; i++) {
			if ((b.row + i) < 0 || (b.row + i) >= rows) {
				continue;
			}
			for (int j = -1; j <= 1; j++) {
				if ((b.col + j) < 0 || (b.col + j) >= cols) {
					continue;
				}
				canBuild[b.row + i][b.col + j] = true;
			}
		}
		reevaluateBuildingPlaces(b.row - 2, b.col - 2, b.row + 2, b.col + 2);
	}

	public void routineActionBuildings(final float time) {
		// for (Building[] bs : ) {
		/*for (Building b : (ArrayList<Building>) buildings.clone()) {
			b.routineAction(time);
		}*/
		// }
		Building b;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if ((b = buildings[i][j]) != null) {
					b.takeDamageOrHeal(time);
					if (!b.isDestroyed()) {
						b.routineAction(time);
					}
				}
			}
		}
	}

	public void spreadSivi(final float time) {
		spreaderRed.spreadSivi(time);
		spreaderGreen.spreadSivi(time);

	}

	/*float getSiviHeight(float[][] sivi, int row, int col) {
		return sivi[row][col];
	}*/

	public float getSiviTerrainHeight(float[][] sivi, int row, int col) {
		return terrainHeight[row][col] + sivi[row][col];
	}

	public void reevaluateBuildingPlaces(int fromRow, int fromCol, int toRow,
			int toCol) {
		for (int row = fromRow; row <= toRow; row++) {
			for (int col = fromCol; col <= toCol; col++) {
				if (row < 0 || row >= rows || col < 0 || col >= cols) {
					continue;
				}
				if (buildings[row][col] == null) {
					continue;
				}
				for (int i = -1; i <= 1; i++) {
					if ((row + i) < 0 || (row + i) >= rows) {
						continue;
					}
					for (int j = -1; j <= 1; j++) {
						if ((col + j) < 0 || (col + j) >= cols) {
							continue;
						}
						canBuild[row + i][col + j] = false;
					}
				}
			}
		}
	}

	public GameField(GameConfig gc) {
		this.rows = gc.rows;
		this.cols = gc.cols;
		this.maxValue = gc.maxSiviLevel;
		this.maxTerrainHeight = gc.maxTerrainHeight;

		siviRed = new float[rows][cols];
		siviGreen = new float[rows][cols];

		terrainHeight = new int[rows][cols];

		buildings = new Building[rows][cols];

		canBuild = new boolean[rows][cols];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				canBuild[i][j] = true;
			}
		}

		baseMinValue = gc.renderStep;
		unitHead = new Unit(null);

		genRandTerrain();

		spreaderRed = new UnbufferedSiviSpreader();
		spreaderRed.setOwner(Sivi.RED);
		spreaderGreen = new UnbufferedSiviSpreader();
		spreaderGreen.setOwner(Sivi.GREEN);

		_this = this;
	}

	public void addUnit(Unit u) {
		Unit before = unitHead;
		Unit after = unitHead.next;
		before.next = u;
		u.next = after;
		after.previous = u;
		u.previous = before;
	}

	public void removeUnit(Unit u) {
		Unit before = u.previous;
		Unit after = u.next;
		before.next = after;
		after.previous = before;
	}

	public void unitsAction(float time) {
		Unit current;// = unitHead;
		Unit next = unitHead.next;
		while (next != unitHead) {
			current = next;
			next = current.next;
			current.routineAction(time);
		}
	}

	public float[][] getField(Sivi owner) {
		switch (owner) {
		case RED:
			return siviRed;
		case GREEN:
			return siviGreen;
		default:
			return null;
		}
	}

	/**
	 * kill red and green sivi
	 */
	public void siviSweep() {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				int cmpr = Float.compare(siviRed[i][j], siviGreen[i][j]);
				if (cmpr == 0) {
					siviRed[i][j] = siviGreen[i][j] = 0;
				} else if (cmpr > 0) {
					siviRed[i][j] -= siviGreen[i][j];
					siviGreen[i][j] = 0;
				} else { // cmp < 0
					siviGreen[i][j] -= siviRed[i][j];
					siviRed[i][j] = 0;
				}
			}
		}
	}

	private class FloatTerrainGenerator {
		private final float[][] heights;

		private int[] rowsFixedIndexes, colsFixedIndexes;
		private int numberOfPointsRows, numberOfPointsCols;

		private FloatTerrainGenerator() {
			heights = new float[rows][cols];
		}

		private void markFixedPoints() {
			// chunk size : 4 or more blocks, excluding the fixed lines
			// slice n-1 into block of size 5 (or bigger)

			numberOfPointsRows = (rows - 1) / 5 + 1; // with the last one
			rowsFixedIndexes = new int[numberOfPointsRows];

			numberOfPointsCols = (cols - 1) / 5 + 1; // with the last one
			colsFixedIndexes = new int[numberOfPointsCols];

			float stepRow = (rows - 1f) / (numberOfPointsRows - 1f);
			/*System.out.println("there are " + rows + " and "
					+ numberOfPointsRows + " and stepRow is " + stepRow);*/

			float stepCol = (cols - 1f) / (numberOfPointsCols - 1f);
			/*System.out.println("there are " + cols + " and "
					+ numberOfPointsCols + " and stepCol is " + stepCol);*/

			float rowStacker = 0;
			for (int i = 0; i < numberOfPointsRows; i++) {
				rowsFixedIndexes[i] = (int) rowStacker;
				// System.out.println(rowsFixedIndexes[i]);
				rowStacker += stepRow;
			}

			float colStacker = 0;
			for (int i = 0; i < numberOfPointsCols; i++) {
				colsFixedIndexes[i] = (int) colStacker;
				// System.out.println(colsFixedIndexes[i]);
				colStacker += stepCol;
			}
		}

		private void fillMarkedPoints() {
			for (int i = 0; i < numberOfPointsRows; i++) {
				for (int j = 0; j < numberOfPointsCols; j++) {
					// whole calculation in +2, then -1 at end and round
					heights[rowsFixedIndexes[i]][colsFixedIndexes[j]] = (float) (Math
							.random() * (maxTerrainHeight + 2));
				}
			}
		}

		private void fillFixedLines() {
			// horizontal
			for (int i = 0; i < numberOfPointsRows; i++) {
				for (int j = 0; j < numberOfPointsCols - 1; j++) {
					float start = heights[rowsFixedIndexes[i]][colsFixedIndexes[j]];
					float end = heights[rowsFixedIndexes[i]][colsFixedIndexes[j + 1]];
					int length = colsFixedIndexes[j + 1] - colsFixedIndexes[j];
					float step = (end - start) / length;
					for (int k = 1; k < length; k++) {
						start += step;
						heights[rowsFixedIndexes[i]][colsFixedIndexes[j] + k] = start;
					}
				}
			}

			// vertical
			for (int j = 0; j < numberOfPointsCols; j++) {
				for (int i = 0; i < numberOfPointsRows - 1; i++) {
					float start = heights[rowsFixedIndexes[i]][colsFixedIndexes[j]];
					float end = heights[rowsFixedIndexes[i + 1]][colsFixedIndexes[j]];
					int length = rowsFixedIndexes[i + 1] - rowsFixedIndexes[i];
					float step = (end - start) / length;
					for (int k = 1; k < length; k++) {
						start += step;
						heights[rowsFixedIndexes[i] + k][colsFixedIndexes[j]] = start;
					}
				}
			}
		}

		/**
		 * call with location of fixed points, this is exclusive on all sides
		 * 
		 * @param startX
		 *            row
		 * @param startY
		 *            col
		 * @param endX
		 *            row
		 * @param endY
		 *            col
		 */
		private void fillChunk(int startX, int startY, int endX, int endY) {
			for (int i = startX + 1; i < endX; i++) {
				for (int j = startY + 1; j < endY; j++) {
					float tmp = 0;
					float weightTotal = 0;
					float weightAcctual; // buffer

					// right
					weightAcctual = 1f / (endX - i);
					tmp += heights[endX][j] * weightAcctual;
					weightTotal += weightAcctual;

					// bot
					weightAcctual = 1f / (endY - j);
					tmp += heights[i][endY] * weightAcctual;
					weightTotal += weightAcctual;

					// left
					weightAcctual = 1f / (i - startX);
					tmp += heights[startX][j] * weightAcctual;
					weightTotal += weightAcctual;

					// top
					weightAcctual = 1f / (j - startY);
					tmp += heights[i][startY] * weightAcctual;
					weightTotal += weightAcctual;

					tmp /= weightTotal;
					heights[i][j] = tmp;
				}
			}
		}

		private void fillChunks() {
			for (int i = 0; i < numberOfPointsRows - 1; i++) {
				for (int j = 0; j < numberOfPointsCols - 1; j++) {
					fillChunk(rowsFixedIndexes[i], colsFixedIndexes[j],
							rowsFixedIndexes[i + 1], colsFixedIndexes[j + 1]);
				}
			}
		}

		private int cast(float what) {
			// ranges were increased by 2
			// so now we need to nerf it a little and do a range check
			int toReturn = Math.round(what - 1);
			if (toReturn < 0) {
				return 0;
			}
			if (toReturn > maxTerrainHeight) {
				return (int) maxTerrainHeight;
			}
			return toReturn;
		}

		private void apply() {
			for (int row = 0; row < rows; row++) {
				for (int col = 0; col < cols; col++) {
					terrainHeight[row][col] = cast(heights[row][col]);
				}
			}
		}

		/*void dumpPrint() {


			/*for (int i = 0; i < rows; i++) {
				for (int j = 0; j < cols; j++) {
					System.out.format("%.2f ", heights[i][j]);
				}
				System.out.println();
			}
			System.out.println();
		}*/

		void generate() {
			markFixedPoints();
			fillMarkedPoints();
			fillFixedLines();
			fillChunks();
			apply();
		}
	}

	interface SiviSpreader {
		public void setOwner(Sivi owner);

		public void spreadSivi(final float sec);
	}

	/*private class MutliThreadUnbufferedSiviSpreader implements SiviSpreader, Runnable {
		private float[][] field;
		private final float maxMult = 1 / 8f; // maximal val
		private final float min = 0.25f;
		private float mult;

		@Override
		public void setOwner(Sivi owner) {
			field = getField(owner);
		}

		// for sovi flowing
		private void resolve2Fields(final int row1, final int col1,
				final int row2, final int col2) {
			float dif = getSiviTerrainHeight(field, row1, col1) // check pairs
					- getSiviTerrainHeight(field, row2, col2);
			if (dif != 0) {
				if ((field[row1][col1] == 0 || field[row2][col2] == 0)
						& Math.abs(dif) < min) {
					return;
				}
				// check for flowing over edges
				if (dif > 0) {
					dif = Math.min(dif, field[row1][col1]);
				} else { // dif < 0
					dif = Math.max(dif, -field[row2][col2]);
				}

				dif *= mult;
				field[row1][col1] -= dif;
				field[row2][col2] += dif;
			}
		}

		private void spreadSivi0(final float sec) {
			// TODO: better implementation of spreading

			mult = 0.5f; // basic
			mult *= sec;
			mult = Math.min(mult, maxMult);

			// only one cache friendly walk
			for (int i = 0; i < rows; i++) { // "down"
				for (int j = 0; j < cols; j++) { // "right"
					if (i != 0) {
						resolve2Fields(i, j, i - 1, j);
					}
					if (j != 0) {
						resolve2Fields(i, j, i, j - 1);
					}
				}
			}
		}

		@Override
		public void spreadSivi(final float sec) {

		}

		@Override
		public void run() {
			// TODO Auto-generated method stub

		}

	}//*/

	private class UnbufferedSiviSpreader implements SiviSpreader {
		private float[][] field;
		private final float maxMult = 1 / 8f; // maximal val
		private final float min = 0.25f;
		private float mult;

		@Override
		public void setOwner(Sivi owner) {
			field = getField(owner);
		}

		// for sovi flowing
		private void resolve2Fields(final int row1, final int col1,
				final int row2, final int col2) {
			float dif = getSiviTerrainHeight(field, row1, col1) // check pairs
					- getSiviTerrainHeight(field, row2, col2);
			// check for flowing over edges, belive it
			if (dif > 0) {
				dif = Math.min(dif, field[row1][col1]);
			} else { // dif < 0
				dif = Math.max(dif, -field[row2][col2]);
			}
			if (dif != 0) {
				if ((field[row1][col1] == 0 || field[row2][col2] == 0)
						& Math.abs(dif) < min) {
					return;
				}

				dif *= mult;
				field[row1][col1] -= dif;
				field[row2][col2] += dif;
			}
		}

		@Override
		public void spreadSivi(final float sec) {
			// TODO: better implementation of spreading

			mult = 0.5f; // basic
			mult *= sec;
			mult = Math.min(mult, maxMult);

			// only one cache friendly walk
			for (int i = 0; i < rows; i++) { // "down"
				for (int j = 0; j < cols; j++) { // "right"
					if (i != 0) {
						resolve2Fields(i, j, i - 1, j);
					}
					if (j != 0) {
						resolve2Fields(i, j, i, j - 1);
					}
				}
			}
		}

	}

	/*private class SiviSpreader {
		private float[][] curSivi, newSivi;

		private final Sivi player;

		SiviSpreader(Sivi player) {
			this.player = player;
			newSivi = new float[rows][cols];
		}

		void spreadSivi(final float sec) {
			loadSivi();
			copySiviValues();
			spreadSivi0(sec);
			applyChanges();
		}

		private void loadSivi() {
			switch (player) {
			case RED:
				curSivi = siviRed;
				break;
			case GREEN:
				curSivi = siviGreen;
				break;
			default:
				throw new RuntimeException("Unrecognised sivi: " + player);
			}
		}

		private void copySiviValues() {
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < cols; j++) {
					newSivi[i][j] = curSivi[i][j];
				}
			}
		}

		private void spreadSivi0(final float sec) {
			// TODO: better implementation of spreading
			float dif;
			float min = 0.15f;
			float mult = 0.4f; // basic
			float maxMult = 1 / 8f; // maximal val

			mult *= sec;
			mult = Math.min(mult, maxMult);

			for (int i = 1; i < rows; i++) { // "down"
				for (int j = 0; j < cols; j++) {
					dif = getSiviTerrainHeight(curSivi, i - 1, j) // check pairs
							- getSiviTerrainHeight(curSivi, i, j);
					if (dif != 0) {
						if ((curSivi[i - 1][j] == 0 || curSivi[i][j] == 0)
								& Math.abs(dif) < min) {
							continue;
						}
						// check for flowing over edges
						if (dif > 0) {
							dif = Math.min(dif, curSivi[i - 1][j]);
						} else { // dif < 0
							dif = Math.max(dif, -curSivi[i][j]);
						}

						dif *= mult;
						newSivi[i - 1][j] -= dif;
						newSivi[i][j] += dif;
					}
				}
			}
			for (int j = 1; j < cols; j++) { // "right"
				for (int i = 0; i < rows; i++) {
					dif = getSiviTerrainHeight(curSivi, i, j - 1) // check pairs
							- getSiviTerrainHeight(curSivi, i, j);
					if (dif != 0) {
						if ((curSivi[i][j - 1] == 0 || curSivi[i][j] == 0)
								& Math.abs(dif) < min) {
							continue;
						}
						// check for flowing over edges
						if (dif > 0) {
							dif = Math.min(dif, curSivi[i][j - 1]);
						} else { // dif < 0
							dif = Math.max(dif, -curSivi[i][j]);
						}
						dif *= mult;
						newSivi[i][j - 1] -= dif;
						newSivi[i][j] += dif;
					}
				}
			}
		}

		private void applyChanges() {
			switch (player) {
			case RED:
				siviRed = newSivi;
				break;
			case GREEN:
				siviGreen = newSivi;
				break;
			default:
				throw new RuntimeException("Unrecognised sivi: " + player);
			}
			newSivi = curSivi; // unused array to be used next time
		}
	}*/

}
