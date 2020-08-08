package Engine;

public class Assets {

	private static Sprite tileSheet;
	private static Sprite[] tileImages;

	public static void loadAssets() {
		tileSheet = new Sprite("/spritesheets/tileSheet.png", 16);

		tileImages = new Sprite[256];
		tileImages[0] = tileSheet.crop(0, 0, 1, 1); // grass
		tileImages[1] = tileSheet.crop(1, 0, 1, 1); // sand
		tileImages[2] = tileSheet.crop(2, 0, 1, 1); // brick
		tileImages[3] = tileSheet.crop(3, 0, 3, 1); // water (animated)
		tileImages[4] = tileSheet.crop(6, 0, 3, 1); // lava (animated)
		tileImages[5] = tileSheet.crop(9, 0, 1, 1); // tree

	}

	public static Sprite getTileSprite(int id) {
		if (id >= 0 && id < tileImages.length && tileImages[id] != null) return tileImages[id];
		else return tileImages[2]; // return brick to make it stand out
	}

	public static Sprite[] getTileSprites() { return tileImages; }

}
