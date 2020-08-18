package Engine;

import java.awt.Graphics;

public abstract class State {

	protected Game game; // An instance of the game object

	/**
	 * @param game The instance of the game object
	 */
	public State(Game game) { this.game = game; }

	public abstract void tick(double deltaTime);

	public abstract void render(Graphics g);

	/**
	 * Converts a vector from world coordinates to screen coordinates, and returns the converted vector.
	 */
	public abstract Tools.Vec2 worldToScreen(Tools.Vec2 v);

	/**
	 * Converts a vector from screen coordinates to world coordinates, and returns the converted vector.
	 */
	public abstract Tools.Vec2 screenToWorld(Tools.Vec2 v);

	/**
	 * Converts an fRect from world coordinates to screen coordinates, and returns the converted fRect.
	 */
	public abstract Tools.fRect worldToScreen(Tools.fRect r);

	/**
	 * Converts an fRect from screen coordinates to world coordinates, and returns the converted fRect.
	 */
	public abstract Tools.fRect screenToWorld(Tools.fRect r);

}
