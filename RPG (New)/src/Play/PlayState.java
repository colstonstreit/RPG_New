package Play;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Comparator;

import Editor.EditorState;
import Engine.Game;
import Engine.State;
import Engine.Tools.Vec2;
import Engine.Tools.fRect;
import Play.Entity.Dynamic;
import Play.Quests.Quest;

public class PlayState extends State {

	public static TileMap map;
	public static String newMapName;

	public static Camera camera;
	public static Player player;

	public static ArrayList<Dynamic> entities = new ArrayList<Dynamic>();

	public static boolean drawHoveredTileCoords = false;

	public PlayState(Game game) {
		super(game);
		camera = new Camera(game, 0, 0);

		player = new Player(game, new Vec2(24.5, 32));
		entities.add(player);

		changeMap("Cool Island");

		camera.centerOnEntity(player, false);
	}

	public void tick(double deltaTime) {

		// Update commands
		TheaterEngine.tick(deltaTime);

		// Switch to editor if p is pressed
		if (game.keyUp('p')) game.changeState(Game.States.EDITOR);

		// Switch camera mode if f is pressed
		if (game.keyUp('f')) camera.centerOnEntity(player, !camera.smoothMovement);

		// Test all the commands if t is pressed
		if (game.keyUp('t') && !TheaterEngine.hasCommand()) {
			ArrayList<Command> commands = new ArrayList<Command>();
			commands.add(new Command.FadeOut(game, 1000, 1000, 2000, Color.black));
			commands.add(new Command.ShowDialog(game, "Hi!"));
			commands.add(new Command.Wait(game, 2000));
			commands.add(new Command.Move(game, player, new Vec2(1, 1), 1000, true));
			TheaterEngine.addGroup(commands, true);
		}

		// Zoom out/in if q/e are pressed, toggle hovered tile drawing
		if (game.keyUp('q')) Tile.GAME_SIZE -= 2;
		if (game.keyUp('e')) Tile.GAME_SIZE += 2;
		if (game.keyUp('o')) drawHoveredTileCoords = !drawHoveredTileCoords;

		// Update entities
		for (Dynamic e : entities)
			e.tick(deltaTime);

		// Update map
		map.tick(deltaTime);

		// Change map if necessary!
		if (newMapName != null) {
			changeMap(newMapName);
			newMapName = null;
		}

		// Update camera
		camera.tick(deltaTime);

		// Remove completed quests
		Quests.removeCompleted();

	}

	public void render(Graphics g) {

		// Render map
		map.render(g, camera.ox, camera.oy);

		// Sort entities by the y-value at their feet
		entities.sort(new Comparator<Dynamic>() {

			public int compare(Dynamic o1, Dynamic o2) {
				return (o1.pos.y + o1.size.y == o2.pos.y + o2.size.y) ? 0 : (o1.pos.y + o1.size.y > o2.pos.y + o2.size.y) ? 1 : -1;
			}

		});

		// Draw entities in the correct order
		for (Dynamic e : entities)
			e.render(g, camera.ox, camera.oy);

		// Draw a cyan transparent rectangle over the hovered tile, as well as a string containing the tile's coordinates for reference
		if (drawHoveredTileCoords) {
			fRect mousePos = game.mouseBounds();
			int tx = (int) (mousePos.x - camera.ox) / EditorState.tSize - ((mousePos.x - camera.ox < 0) ? 1 : 0);
			int ty = (int) (mousePos.y - camera.oy) / EditorState.tSize - ((mousePos.y - camera.oy < 0) ? 1 : 0);
			if (tx >= 0 && ty >= 0 && tx < map.numWide() && ty < map.numTall()) {
				new fRect(tx * Tile.GAME_SIZE + camera.ox, ty * Tile.GAME_SIZE + camera.oy, Tile.GAME_SIZE, Tile.GAME_SIZE).fill(g,
						new Color(0, 255, 255, 120));
				g.setColor(Color.white);
				g.setFont(new Font("Times New Roman", Font.BOLD, 36));
				g.drawString("<" + tx + "," + ty + ">", 10, 36);
			}
		}

		TheaterEngine.render(g, camera.ox, camera.oy);

	}

	/**
	 * Switches the map to the one with the name passed in, or does nothing if the requested map does not exist.
	 * 
	 * @param name The name of the requested map
	 */
	public static void changeMap(String name) {
		if (!Maps.mapList.containsKey(name)) {
			System.out.println("There is no map with the name: " + name + "!");
			return;
		} else if (map != null && name.equals(map.name)) return;

		entities.clear();
		entities.add(player);
		map = Maps.get(name);
		map.populateDynamics(entities);

		for (Quest q : Quests.currentQuestList) {
			q.populateDynamics(name, entities);
		}

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
