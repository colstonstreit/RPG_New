package Play.Entities;

import java.awt.Graphics;

import Engine.Game;
import Engine.Tools.Vec2;
import Engine.Tools.fRect;
import Play.PlayState;

public abstract class Dynamic extends Entity {

	public Vec2 v; // velocity vector
	public boolean solidVsDynamic; // whether this entity is solid against dynamic entities or not
	public boolean solidVsStatic; // whether this entity is solid against static world tiles or not
	protected boolean moving; // Whether or not the dynamic is currently moving.

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
	 * Resolves collisions between this Dynamic and the surrounding map, as well as other Dynamic Entities within the game.
	 */
	protected void handleCollisions() {

		// Handle collisions in the x direction, static then dynamic
		if (v.x != 0) {
			pos.x += v.x;

			if (solidVsStatic) {
				fRect hitbox = hitbox();
				double hitboxLeftDistance = hitbox.x - pos.x;
				double hitboxRightDistance = hitbox.x + hitbox.width - pos.x;
				for (fRect r : PlayState.map.getColliders()) {
					if (hitbox.intersects(r)) {
						if (v.x > 0 && pos.x + hitboxRightDistance > r.x) pos.x = r.x - hitboxRightDistance;
						else if (v.x < 0 && pos.x + hitboxLeftDistance < r.x + 1) pos.x = r.x + 1 - hitboxLeftDistance;
					}
				}
			}

			if (solidVsDynamic) {
				for (Dynamic e : PlayState.entities) {
					if (e == this || !e.solidVsDynamic) continue;
					fRect hitbox = hitbox();
					double hitboxLeftDistance = hitbox.x - pos.x;
					double hitboxRightDistance = hitbox.x + hitbox.width - pos.x;

					fRect other;
					if (hitbox.intersects(other = e.hitbox())) {
						if (v.x > 0 && pos.x + hitboxRightDistance > other.x) pos.x = other.x - hitboxRightDistance;
						else if (v.x < 0 && pos.x + hitboxLeftDistance < other.x + other.width) pos.x = other.x + other.width - hitboxLeftDistance;
					}
				}
			}
		}

		// Handle collisions in the y direction, static then dynamic
		if (v.y != 0) {
			pos.y += v.y;
			if (solidVsStatic) {
				fRect hitbox = hitbox();
				double hitboxTopDistance = hitbox.y - pos.y;
				double hitboxBottomDistance = hitbox.y + hitbox.height - pos.y;
				for (fRect r : PlayState.map.getColliders()) {
					if (hitbox.intersects(r)) {
						if (v.y > 0 && pos.y + hitboxBottomDistance > r.y) pos.y = r.y - hitboxBottomDistance;
						else if (v.y < 0 && pos.y + hitboxTopDistance < r.y + 1) pos.y = r.y + 1 - hitboxTopDistance;
					}
				}
			}

			if (solidVsDynamic) {
				for (Dynamic e : PlayState.entities) {
					if (e == this || !e.solidVsDynamic) continue;
					fRect hitbox = hitbox();
					double hitboxTopDistance = hitbox.y - pos.y;
					double hitboxBottomDistance = hitbox.y + hitbox.height - pos.y;

					fRect other;
					if (hitbox.intersects(other = e.hitbox())) {
						if (v.y > 0 && pos.y + hitboxBottomDistance > other.y) pos.y = other.y - hitboxBottomDistance;
						else if (v.y < 0 && pos.y + hitboxTopDistance < other.y + other.height) pos.y = other.y + other.height - hitboxTopDistance;
					}
				}
			}
		}
	}

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

	/**
	 * Sets the Dynamic's velocity, then returns the Dynamic.
	 */
	public Dynamic setVel(double vx, double vy) {
		this.v = new Vec2(vx, vy);
		return this;
	}

	@Override
	public void tick(double deltaTime) {}

	@Override
	public void render(Graphics g, int ox, int oy) {}
}
