package Play;

import java.awt.Graphics;
import java.util.ArrayList;

import Engine.Assets;
import Engine.Game;
import Engine.Tools;
import Engine.Tools.Vec2;

public class TileMap {

	protected Game game;

	protected int[][][] tileData;
	protected boolean[][] solidData;

	private ArrayList<Vec2> colliders = new ArrayList<Vec2>();

	protected int numWide, numTall, numLayers;

	public TileMap(Game game, String path) {
		load(path, Tools.ResourceLoader.LOAD_RESOURCE);
		this.game = game;
	}

	public TileMap(Game game, int width, int height) {
		this.numWide = width;
		this.numTall = height;
		this.numLayers = 1;

		tileData = new int[numLayers][height][width];
		solidData = new boolean[height][width];
		this.game = game;
	}

	public void load(String path, int type) {
		String text = Tools.ResourceLoader.loadTextFile(path, type);
		String[] tokens = text.split("\nBREAK\n");

		String[] mapSize = tokens[0].split("\\s+");
		numWide = Integer.parseInt(mapSize[0]);
		numTall = Integer.parseInt(mapSize[1]);
		numLayers = Integer.parseInt(mapSize[2]);
		tileData = new int[numLayers][numTall][numWide];
		solidData = new boolean[numTall][numWide];

		for (int z = 0; z < numLayers; z++) {
			String[] lines = tokens[z + 1].split("\n");
			for (int y = 0; y < numTall; y++) {
				String[] data = lines[y].split("\\s+");
				for (int x = 0; x < numWide; x++) {
					tileData[z][y][x] = Integer.parseInt(data[x]);
				}
			}
		}

		String[] solidLines = tokens[numLayers + 1].split("\n");
		for (int y = 0; y < numTall; y++) {
			String[] solidDataTokens = solidLines[y].split("\\s+");
			for (int x = 0; x < numWide; x++) {
				int val = Integer.parseInt(solidDataTokens[x]);
				solidData[y][x] = (val == 0) ? false : true;
			}
		}

		collectSolids();
	}

	public void render(Graphics g, int px, int py) {
		for (int z = 0; z < numLayers; z++) {
			for (int y = 0; y < numTall; y++) {
				for (int x = 0; x < numWide; x++) {
					if (tileData[z][y][x] == -1) continue;
					if ((x + 1) * Tile.GAME_SIZE + px < 0 || x * Tile.GAME_SIZE + px > game.getWidth()) continue;
					if ((y + 1) * Tile.GAME_SIZE + py < 0 || y * Tile.GAME_SIZE + py > game.getHeight()) continue;
					Tile.getTile(tileData[z][y][x]).render(g, x, y, px, py, Tile.GAME_SIZE);
				}
			}
		}
	}

	public boolean isSolid(int tx, int ty) { return solidData[ty][tx]; }

	public TileMap setSolid(int tx, int ty, boolean b) {
		solidData[ty][tx] = b;
		collectSolids();

		return this;
	}

	public TileMap setTile(int tx, int ty, int tz, int id) {
		if (id < Assets.getTileSprites().length && id >= 0) tileData[tz][ty][tx] = id;
		return this;
	}

	public void collectSolids() {
		colliders.clear();
		for (int y = 0; y < numTall; y++) {
			for (int x = 0; x < numWide; x++) {
				if (solidData[y][x]) colliders.add(new Vec2(x, y));
			}
		}
	}

	public ArrayList<Vec2> getColliders() { return colliders; }

}
