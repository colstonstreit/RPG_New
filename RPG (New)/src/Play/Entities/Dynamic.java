package Play.Entities;

import java.awt.Graphics;

import Engine.Game;
import Engine.Tools.Vec2;
import Engine.Tools.fRect;

public abstract class Dynamic extends Entity {

	public Vec2 v; // velocity vector
	public boolean solidVsDynamic; // whether this entity is solid against dynamic entities or not
	public boolean solidVsStatic; // whether this entity is solid against static world tiles or not

	protected fRect interactableRegion; // relative image coordinates of interact zone for onInteract() purposes

	/**
	 * @param game An instance of the game object
	 * @param type The type of entity (i.e. "Player" or "NPC")
	 */
	public Dynamic(Game game, String name) {
		super(game, name);
		v = new Vec2(0, 0);
		setCollisionType(false, false);
		interactableRegion = new fRect(0, 0, 1, 1);
	}

	public abstract void onInteract(Entity e);

	/**
	 * Returns an fRect containing the Dynamic's interact rectangle in world coordinates.
	 */
	public fRect interactableRegion() {
		return new fRect(pos.x + size.x * interactableRegion.x, pos.y + size.y * interactableRegion.y, size.x * interactableRegion.width,
				size.y * interactableRegion.height);
	}

	/**
	 * Sets the solidVsDynamic and solidVsStatic flags to those passed in, then returns the Dynamic.
	 * 
	 * @param solidVsStatic  Whether or not the Dynamic should be solid against the map
	 * @param solidVsDynamic Whether or not the Dynamic should be solid against other dynamics.
	 */
	public Dynamic setCollisionType(boolean solidVsStatic, boolean solidVsDynamic) {
		this.solidVsStatic = solidVsStatic;
		this.solidVsDynamic = solidVsDynamic;
		return this;
	}

	@Override
	public void tick(double deltaTime) {}

	@Override
	public void render(Graphics g, int ox, int oy) {}
}
