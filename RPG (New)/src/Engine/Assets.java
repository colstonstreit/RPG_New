package Engine;

public class Assets {

	private static Sprite tileSheet; // Spritesheet containing all the tiles
	private static Sprite[] tileImages; // List of tile images obtained from tileSheet

	public static void loadAssets() {
		tileSheet = new Sprite("/spritesheets/tileSheet.png", 16);

		tileImages = new Sprite[256];
		tileImages[0] = tileSheet.crop(0, 0, 1, 1); // grass
		tileImages[1] = tileSheet.crop(1, 0, 1, 1); // sand
		tileImages[2] = tileSheet.crop(2, 0, 1, 1); // brick
		tileImages[3] = tileSheet.crop(3, 0, 3, 1); // water (animated)
		tileImages[4] = tileSheet.crop(6, 0, 3, 1); // lava (animated)
		tileImages[5] = tileSheet.crop(9, 0, 1, 1); // tree
		tileImages[6] = tileSheet.crop(10, 0, 1, 1); // sun
		tileImages[7] = tileSheet.crop(11, 0, 1, 1); // flower

	}

	/**
	 * Returns the sprite corresponding to the given id, or the brick sprite if id is invalid.
	 * 
	 * @return Sprite with given id
	 */
	public static Sprite getTileSprite(int id) {
		if (id >= 0 && id < tileImages.length && tileImages[id] != null) return tileImages[id];
		else return tileImages[2]; // return brick by default to make it stand out
	}

	/**
	 * Returns an array of all the sprites contained within tileImages.
	 * 
	 * @return Sprite[] of all tileImages
	 */
	public static Sprite[] getTileSprites() { return tileImages; }

}
