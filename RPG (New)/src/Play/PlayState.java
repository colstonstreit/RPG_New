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

	public PlayState(Game game) {
		super(game);
		map = new TileMap(game, "Cool Island");
		camera = new Camera(game, 0, 0);

		player = new Player(game, new Vec2(19, 23));
		entities.add(player);

		for (int y = 33; y <= 33; y++) {
			for (int x = 14; x <= 35; x++) {
				Dynamic t = new NPC(game, "Pikachu", new Vec2(x, y));
				t.v = new Vec2(0, 0);
				entities.add(t);
			}
		}

		entities.add(new NPC(game, "Bulbasaur", new Vec2(20, 20)));

		camera.centerOnEntity(player, false);
	}

	public void tick(double deltaTime) {
		if (game.keyUp('p')) game.changeState(Game.States.EDITOR);

		if (game.keyUp('f')) camera.centerOnEntity(player, !camera.smoothMovement);

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

	}

	@Override
	public Vec2 worldToScreen(Vec2 v) { return new Vec2(v.x * Tile.GAME_SIZE + camera.ox, v.y * Tile.GAME_SIZE + camera.oy); }

	@Override
	public Vec2 screenToWorld(Vec2 v) { return new Vec2((v.x - camera.ox) / Tile.GAME_SIZE, (v.y - camera.oy) / Tile.GAME_SIZE); }

	@Override
	public fRect worldToScreen(fRect r) {
		Vec2 topCorner = worldToScreen(new Vec2(r.x, r.y));
		Vec2 bottomCorner = worldToScreen(new Vec2(r.x + r.width, r.y + r.height));
		return new fRect(topCorner.x, topCorner.y, bottomCorner.x - topCorner.x, bottomCorner.y - topCorner.y);
	}

	@Override
	public fRect screenToWorld(fRect r) {
		Vec2 topCorner = screenToWorld(new Vec2(r.x, r.y));
		Vec2 bottomCorner = screenToWorld(new Vec2(r.x + r.width, r.y + r.height));
		return new fRect(topCorner.x, topCorner.y, bottomCorner.x - topCorner.x, bottomCorner.y - topCorner.y);
	}
}
