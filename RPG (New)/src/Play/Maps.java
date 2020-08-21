package Play;

import java.util.ArrayList;
import java.util.HashMap;

import Engine.Game;
import Engine.Tools.Vec2;
import Play.Entity.Dynamic;
import Play.Entity.Dynamic.Trigger.Teleport;

public class Maps {

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
		mapList.put("Cool Island", new CoolIsland(game));
		mapList.put("Lol", new Lol(game));
	}

	//////////////////////////////////////////////////////////////////////////////////////////

	static class CoolIsland extends TileMap {

		public CoolIsland(Game game) { super(game, "Cool Island"); }

		public void populateDynamics(ArrayList<Dynamic> entities) {
			entities.add(new NPC(game, "Sparky", "Pikachu", new Vec2(19, 29))
					.setText(!Quests.completedQuest("PikachuRunToCorner", false) ? "Hey, would you run to the top-left corner for me?"
							: "You helped me! Thank you so much."));
			entities.add(
					new NPC(game, "Squirty", "Squirtle", new Vec2(30, 29)).setText(
							!Quests.doingQuest("Test") && !Quests.completedQuest("Test", false)
									? "I've got a quest for you! Go talk to the man past the lava. He'll explain what you need to do."
									: "Have fun!"));
			entities.add((Teleport) new Teleport(game, true, "RunAround", new Vec2(11, 12), "Lol").setShouldBeDrawn(true).setCollisionType(true, true)
					.setTransform(20, 24, 10, 5));
		}

		public boolean onInteract(Entity target) {
			if (target.name.equals("Squirty")) {
				((NPC) target).setText("Have fun!");
				Quests.addQuest("Test");
				return true;
			}
			if (target.name.equals("Sparky")) {
				Quests.addQuest("PikachuRunToCorner");
				return true;
			}
			return false;
		}

	}

	//////////////////////////////////////////////////////////////////////////////////////////

	static class Lol extends TileMap {

		public Lol(Game game) { super(game, "lol"); }

		public void populateDynamics(ArrayList<Dynamic> entities) {
			entities.add(
					(Teleport) new Teleport(game, false, "RunAround", new Vec2(24.5, 32), "Cool Island").setShouldBeDrawn(true).setTransform(15, 15, 2, 2));
		}

	}

}
