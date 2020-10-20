package Engine;

import java.util.HashMap;

import Play.Entities.Items.ItemManager.Items;

public class AssetManager {

	private static Sprite[] tileImages; // List of tile images obtained from tileSheet

	public static enum CharacterSprites { PLAYER, BULBASAUR, PIKACHU, SQUIRTLE }
	private static HashMap<CharacterSprites, Sprite> characterSprites; // Hashmap of character images
	private static HashMap<Items, Sprite> itemSprites; // Hashmap of character images

	public static void loadAssets() {
		// Load tile images
		Sprite tileSheet = new Sprite("/spritesheets/tileSheet.png", 16);
		tileImages = new Sprite[256];
		tileImages[0] = tileSheet.crop(0, 0, 1, 1); // grass
		tileImages[1] = tileSheet.crop(1, 0, 1, 1); // sand
		tileImages[2] = tileSheet.crop(2, 0, 1, 1); // brick
		tileImages[3] = tileSheet.crop(3, 0, 3, 1); // water (animated)
		tileImages[4] = tileSheet.crop(6, 0, 3, 1); // lava (animated)
		tileImages[5] = tileSheet.crop(9, 0, 1, 1); // tree
		tileImages[6] = tileSheet.crop(10, 0, 1, 1); // sun
		tileImages[7] = tileSheet.crop(11, 0, 1, 1); // flower
		tileImages[8] = tileSheet.crop(12, 0, 1, 1); // house door
		tileImages[9] = tileSheet.crop(13, 0, 1, 1); // house window
		tileImages[10] = tileSheet.crop(14, 0, 1, 1); // house wall without window
		tileImages[11] = tileSheet.crop(12, 1, 1, 1); // upper-left blue house roof
		tileImages[12] = tileSheet.crop(13, 1, 1, 1); // upper-middle blue house roof
		tileImages[13] = tileSheet.crop(14, 1, 1, 1); // upper-right blue house roof
		tileImages[14] = tileSheet.crop(12, 2, 1, 1); // lower-left blue house roof
		tileImages[15] = tileSheet.crop(13, 2, 1, 1); // lower-middle blue house roof
		tileImages[16] = tileSheet.crop(14, 2, 1, 1); // lower-right blue house roof
		tileImages[17] = tileSheet.crop(15, 0, 1, 1); // wooden floorboards
		tileImages[18] = tileSheet.crop(15, 1, 1, 1); // stone brick (for floor?)

		// Load character images
		Sprite characterSheet = new Sprite("/spritesheets/characterSheet.png", 16);
		characterSprites = new HashMap<CharacterSprites, Sprite>();
		characterSprites.put(CharacterSprites.PLAYER, characterSheet.crop(0, 0, 4, 3));
		characterSprites.put(CharacterSprites.BULBASAUR, characterSheet.crop(4, 0, 4, 3));
		characterSprites.put(CharacterSprites.PIKACHU, characterSheet.crop(8, 0, 4, 3));
		characterSprites.put(CharacterSprites.SQUIRTLE, characterSheet.crop(12, 0, 4, 3));

		// Load item icons
		Sprite itemSheet = new Sprite("/spritesheets/itemSheet.png", 16);
		itemSprites = new HashMap<Items, Sprite>();
		itemSprites.put(Items.APPLE, itemSheet.crop(0, 0, 1, 1));
		itemSprites.put(Items.ORANGE, itemSheet.crop(1, 0, 1, 1));

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

	/**
	 * Returns the character spritesheet that is referenced by the given key, or returns null if the key is not in the list.
	 * 
	 * @param key The key corresponding to the desired character sprite
	 * @return Sprite The corresponding character spritesheet
	 */
	public static Sprite getCharacterSpriteSheet(CharacterSprites key) {
		if (!characterSprites.containsKey(key)) {
			System.out.println("Hashmap CharacterSprites does not have sheet with key: " + key + ".");
			return null;
		}
		return characterSprites.get(key);
	}

	/**
	 * Returns the item icon for the given itemID, or null if the list does not contain the given itemID.
	 * 
	 * @param itemID The ID of the item (from Items enum)
	 * @return Sprite The requested item icon
	 */
	public static Sprite getItemSprite(Items itemID) {
		if (!itemSprites.containsKey(itemID)) {
			System.out.println("Hashmap ItemSprites does not have an item icon for key: " + itemID + ".");
			return null;
		}
		return itemSprites.get(itemID);
	}

}
