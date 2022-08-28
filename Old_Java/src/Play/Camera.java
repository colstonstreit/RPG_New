package Play;

import Engine.Game;
import Engine.State;
import Engine.Tools.Vec2;
import Play.Entities.Entity;
import Play.Maps.Tile;

public class Camera {

	private Game game; // An instance of the game object
	public Entity e; // The entity to be followed
	public int ox, oy; // The x and y camera offsets

	public boolean smoothMovement; // Whether or not the camera should move smoothly
	private static final int cameraInertia = 10; // inertia of camera movement speed

	/**
	 * @param game The instance of the game object
	 * @param ox   The initial x offset
	 * @param oy   The initial y offset
	 */
	public Camera(Game game, int ox, int oy) {
		this.game = game;
		this.ox = ox;
		this.oy = oy;
	}

	/**
	 * Returns the game's current state.
	 */
	public State getState() { return game.currentState(); }

	/**
	 * Sets the camera to follow an entity.
	 * 
	 * @param toFollow       The entity that the camera should follow
	 * @param smoothMovement True if smooth movement is desired, false if not.
	 */
	public void centerOnEntity(Entity toFollow, boolean smoothMovement) {
		e = toFollow;
		this.smoothMovement = smoothMovement;
	}

	public void tick(double deltaTime) {
		if (e != null) {
			// Only update if there's an entity to follow and then find ideal offsets
			Vec2 screenPos = e.getCenter().scale(Tile.GAME_SIZE);
			double idealOX = game.getWidth() / 2 - screenPos.x;
			double idealOY = game.getHeight() / 2 - screenPos.y;

			double diffX = idealOX - ox;
			double diffY = idealOY - oy;

			if (smoothMovement) {
				// Use mathematical algorithm to convey smooth panning motion only if desired
				if (Math.abs(diffX) >= 0.5) ox += (int) (diffX * Math.exp(1 / diffX) / cameraInertia);
				if (Math.abs(diffY) >= 0.5) oy += (int) (diffY * Math.exp(1 / diffY) / cameraInertia);
				if (new Vec2(diffX, diffY).getMagnitude() <= 4) {
					ox = (int) idealOX;
					oy = (int) idealOY;
				}
			} else {
				// Snap to correct coordinates if smoothness is not desired.
				ox = (int) idealOX;
				oy = (int) idealOY;
			}
		}
	}

}
