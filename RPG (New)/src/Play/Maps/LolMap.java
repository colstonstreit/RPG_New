package Play.Maps;

import java.util.ArrayList;

import Engine.Game;
import Engine.Tools.Vec2;
import Play.Entities.Dynamic;
import Play.Entities.Teleport;

public class LolMap extends TileMap {

	private static Teleport backToCoolIsland;

	public LolMap(Game game) {
		super(game, "lol");
		backToCoolIsland = (Teleport) new Teleport(game, false, "RunAround", new Vec2(24.5, 32), "Cool Island").setShouldBeDrawn(true).setTransform(15, 15, 2,
				2);
	}

	public void populateDynamics(ArrayList<Dynamic> entities) { entities.add(backToCoolIsland); }

}
