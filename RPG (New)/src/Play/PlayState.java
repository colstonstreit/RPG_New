package Play;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Comparator;

import Engine.Game;
import Engine.State;
import Engine.Tools.Vec2;
import Engine.Tools.fRect;
import Play.Entity.Dynamic;

public class PlayState extends State {

	public static TileMap map;
	public static Camera camera;
	public static Player player;

	public static ArrayList<Dynamic> entities = new ArrayList<Dynamic>();

	private ArrayList<Dynamic> testPokemon = new ArrayList<Dynamic>();
	private int i = 0;

	public PlayState(Game game) {
		super(game);
		map = new TileMap(game, "Cool Island");
		camera = new Camera(game, 0, 0);

		player = new Player(game, new Vec2(12, 17));
		entities.add(player);
		testPokemon.add(player);

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

		camera.centerOnEntity(player, false);
	}

	public void tick(double deltaTime) {

		TheaterEngine.tick(deltaTime);

		if (game.keyUp('p')) game.changeState(Game.States.EDITOR);

		if (game.keyUp('f')) camera.centerOnEntity(player, !camera.smoothMovement);

		if (game.keyUp('g') && !TheaterEngine.hasCommand()) {
			ArrayList<Command> commands = new ArrayList<Command>();
			commands.add(new Command.Move(game, testPokemon.get(i % testPokemon.size()), new Vec2(13, 18), 1000, false));
			commands.add(new Command.Move(game, testPokemon.get((i + 1) % testPokemon.size()), new Vec2(36, 18), 1000, false));
			commands.add(new Command.Move(game, testPokemon.get((i + 2) % testPokemon.size()), new Vec2(36, 34), 1000, false));
			commands.add(new Command.Move(game, testPokemon.get((i + 3) % testPokemon.size()), new Vec2(13, 34), 1000, false));
			TheaterEngine.addGroup(commands, false);
			i = (i + 1) % testPokemon.size();
		}

		if (game.keyUp('q')) Tile.GAME_SIZE -= 2;
		if (game.keyUp('e')) Tile.GAME_SIZE += 2;

		for (Dynamic e : entities)
			e.tick(deltaTime);

		camera.tick(deltaTime);

	}

	public void render(Graphics g) {

		// Render map
		map.render(g, camera.ox, camera.oy);

		// Sort entities by the y-value at their feet
		entities.sort(new Comparator<Dynamic>() {

			public int compare(Dynamic o1, Dynamic o2) {
				return (o1.pos.y + o1.screenSize.y == o2.pos.y + o2.screenSize.y) ? 0 : (o1.pos.y + o1.screenSize.y > o2.pos.y + o2.screenSize.y) ? 1 : -1;
			}

		});

		// Draw entities in the correct order
		for (Dynamic e : entities)
			e.render(g, camera.ox, camera.oy);

		TheaterEngine.render(g, camera.ox, camera.oy);

	}

	public Vec2 worldToScreen(Vec2 v) { return new Vec2(v.x * Tile.GAME_SIZE + camera.ox, v.y * Tile.GAME_SIZE + camera.oy); }

	public Vec2 screenToWorld(Vec2 v) { return new Vec2((v.x - camera.ox) / Tile.GAME_SIZE, (v.y - camera.oy) / Tile.GAME_SIZE); }

	public fRect worldToScreen(fRect r) {
		Vec2 topCorner = worldToScreen(new Vec2(r.x, r.y));
		Vec2 bottomCorner = worldToScreen(new Vec2(r.x + r.width, r.y + r.height));
		return new fRect(topCorner.x, topCorner.y, bottomCorner.x - topCorner.x, bottomCorner.y - topCorner.y);
	}

	public fRect screenToWorld(fRect r) {
		Vec2 topCorner = screenToWorld(new Vec2(r.x, r.y));
		Vec2 bottomCorner = screenToWorld(new Vec2(r.x + r.width, r.y + r.height));
		return new fRect(topCorner.x, topCorner.y, bottomCorner.x - topCorner.x, bottomCorner.y - topCorner.y);
	}
}
