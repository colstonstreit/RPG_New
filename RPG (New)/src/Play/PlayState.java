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
import Play.Entities.Creature.Facing;
import Play.Entities.Dynamic;
import Play.Entities.Player;
import Play.Maps.MapManager;
import Play.Maps.Tile;
import Play.Maps.TileMap;
import Play.Quests.BaseQuest;
import Play.Quests.QuestManager;
import Play.TheaterEngine.BaseCommand;
import Play.TheaterEngine.FadeOutCommand;
import Play.TheaterEngine.MoveCommand;
import Play.TheaterEngine.ShowDialogCommand;
import Play.TheaterEngine.TheaterEngine;
import Play.TheaterEngine.TurnCommand;
import Play.TheaterEngine.WaitCommand;

public class PlayState extends State {

	public static TileMap map;
	public static String newMapName;
	public static boolean mustResetEntities = false;

	public static Camera camera;
	public static Player player;

	public static ArrayList<Dynamic> entities = new ArrayList<Dynamic>();

	public static boolean drawHoveredTileCoords = false;

	public PlayState(Game game) {
		super(game);
		camera = new Camera(game, 0, 0);

		player = new Player(game, new Vec2(24.5, 32));
		entities.add(player);

		changeMap("Cool Island", false);

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
			ArrayList<BaseCommand> commands = new ArrayList<BaseCommand>();
			commands.add(new FadeOutCommand(game, 1000, 1000, 2000, Color.black, null));
			commands.add(new ShowDialogCommand(game, "Hi!"));
			commands.add(new WaitCommand(game, 2000));
			commands.add(new MoveCommand(game, player, new Vec2(1, 1), 1000, true));
			commands.add(new TurnCommand(game, player, Facing.Up));
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
			changeMap(newMapName, false);
			newMapName = null;
		} else if (mustResetEntities) {
			changeMap(map.name, true);
			mustResetEntities = false;
		}

		// Update camera
		camera.tick(deltaTime);

		// Remove completed quests
		QuestManager.removeCompleted();

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
	 * @param name             The name of the requested map
	 * @param refreshIfSameMap True if the entities should be refreshed even if the map itself isn't actually changing
	 */
	public static void changeMap(String name, boolean refreshIfSameMap) {
		if (!MapManager.mapList.containsKey(name)) {
			System.out.println("There is no map with the name: " + name + "!");
			return;
		} else if (map != null && name.equals(map.name) && !refreshIfSameMap) return;

		entities.clear();
		entities.add(player);
		map = MapManager.get(name);
		map.populateDynamics(entities);

		for (BaseQuest q : QuestManager.currentQuestList) {
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
