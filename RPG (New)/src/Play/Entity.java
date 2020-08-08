package Play;

import java.awt.Graphics;

import Engine.Game;
import Engine.State;
import Engine.Tools.Vec2;

public abstract class Entity {

	protected Game game;

	public Vec2 pos;
	public Vec2 size;

	protected String type;

	public Entity(Game game, String type) {
		this.game = game;
		this.type = type;
	}

	public abstract void tick(double deltaTime);

	public abstract void render(Graphics g, int ox, int oy);

	protected State getState() { return game.currentState(); }

	public Vec2 getCenter() { return new Vec2(pos.x + 0.5 * size.x, pos.y + 0.5 * size.y); }

	//////////////////////////////////////////////////////////////////////////////////////////

	public static abstract class Dynamic extends Entity {

		public Vec2 v;
		public boolean solidVsDynamic;
		public boolean solidVsStatic;

		public Dynamic(Game game, String type) { 
			super(game, type); 
			v = new Vec2(0, 0);
		}

		public abstract void onInteract(Entity e);
	}

}
