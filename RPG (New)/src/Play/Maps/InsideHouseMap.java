package Play.Maps;

import java.util.ArrayList;

import Engine.Game;
import Engine.Tools.Vec2;
import Play.Entities.Dynamic;
import Play.Entities.Teleport;
import Play.Maps.MapManager.Maps;

public class InsideHouseMap extends TileMap {

	private static Teleport backToCoolIsland;

	public InsideHouseMap(Game game) {
		super(game, Maps.INSIDE_HOUSE);
		backToCoolIsland = (Teleport) new Teleport(game, false, "HouseExit", new Vec2(24, 10), Maps.COOL_ISLAND).setShouldBeDrawn(true).setTransform(4, 7, 2,
				1);
	}

	public void populateDynamics(ArrayList<Dynamic> entities) { entities.add(backToCoolIsland); }

}
