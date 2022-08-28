package Editor;

import java.awt.Color;
import java.awt.Graphics;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import javax.swing.JOptionPane;

import Editor.EditorState.MapData;
import Engine.Game;
import Engine.Tools;
import Engine.Tools.fRect;
import Play.Maps.MapManager.Maps;
import Play.Maps.Tile;
import Play.Maps.TileMap;

public class EditorTileMap extends TileMap {

	/**
	 * Loads a map from the given path as a file.
	 */
	public EditorTileMap(Game game, Maps path) {
		super(game, path);
		load(path.toString(), Tools.ResourceLoader.LOAD_FILE);
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

	/**
	 * Does all of the editor tile changing capabilities.
	 * 
	 * @param px The x position where the map has begun being rendered (may be off screen)
	 * @param py The y position where the map has begun being rendered (may be off screen)
	 */
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
				EditorState.addMapState();
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
				EditorState.addMapState();
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
				EditorState.addMapState();
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
				EditorState.addMapState();
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
		EditorState.addMapState();
	}

	/**
	 * Fills all of the tiles contained within the given Rectangle r with the currently selected tile.
	 * 
	 * @param r The rectangle in which the tiles should be filled
	 */
	public void fillRect(fRect r) {
		for (int y = (int) r.y; y < (int) (r.y + r.height); y++) {
			for (int x = (int) r.x; x < (int) (r.x + r.width); x++) {
				setTile(x, y, EditorState.selectedLayer);
			}
		}
		// Add new state after having filled the rectangle
		EditorState.addMapState();
	}

	/**
	 * Fills a rectangle r with the data from the provided mapData, and adds the state to the undo list if addState=true.
	 * 
	 * @param r        The rectangle to be altered
	 * @param mapData  The MapData that the new data should come from
	 * @param addState whether or not you want to add this new state to the undo list
	 */
	public void fillRect(fRect r, MapData mapData, boolean addState) {
		int index = EditorState.selectedTileIndex;
		for (int z = 0; z < mapData.tiles.length; z++) {
			for (int y = (int) r.y; y < (int) (r.y + r.height); y++) {
				for (int x = (int) r.x; x < (int) (r.x + r.width); x++) {
					if (x < 0 || x >= numWide || y < 0 || y >= numTall) continue;

					EditorState.selectedTileIndex = mapData.tiles[z][y - (int) r.y][x - (int) r.x];
					setTile(x, y, z);
					if (z == 0) this.solidData[y][x] = mapData.solids[y - (int) r.y][x - (int) r.x];
				}
			}
		}
		EditorState.selectedTileIndex = index;
		// Add new state after having filled the rectangle if desired
		if (addState) EditorState.addMapState();
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
	 * Returns a mapData representing the selection encased by the provided rectangle r.
	 * 
	 * @param r The rectangle that a mapData should be made of
	 * @return MapData storing the data encased within r
	 */
	public MapData getMapSelection(fRect r) {
		// Check to make sure r bounds are valid
		if (r.x < 0) {
			r.width += r.x;
			r.x = 0;
		} else if (r.x >= numWide) r.width = 0;
		else if (r.x + r.width >= numWide) r.width -= r.x + r.width - numWide;

		if (r.y < 0) {
			r.height += r.y;
			r.y = 0;
		} else if (r.y >= numTall) r.height = 0;
		else if (r.y + r.height >= numTall) r.height -= r.y + r.height - numTall;

		// Create tile data within r
		int[][][] tempData = new int[numLayers][(int) r.height][(int) r.width];
		for (int z = 0; z < numLayers; z++) {
			for (int y = (int) r.y; y < (int) (r.y + r.height); y++) {
				for (int x = (int) r.x; x < (int) (r.x + r.width); x++) {
					tempData[z][y - (int) r.y][x - (int) r.x] = tileData[z][y][x];
				}
			}
		}

		// Create solid data within r
		boolean[][] tempSolidData = new boolean[(int) r.height][(int) r.width];
		for (int y = (int) r.y; y < (int) (r.y + r.height); y++) {
			for (int x = (int) r.x; x < (int) (r.x + r.width); x++) {
				tempSolidData[y - (int) r.y][x - (int) r.x] = solidData[y][x];
			}
		}

		// Return new MapData
		return new MapData(tempData, tempSolidData);
	}

	/**
	 * Reverts the map back to the state comprised by the provided mapData; called from undo/redo buttons.
	 * 
	 * @param mapData the state of the map to go back to
	 */
	public void revert(MapData mapData) {
		numLayers = mapData.tiles.length;
		numTall = mapData.tiles[0].length;
		numWide = mapData.tiles[0][0].length;

		// Copy data from mapData (REAL COPY: you spent 3 hours debugging just to find you actually hadn't been making copies!!)
		tileData = new int[numLayers][numTall][numWide];
		solidData = new boolean[numTall][numWide];
		for (int z = 0; z < numLayers; z++) {
			for (int y = 0; y < numTall; y++) {
				for (int x = 0; x < numWide; x++) {
					tileData[z][y][x] = mapData.tiles[z][y][x];
					if (z == 0) solidData[y][x] = mapData.solids[y][x];
				}
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

	/**
	 * Renders the map beginning at the position px, py.
	 * 
	 * @param g  The graphics object
	 * @param px The x position at which the map is starting to be rendered
	 * @param py The y position at which the map is starting to be rendered
	 */
	public void render(Graphics g, int px, int py) {
		for (int z = 0; z < numLayers; z++) {
			for (int y = 0; y < numTall; y++) {
				for (int x = 0; x < numWide; x++) {

					// Only draw if on the screen
					if ((x + 1) * EditorState.tSize + px < 0 || x * EditorState.tSize + px > game.getWidth()) continue;
					if ((y + 1) * EditorState.tSize + py < 0 || y * EditorState.tSize + py > game.getHeight()) continue;
					if (tileData[z][y][x] != -1 && EditorState.layerBools[z]) Tile.getTile(tileData[z][y][x]).render(g, x, y, px, py, EditorState.tSize);

					// If top layer, draw either the grid or a red outline and black fadeover if the tile is solid
					if (z == numLayers - 1) {
						if (EditorState.drawingGrid && !solidData[y][x]) {
							g.setColor(Color.white);
							g.drawRect(px + x * EditorState.tSize, py + y * EditorState.tSize, EditorState.tSize - 1, EditorState.tSize - 1);
						}
						if (solidData[y][x]) {
							g.setColor(new Color(0, 0, 0, 80));
							g.fillRect(px + x * EditorState.tSize, py + y * EditorState.tSize, EditorState.tSize - 1, EditorState.tSize - 1);
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

}
