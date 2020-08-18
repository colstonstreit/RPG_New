package Play;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Comparator;

import Engine.Game;
import Engine.State;
import Engine.Tools.Vec2;
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
		
		for(int y = -18; y < 18; y++) {
			for(int x = 0; x < 50; x++) {
				Dynamic t = new NPC(game, "Bulbasaur", new Vec2(x, y));
				t.v = new Vec2(0.1, 0.1);
				entities.add(t);
			}
		}

		System.out.println(entities.size());
		
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

		// Sort entities by the y-value at their feet and then render them in the correct order
		entities.sort(new Comparator<Dynamic>() {

			public int compare(Dynamic o1, Dynamic o2) { 
				return (o1.pos.y + o1.screenSize.y == o2.pos.y + o2.screenSize.y) ? 0 : (o1.pos.y + o1.screenSize.y > o2.pos.y + o2.screenSize.y) ? 1 : -1 ;
			}
			
		});
		
		for (Dynamic e : entities)
			e.render(g, camera.ox, camera.oy);
		
	}

	@Override
	public Vec2 worldToScreen(Vec2 v) { return new Vec2(v.x * Tile.GAME_SIZE + camera.ox, v.y * Tile.GAME_SIZE + camera.oy); }

	@Override
	public Vec2 screenToWorld(Vec2 v) { return new Vec2((v.x - camera.ox) / Tile.GAME_SIZE, (v.y - camera.oy) / Tile.GAME_SIZE); }
}
