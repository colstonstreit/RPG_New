package Play.Entities;

import java.awt.Graphics;

import Engine.Game;
import Engine.State;
import Engine.Tools.Vec2;
import Engine.Tools.fRect;

public abstract class Entity {

	public static final boolean showHitboxes = false;

	protected final Game game; // instance of the game

	public Vec2 pos; // position on the screen (in world units)
	public Vec2 size; // size on the screen (in world units | 1 tile = 1 unit)
	protected fRect relativeHitbox; // relative hitbox based on screenSize

	public final String name; // name of entity (i.e. "Player")

	/**
	 * @param game An instance of the game object
	 * @param type The type of entity (i.e. "Player" or "NPC")
	 */
	public Entity(Game game, String name) {
		this.game = game;
		this.name = name;
		setPos(0, 0);
		setSize(1, 1);
		relativeHitbox = new fRect(0, 0, 1, 1);
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
	public Vec2 getCenter() { return new Vec2(pos.x + 0.5 * size.x, pos.y + 0.5 * size.y); }

	/**
	 * Returns true if this Entity is on the screen, and false if not.
	 */
	public boolean isOnScreen() {
		Vec2 screenPos = worldToScreen(pos);
		Vec2 screenCornerPos = worldToScreen(pos.add(size));
		return !(screenPos.x > game.getWidth() || screenPos.y > game.getHeight() || screenCornerPos.x < 0 || screenCornerPos.y < 0);
	}

	/**
	 * Returns an fRect containing the Entity's hitbox in world coordinates rounded to the nearest millionth.
	 */
	public fRect hitbox() {
		return Game.round(
				new fRect(pos.x + size.x * relativeHitbox.x, pos.y + size.y * relativeHitbox.y, size.x * relativeHitbox.width, size.y * relativeHitbox.height),
				0.000001);
	}

	/**
	 * Sets the size of the entity to the width and height provided, then returns the entity.
	 */
	public Entity setSize(double width, double height) {
		size = new Vec2(width, height);
		return this;
	}

	/**
	 * Sets the position of the entity to the x and y provided, then returns the entity.
	 */
	public Entity setPos(double x, double y) {
		pos = new Vec2(x, y);
		return this;
	}

	/**
	 * Sets both the position and size of the entity to the x, y, width, and height provided, then returns the entity.
	 */
	public Entity setTransform(double x, double y, double width, double height) { return setPos(x, y).setSize(width, height); }

	/**
	 * Converts a vector from world coordinates to screen coordinates, and returns the converted vector.
	 */
	public Vec2 worldToScreen(Vec2 v) { return getState().worldToScreen(v); }

	/**
	 * Converts a vector from screen coordinates to world coordinates, and returns the converted vector.
	 */
	public Vec2 screenToWorld(Vec2 v) { return getState().screenToWorld(v); }

	/**
	 * Converts an fRect from world coordinates to screen coordinates, and returns the converted fRect.
	 */
	public fRect worldToScreen(fRect r) { return getState().worldToScreen(r); }

	/**
	 * Converts an fRect from screen coordinates to world coordinates, and returns the converted fRect.
	 */
	public fRect screenToWorld(fRect r) { return getState().screenToWorld(r); }

}
