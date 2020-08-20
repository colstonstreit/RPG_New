package Play;

import java.util.ArrayList;
import java.util.HashMap;

import Engine.Game;
import Engine.Tools.Vec2;
import Play.Entity.Dynamic;
import Play.Entity.Dynamic.Trigger.Teleport;

public class Maps {

	public static HashMap<String, TileMap> mapList = new HashMap<String, TileMap>();

	public static void loadMaps(Game game) {
		mapList.put("Cool Island", new CoolIsland(game));
		mapList.put("Lol", new Lol(game));
	}

	//////////////////////////////////////////////////////////////////////////////////////////

	static class CoolIsland extends TileMap {

		private ArrayList<Dynamic> testPokemon = new ArrayList<Dynamic>();
		private int i = 0;

		public CoolIsland(Game game) { super(game, "Cool Island"); }

		public void populateDynamics(ArrayList<Dynamic> entities) {
			Dynamic t = new NPC(game, "Bulbasaur", new Vec2(20, 20));
			t.v = new Vec2(0.01, 0);
			entities.add(t);
			testPokemon.add(t);

			Dynamic t2 = new NPC(game, "Pikachu", new Vec2(22, 20));
			t2.v = new Vec2(-0.01, 0);
			entities.add(t2);
			testPokemon.add(t2);

			Dynamic t3 = new NPC(game, "Player", new Vec2(21, 19));
			t3.v = new Vec2(0, 0.01);
			entities.add(t3);
			testPokemon.add(t3);

			testPokemon.add(PlayState.player);

			entities.add((Teleport) new Teleport(game, true, "RunAround", new Vec2(11, 12), "Lol").setShouldBeDrawn(true).setCollisionType(true, true)
					.setTransform(15, 15, 2, 2));
		}

		public void tick(double deltaTime) {
			// Do fun pokemon spin thing
			if (game.keyUp('g') && !TheaterEngine.hasCommand()) {
				ArrayList<Command> commands = new ArrayList<Command>();
				commands.add(new Command.Move(game, testPokemon.get(i % testPokemon.size()), new Vec2(13, 18), 1000, false));
				commands.add(new Command.Move(game, testPokemon.get((i + 1) % testPokemon.size()), new Vec2(36, 18), 1000, false));
				commands.add(new Command.Move(game, testPokemon.get((i + 2) % testPokemon.size()), new Vec2(36, 34), 1000, false));
				commands.add(new Command.Move(game, testPokemon.get((i + 3) % testPokemon.size()), new Vec2(13, 34), 1000, false));
				TheaterEngine.addGroup(commands, false);
				i = (i + 1) % testPokemon.size();
			}
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
