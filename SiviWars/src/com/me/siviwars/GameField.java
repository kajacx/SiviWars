package com.me.siviwars;

import com.me.siviwars.buildings.Building;

public class GameField {
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

	SiviSpreader spreaderRed, spreaderGreen;

	private void genRandTerrain() {
		new FloatTerrainGenerator().generate();
	}

	public void addSiviSoft(int row, int col, float howMuch, Sivi owner) {
		/*System.out.println("Adding " + howMuch + " at row " + row + " and col "
				+ col + " for " + owner);//*/
		float val = getField(owner)[row][col];
		val += howMuch;
		if (val + terrainHeight[row][col] > maxValue) {
			val = maxValue - terrainHeight[row][col];
			System.out.println("OF " + row + " " + col);
		}
		/*if (row == 10 && col == 1) {
			System.out.println("hotspot set: " + val);
		}*/
		getField(owner)[row][col] = val;
	}

	public void addBuilding(Building b) {
		buildings[b.row][b.col] = b;
	}

	public void routineActionBuildings(float time) {
		for (Building[] bs : buildings) {
			for (Building b : bs) {
				if (b != null) {
					b.routineAction(time);
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

	public GameField(GameConfig gc) {
		this.rows = gc.rows;
		this.cols = gc.cols;
		this.maxValue = gc.maxSiviLevel;
		this.maxTerrainHeight = gc.maxTerrainHeight;

		siviRed = new float[rows][cols];
		siviGreen = new float[rows][cols];

		terrainHeight = new int[rows][cols];

		buildings = new Building[rows][cols];

		baseMinValue = gc.renderStep;

		genRandTerrain();

		spreaderRed = new SiviSpreader(Sivi.RED);
		spreaderGreen = new SiviSpreader(Sivi.GREEN);

		/*for (int i = 0; i < rows; i++) {
			siviRed[i][0] = 4;
			siviRed[i][1] = 2;

			siviGreen[i][cols - 2] = 2;
			siviGreen[i][cols - 1] = 4;
		}*/

		/*for (int j = 0; j < rows; j++) {
			for (int i = 0; i < cols; i++) {
				terrainHeight[j][i] = j;
			}
		}*/
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
				} else { // dif < 0
					siviRed[i][j] = 0;
					siviGreen[i][j] -= siviRed[i][j];
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

	private class SiviSpreader {
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

		/*private void resolveNewlyOccupiedFields() {
			for(int i = 0; i<rows; i++) {
				for(int j = 0; j<cols; j++) {
					
				}
			}
		}*/

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
	}

}
