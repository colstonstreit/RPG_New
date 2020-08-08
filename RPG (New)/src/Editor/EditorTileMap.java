package Editor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import javax.swing.JOptionPane;

import Engine.Game;
import Engine.Tools;
import Play.Tile;
import Play.TileMap;

public class EditorTileMap extends TileMap {

	/**
	 * Loads a map from the given path as a file.
	 */
	public EditorTileMap(Game game, String path) {
		super(game, path);
		load(path, Tools.ResourceLoader.LOAD_FILE);
		this.game = game;
	}

	/**
	 * Loads a new map of the given dimensions width and height, and defaults all values to be -1 for empty and non-solid everywhere.
	 */
	public EditorTileMap(Game game, int width, int height) {
		super(game, width, height);
		this.numWide = width;
		this.numTall = height;
		this.numLayers = 1;

		tileData = new int[numLayers][height][width];
		for (int z = 0; z < numLayers; z++) {
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					tileData[z][y][x] = -1;
				}
			}
		}
		solidData = new boolean[height][width];
		this.game = game;
	}

	public void tick(int px, int py) {

		// Calculate the x and y coordinates of the tile that the mouse is pressed over (tick() only called when mouse is pressed)
		if (EditorState.currentAction == null) return;
		Tools.fRect mousePos = game.mouseBounds();
		int tx = (int) (mousePos.x - px) / EditorState.tSize - ((mousePos.x - px < 0) ? 1 : 0);
		int ty = (int) (mousePos.y - py) / EditorState.tSize - ((mousePos.y - py < 0) ? 1 : 0);

		// Handle the corresponding event based on the Editor's currentAction
		switch (EditorState.currentAction) {
			case SetBrush: //////////////////// Brush: Set Tile to Selected Tile ////////////////////
				setTile(tx, ty, EditorState.selectedLayer);
				break;
			case AddColumn: //////////////////// Add a new Column ////////////////////
				if (ty >= 0 && ty < numTall && System.currentTimeMillis() - EditorState.sizeChangingTimer >= EditorState.sizeChangingDelay) {
					EditorState.sizeChangingTimer = System.currentTimeMillis();
					tx = (int) Game.clamp(tx, 0, numWide);
					int[][][] tempData = new int[numLayers][numTall][numWide + 1];
					boolean[][] tempSolidData = new boolean[numTall][numWide + 1];
					for (int z = 0; z < numLayers; z++) {
						for (int y = 0; y < numTall; y++) {
							int count = 0;
							for (int x = 0; x < numWide + 1; x++) {
								if (x == tx) {
									tempData[z][y][x] = EditorState.selectedTileIndex;
									count++;
								} else {
									tempData[z][y][x] = tileData[z][y][x - count];
									tempSolidData[y][x] = solidData[y][x - count];
								}
							}
						}
					}
					tileData = tempData;
					solidData = tempSolidData;
					numWide++;
				}
				break;
			case RemoveColumn: //////////////////// Remove a Column ////////////////////
				if (numWide > 1 && tx >= 0 && tx < numWide && ty >= 0 && ty < numTall
						&& System.currentTimeMillis() - EditorState.sizeChangingTimer >= EditorState.sizeChangingDelay) {
					EditorState.sizeChangingTimer = System.currentTimeMillis();
					int[][][] tempData = new int[numLayers][numTall][numWide - 1];
					boolean[][] tempSolidData = new boolean[numTall][numWide - 1];
					for (int z = 0; z < numLayers; z++) {
						for (int y = 0; y < numTall; y++) {
							int count = 0;
							for (int x = 0; x < numWide - 1; x++) {
								if (x == tx) count++;
								tempData[z][y][x] = tileData[z][y][x + count];
								tempSolidData[y][x] = solidData[y][x + count];
							}
						}
					}
					tileData = tempData;
					solidData = tempSolidData;
					numWide--;
				}
				break;
			case AddRow: //////////////////// Add a new Row ////////////////////
				if (tx >= 0 && tx < numWide && System.currentTimeMillis() - EditorState.sizeChangingTimer >= EditorState.sizeChangingDelay) {
					EditorState.sizeChangingTimer = System.currentTimeMillis();
					ty = (int) Game.clamp(ty, 0, numTall);
					int[][][] tempData = new int[numLayers][numTall + 1][numWide];
					boolean[][] tempSolidData = new boolean[numTall + 1][numWide];
					for (int z = 0; z < numLayers; z++) {
						for (int x = 0; x < numWide; x++) {
							int count = 0;
							for (int y = 0; y < numTall + 1; y++) {
								if (y == ty) {
									tempData[z][y][x] = EditorState.selectedTileIndex;
									count++;
								} else {
									tempData[z][y][x] = tileData[z][y - count][x];
									tempSolidData[y][x] = solidData[y - count][x];
								}
							}
						}
					}
					tileData = tempData;
					solidData = tempSolidData;
					numTall++;
				}
				break;
			case RemoveRow: //////////////////// Remove a Row ////////////////////
				if (numTall > 1 && tx >= 0 && tx < numWide && ty >= 0 && ty < numTall
						&& System.currentTimeMillis() - EditorState.sizeChangingTimer >= EditorState.sizeChangingDelay) {
					EditorState.sizeChangingTimer = System.currentTimeMillis();
					int[][][] tempData = new int[numLayers][numTall - 1][numWide];
					boolean[][] tempSolidData = new boolean[numTall - 1][numWide];
					for (int z = 0; z < numLayers; z++) {
						for (int x = 0; x < numWide; x++) {
							int count = 0;
							for (int y = 0; y < numTall - 1; y++) {
								if (y == ty) count++;
								tempData[z][y][x] = tileData[z][y + count][x];
								tempSolidData[y][x] = solidData[y + count][x];
							}
						}
					}
					tileData = tempData;
					solidData = tempSolidData;
					numTall--;
				}
				break;
			case SetSolid: //////////////////// Add Colliders and Stuff ////////////////////
				if (tx >= 0 && ty >= 0 && tx < numWide && ty < numTall && (tx != EditorState.lastTXChanged || ty != EditorState.lastTYChanged
						|| System.currentTimeMillis() - EditorState.solidAddingTimer >= EditorState.solidAddingDelay)) {
					solidData[ty][tx] = !solidData[ty][tx];
					EditorState.lastTXChanged = tx;
					EditorState.lastTYChanged = ty;
					EditorState.solidAddingTimer = System.currentTimeMillis();
				}
				break;
			default: //////////////////// Don't do anything otherwise ////////////////////
				break;
		}
	}

	/**
	 * Fills the current layer specified by the Editor with the currently selected tile.
	 */
	public void fillLayer() {
		for (int y = 0; y < numTall; y++) {
			for (int x = 0; x < numWide; x++) {
				setTile(x, y, EditorState.selectedLayer);
			}
		}
	}

	/**
	 * Changes the number of layers based on the parameter dir.
	 * 
	 * @param dir -1 if remove top layer, 1 if add new layer
	 */
	public void changeLayers(int dir) {
		if (dir == -1 && numLayers > 1) {
			int[][][] tempData = new int[numLayers - 1][numTall][numWide];
			for (int z = 0; z < numLayers - 1; z++)
				tempData[z] = tileData[z];
			tileData = tempData;
			numLayers--;
		} else if (dir == 1 && numLayers < EditorState.numLayersAllowed) {
			int[][][] tempData = new int[numLayers + 1][numTall][numWide];
			for (int z = 0; z < numLayers + 1; z++) {
				if (z != numLayers) tempData[z] = tileData[z];
				else {
					for (int y = 0; y < numTall; y++) {
						for (int x = 0; x < numWide; x++) {
							tempData[z][y][x] = -1;
						}
					}
				}
			}
			tileData = tempData;
			numLayers++;
		}
	}

	/**
	 * Fills all of the tiles contained within the given Rectangle r with the currently selected tile.
	 */
	public void fillRect(Rectangle r, int ox, int oy) {
		Tools.Vec2 startPos = new Tools.Vec2(r.x, r.y);
		Tools.Vec2 endPos = new Tools.Vec2(r.x + r.width, r.y + r.height);
		Tools.Vec2 startTPos = new Tools.Vec2((startPos.x - ox) / EditorState.tSize, (startPos.y - oy) / EditorState.tSize);
		Tools.Vec2 endTPos = new Tools.Vec2((endPos.x - ox) / EditorState.tSize, (endPos.y - oy) / EditorState.tSize);

		for (int y = (int) startTPos.y; y <= (int) endTPos.y; y++) {
			for (int x = (int) startTPos.x; x <= (int) endTPos.x; x++) {
				setTile(x, y, EditorState.selectedLayer);
			}
		}
	}

	/**
	 * Saves this map to a file with path dest.
	 */
	public void save(String dest) {
		File file = new File(dest);
		if (file.exists() && JOptionPane.showConfirmDialog(null, "Do you want to overwrite this file?") == JOptionPane.YES_OPTION || !file.exists()) {
			try {
				PrintWriter pw = new PrintWriter(new FileOutputStream(file));
				pw.printf("%d %d %d%nBREAK%n", numWide, numTall, numLayers);

				for (int z = 0; z < numLayers; z++) {
					for (int y = 0; y < numTall; y++) {
						for (int x = 0; x < numWide; x++) {
							pw.print("" + tileData[z][y][x] + ((x == numWide - 1) ? "\n" : " "));
						}
					}
					pw.println("BREAK");
				}

				for (int y = 0; y < numTall; y++) {
					for (int x = 0; x < numWide; x++) {
						pw.print("" + (solidData[y][x] ? 1 : 0) + ((x == numWide - 1) ? "\n" : " "));
					}
				}
				pw.println("BREAK");

				pw.close();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public void render(Graphics g, int px, int py) {
		for (int z = 0; z < numLayers; z++) {
			for (int y = 0; y < numTall; y++) {
				for (int x = 0; x < numWide; x++) {

					// Only draw if on the screen
					if ((x + 1) * EditorState.tSize + px < 0 || x * EditorState.tSize + px > game.getWidth()) continue;
					if ((y + 1) * EditorState.tSize + py < 0 || y * EditorState.tSize + py > game.getHeight()) continue;
					if (tileData[z][y][x] != -1 && EditorState.layerBools[z]) Tile.getTile(tileData[z][y][x]).render(g, x, y, px, py, EditorState.tSize);

					// If top layer, draw either the grid or a red outline if the tile is solid
					if (z == numLayers - 1) {
						if (EditorState.drawingGrid && !solidData[y][x]) {
							g.setColor(Color.white);
							g.drawRect(px + x * EditorState.tSize, py + y * EditorState.tSize, EditorState.tSize - 1, EditorState.tSize - 1);
						}
						if (solidData[y][x]) {
							g.setColor(Color.red);
							g.drawRect(px + x * EditorState.tSize, py + y * EditorState.tSize, EditorState.tSize - 1, EditorState.tSize - 1);
						}
					}
				}
			}
		}
	}

	/**
	 * Sets the tile of the zth layer with coordinates <x, y> to the currently selected tile in the Editor. Does nothing if out of bounds.
	 */
	public void setTile(int x, int y, int z) {
		if (z >= numLayers || y >= numTall || x >= numWide || x < 0 || y < 0 || z < 0) return;
		tileData[z][y][x] = EditorState.selectedTileIndex;
	}

	/**
	 * Returns the width of the map in tiles.
	 */
	public int numWide() { return numWide; }

	/**
	 * Returns the height of the map in tiles.
	 */
	public int numTall() { return numTall; }

	/**
	 * Returns the number of layers in this map.
	 */
	public int numLayers() { return numLayers; }

}
