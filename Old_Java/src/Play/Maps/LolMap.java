package Play.Maps;

import java.util.ArrayList;

import Engine.Game;
import Engine.Tools.Vec2;
import Play.Entities.Dynamic;
import Play.Entities.Teleport;
import Play.Maps.MapManager.Maps;

public class LolMap extends TileMap {

	private static Teleport backToCoolIsland;

	public LolMap(Game game) {
		super(game, Maps.LOL);
		backToCoolIsland = (Teleport) new Teleport(game, false, "RunAround", new Vec2(24.5, 32), Maps.COOL_ISLAND).setShouldBeDrawn(true).setTransform(15, 15,
				2, 2);
	}

	public void populateDynamics(ArrayList<Dynamic> entities) { entities.add(backToCoolIsland); }

}
