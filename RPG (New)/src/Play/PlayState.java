package Play;

import java.awt.Graphics;

import Engine.Game;
import Engine.State;
import Engine.Tools.Vec2;

public class PlayState extends State{

	public static TileMap map;
	public static Camera camera;
	public static Player player;

	public PlayState(Game game) { 
		super(game); 
		map = new TileMap(game, "/maps/lol.map");
		camera = new Camera(game, 0, 0);
		player = new Player(game, 1, 1);
	}

	public void tick(double deltaTime) { 
		if (game.keyUp('p')) game.changeState(Game.States.EDITOR);
		
		if(game.keyUp('f')) camera.centerOnEntity(player);
		
		player.tick(deltaTime);
		camera.tick(deltaTime);
	}

	public void render(Graphics g) {
		map.render(g, camera.ox, camera.oy);
		player.render(g, camera.ox, camera.oy);
	}

	@Override
	public Vec2 worldToScreen(Vec2 v) { return new Vec2(v.x * Tile.GAME_SIZE + camera.ox, v.y * Tile.GAME_SIZE + camera.oy); }

	@Override
	public Vec2 screenToWorld(Vec2 v) { return new Vec2((v.x - camera.ox) / Tile.GAME_SIZE, (v.y - camera.oy) / Tile.GAME_SIZE); }
}
