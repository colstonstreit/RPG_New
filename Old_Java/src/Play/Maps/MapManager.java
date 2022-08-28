package Play.Maps;

import java.util.HashMap;

import Engine.Game;

public class MapManager {

	public static enum Maps { LOL, COOL_ISLAND, INSIDE_HOUSE }

	public static HashMap<Maps, TileMap> mapList = new HashMap<Maps, TileMap>(); // hashMap of maps

	/**
	 * Returns a map requested by the given name.
	 * 
	 * @param name The name of the requested map
	 */
	public static TileMap get(Maps name) {
		if (mapList.containsKey(name)) return mapList.get(name).reset();
		System.out.println("No map with name: " + name + " exists!");
		return null;
	}

	/**
	 * Loads each of the maps in the game.
	 */
	public static void loadMaps(Game game) {
		mapList.put(Maps.COOL_ISLAND, new CoolIslandMap(game));
		mapList.put(Maps.LOL, new LolMap(game));
		mapList.put(Maps.INSIDE_HOUSE, new InsideHouseMap(game));
	}

}
