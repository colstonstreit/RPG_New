package Play.Maps;

import java.awt.Graphics;
import java.util.ArrayList;

import Engine.AssetManager;
import Engine.Game;
import Engine.Tools;
import Play.Entities.Dynamic;
import Play.Entities.Entity;
import Play.Maps.MapManager.Maps;

public class TileMap {

	protected Game game; // The instance of the game
	public final Maps id; // The id of the map

	protected int[][][] tileData; // The tile id data for the map
	protected boolean[][] solidData; // The solid data for the map

	protected int numWide, numTall, numLayers; // Size variables of the map

	/**
	 * @param game The instance of the game object
	 * @param id   The id of the map to be loaded
	 */
	public TileMap(Game game, Maps id) {
		this.game = game;
		this.id = id;
		load("/maps/" + id + ".map", Tools.ResourceLoader.LOAD_RESOURCE);
	}

	/**
	 * Constructs a blank map with the given size.
	 * 
	 * @param game   The instance of the game object
	 * @param width  The width of the new map
	 * @param height The height of the new map
	 */
	public TileMap(Game game, int width, int height) {
		this.numWide = width;
		this.numTall = height;
		this.numLayers = 1;
		this.id = null;

		tileData = new int[numLayers][height][width];
		solidData = new boolean[height][width];
		this.game = game;
	}

	/**
	 * Loads a map with the given path by the given method.
	 * 
	 * @param path The path of the map to be loaded
	 * @param type ResourceLoader.LOAD_FILE or ResourceLoader.LOAD_RESOURCE
	 */
	public void load(String path, int type) {
		// Start loading text file
		String text = Tools.ResourceLoader.loadTextFile(path, type);
		String[] tokens = text.split("\nBREAK\n");

		// Get important values
		String[] mapSize = tokens[0].split("\\s+");
		numWide = Integer.parseInt(mapSize[0]);
		numTall = Integer.parseInt(mapSize[1]);
		numLayers = Integer.parseInt(mapSize[2]);
		tileData = new int[numLayers][numTall][numWide];
		solidData = new boolean[numTall][numWide];

		// Load tile data
		for (int z = 0; z < numLayers; z++) {
			String[] lines = tokens[z + 1].split("\n");
			for (int y = 0; y < numTall; y++) {
				String[] data = lines[y].split("\\s+");
				for (int x = 0; x < numWide; x++) {
					tileData[z][y][x] = Integer.parseInt(data[x]);
				}
			}
		}

		// Load solid data
		String[] solidLines = tokens[numLayers + 1].split("\n");
		for (int y = 0; y < numTall; y++) {
			String[] solidDataTokens = solidLines[y].split("\\s+");
			for (int x = 0; x < numWide; x++) {
				int val = Integer.parseInt(solidDataTokens[x]);
				solidData[y][x] = (val == 0) ? false : true;
			}
		}

	}

	public void render(Graphics g, int px, int py) {
		for (int z = 0; z < numLayers; z++) {
			for (int y = 0; y < numTall; y++) {
				for (int x = 0; x < numWide; x++) {
					// If tileData is empty or off screen, don't render
					if (tileData[z][y][x] == -1) continue;
					if ((x + 1) * Tile.GAME_SIZE + px < 0 || x * Tile.GAME_SIZE + px > game.getWidth()) continue;
					if ((y + 1) * Tile.GAME_SIZE + py < 0 || y * Tile.GAME_SIZE + py > game.getHeight()) continue;

					// Render if on screen and not empty
					Tile.getTile(tileData[z][y][x]).render(g, x, y, px, py, Tile.GAME_SIZE);
				}
			}
		}
	}

	public boolean isSolid(int tx, int ty) { return solidData[ty][tx]; }

	/**
	 * Sets the given tile to be solid or not based on the given boolean.
	 * 
	 * @param tx The x coordinate of the tile to be changed
	 * @param ty The y coordinate of the tile to be changed
	 * @param b  True if the tile should be solid, false if not
	 * @return The map after having been changed.
	 */
	public TileMap setSolid(int tx, int ty, boolean b) {
		// Set flag and recollect colliders
		solidData[ty][tx] = b;
		return this;
	}

	/**
	 * Sets the data of the given tile to the given id.
	 * 
	 * @param tx The x coordinate of the tile to be changed
	 * @param ty The y coordinate of the tile to be changed
	 * @param id The new id that the tile should be
	 * @return The map after having been changed
	 */
	public TileMap setTile(int tx, int ty, int tz, int id) {
		if (id < AssetManager.getTileSprites().length && id >= 0) tileData[tz][ty][tx] = id;
		return this;
	}

	/**
	 * Returns the solid data of this tile map.
	 */
	public boolean[][] getSolidData() { return solidData; }

	public void tick(double deltaTime) {}

	/**
	 * Adds entities to the provided entityList.
	 */
	public void populateDynamics(ArrayList<Dynamic> entityList) {}

	/**
	 * Called when a player interacts with a target to see if the target has something to do with this quest.
	 * 
	 * @param target The entity that the player has interacted with
	 */
	public boolean onInteract(Entity target) { return false; }

	/**
	 * Returns the text that a particular NPC should have given parameters of the quest.
	 * 
	 * @param e The entity that dialog should be obtained for.
	 */
	public String getDialog(Entity e) { return "I AM ERROR"; }

	/**
	 * Resets the tilemap.
	 */
	public TileMap reset() { return this; }

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
