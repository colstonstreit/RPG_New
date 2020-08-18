package Play;

import java.awt.Graphics;

import Engine.Game;
import Engine.State;
import Engine.Tools.Vec2;
import Engine.Tools.fRect;

public abstract class Entity {

	protected Game game; // instance of the game

	public Vec2 pos; // position on the screen (in world units)
	public Vec2 screenSize; // size on the screen (in world units | 1 tile = 1 unit)
	public fRect relativeHitbox; // relative hitbox based on screenSize

	protected String type; // type of entity (i.e. "Player")

	/**
	 * @param game An instance of the game object
	 * @param type The type of entity (i.e. "Player" or "NPC")
	 */
	public Entity(Game game, String type) {
		this.game = game;
		this.type = type;
	}

	public abstract void tick(double deltaTime);

	public abstract void render(Graphics g, int ox, int oy);

	/**
	 * Returns the game's current State.
	 */
	protected State getState() { return game.currentState(); }

	/**
	 * Returns a Vec2 representing the center of the entity in world space
	 */
	public Vec2 getCenter() { return new Vec2(pos.x + 0.5 * screenSize.x, pos.y + 0.5 * screenSize.y); }

	//////////////////////////////////////////////////////////////////////////////////////////

	public static abstract class Dynamic extends Entity {

		public Vec2 v; // velocity vector
		public boolean solidVsDynamic; // whether this entity is solid against dynamic entities or not
		public boolean solidVsStatic; // whether this entity is solid against static world tiles or not

		/**
		 * @param game An instance of the game object
		 * @param type The type of entity (i.e. "Player" or "NPC")
		 */
		public Dynamic(Game game, String type) {
			super(game, type);
			v = new Vec2(0, 0);
		}

		public abstract void onInteract(Entity e);
	}

}
