package Play.Maps;

import java.util.HashMap;

import Engine.Game;

public class MapManager {

	public static HashMap<String, TileMap> mapList = new HashMap<String, TileMap>(); // hashMap of maps

	/**
	 * Returns a map requested by the given name.
	 * 
	 * @param name The name of the requested map
	 */
	public static TileMap get(String name) {
		if (mapList.containsKey(name)) return mapList.get(name).reset();
		System.out.println("No map with name: " + name + " exists!");
		return null;
	}

	/**
	 * Loads each of the maps in the game.
	 */
	public static void loadMaps(Game game) {
		mapList.put("Cool Island", new CoolIslandMap(game));
		mapList.put("Lol", new LolMap(game));
	}

}
