package Play;

import java.awt.Graphics;

import Engine.Assets;
import Engine.Sprite;

@SuppressWarnings("unused")
public class Tile {

	public static final int GAME_SIZE = 32; // the size that tiles should be rendered in game mode

	private static Tile[] tiles = new Tile[256]; // list of all tiles
	private static final Tile grassTile = new Tile(0);
	private static final Tile sandTile = new Tile(1);
	private static final Tile brickTile = new Tile(2);
	private static final Tile waterTile = new Tile.Animated(3, new int[][] { { 0 , 0 } , { 1 , 0 } , { 2 , 0 } , { 1 , 0 } }, 750);
	private static final Tile lavaTile = new Tile.Animated(4, new int[][] { { 0 , 0 } , { 1 , 0 } , { 2 , 0 } , { 1 , 0 } }, 750);
	private static final Tile treeTile = new Tile(5);
	private static final Tile sunTile = new Tile(6);
	private static final Tile flowerTile = new Tile(7);

	protected int id; // id of the tile
	protected Sprite sprite; // the sprite of the tile (from Assets)

	public Tile(int id) {
		this.id = id;
		sprite = Assets.getTileSprite(id);
		tiles[id] = this;
	}

	protected void tick() {}

	public void render(Graphics g, int tx, int ty, int ox, int oy, int size) { g.drawImage(sprite.image(), tx * size + ox, ty * size + oy, size, size, null); }

	public static Tile getTile(int id) {
		if (id >= 0 && id < tiles.length && tiles[id] != null) return tiles[id];
		else return tiles[2];
	}

	public static void tickTiles() {
		for (int i = 0; i < tiles.length; i++) {
			if (tiles[i] == null) break;
			tiles[i].tick();
		}
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////

	static class Animated extends Tile {

		private long lastTime = System.currentTimeMillis(); // the last time the animation was updated
		private int msDelay; // the delay between each frame
		private Sprite[] frames; // an array of images for the animation
		private int currentFrame = 0; // index of currentFrame

		public Animated(int id, int[][] animFrames, int msDelay) {
			super(id);
			frames = new Sprite[animFrames.length];
			for (int i = 0; i < animFrames.length; i++) {
				frames[i] = sprite.crop(animFrames[i][0], animFrames[i][1], 1, 1);
			}
			this.msDelay = msDelay;
		}

		protected void tick() {
			if (System.currentTimeMillis() - lastTime >= msDelay) {
				lastTime = System.currentTimeMillis();
				currentFrame = (currentFrame + 1) % frames.length;
			}
		}

		public void render(Graphics g, int tx, int ty, int ox, int oy, int size) {
			g.drawImage(frames[currentFrame].image(), tx * size + ox, ty * size + oy, size, size, null);
		}

	}

}
