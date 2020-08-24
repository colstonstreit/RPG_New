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

		private static NPC sparky, squirty, bulby;
		private static Teleport throughLava;

		public CoolIsland(Game game) {
			super(game, "Cool Island");
			Quests.setInitiator("PikachuRunToCorner", sparky = new NPC(game, "Sparky", "Pikachu", new Vec2(19, 29)));
			Quests.setInitiator("LavaMan", squirty = new NPC(game, "Squirty", "Squirtle", new Vec2(30, 29)));
			bulby = new NPC(game, "Bulby", "Bulbasaur", new Vec2(15, 15)).setText("Hi!", "You're awesome!", "I feel sad.",
					"There comes a time in a young boy's life where they realize that everything they thought they knew was wrong. "
							+ "Girls don't have cooties; they're actually quite likable. All those toys you played with when you were little "
							+ "suddenly seem to have no importance. \nIt's a true shame because it's sad to think that we all must lose the "
							+ "childhood innocence that the world once praised of us... Sigh. So how are you?");

			throughLava = (Teleport) new Teleport(game, true, "throughLava", new Vec2(11, 12), "Lol").setShouldBeDrawn(true).setCollisionType(true, true)
					.setTransform(20, 24, 10, 5);
		}

		public void populateDynamics(ArrayList<Dynamic> entities) {
			entities.add(sparky.setText(getDialog(sparky)));
			entities.add(squirty.setText(getDialog(squirty)));
			entities.add(bulby);
			entities.add(throughLava);
		}

		public String getDialog(Entity e) {
			if (e == sparky) {
				return !Quests.completedQuest("PikachuRunToCorner", false) ? "Hey, would you run to the top-left corner for me?"
						: "You helped me! Thank you so much.";
			} else if (e == squirty) {
				return !Quests.doingQuest("LavaMan") && !Quests.completedQuest("LavaMan", false)
						? "I've got a quest for you! Go talk to the man past the lava. He'll explain what you need to do."
						: "Have fun!";
			} else return super.getDialog(e);
		}

		public boolean onInteract(Entity target) {
			if (target == squirty) {
				Quests.addQuest("LavaMan", false);
				squirty.setText(getDialog(squirty));
				return true;
			} else if (target == sparky) {
				Quests.addQuest("PikachuRunToCorner", true);
				return true;
			}
			return false;
		}

	}

	//////////////////////////////////////////////////////////////////////////////////////////

	static class Lol extends TileMap {

		private static Teleport backToCoolIsland;

		public Lol(Game game) {
			super(game, "lol");
			backToCoolIsland = (Teleport) new Teleport(game, false, "RunAround", new Vec2(24.5, 32), "Cool Island").setShouldBeDrawn(true).setTransform(15, 15,
					2, 2);
		}

		public void populateDynamics(ArrayList<Dynamic> entities) { entities.add(backToCoolIsland); }

	}

}
