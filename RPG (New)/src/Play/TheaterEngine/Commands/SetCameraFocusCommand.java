package Play.TheaterEngine.Commands;

import Engine.Game;
import Play.PlayState;
import Play.Entities.Entity;
import Play.Maps.Tile;

public class SetCameraFocusCommand extends PanCameraCommand {

	private Entity entityToSet; // The entity to set for the camera to focus on
	private boolean smoothMovement; // Whether or not the camera should snap to the new correct position
	private boolean isPanning = false; // Whether or not the camera will first pan to the entity.

	/**
	 * @param game           The game instance
	 * @param entity         The entity that the camera should follow. Can be null if it shouldn't follow anything
	 * @param smoothMovement True if the camera should move smoothly rather than snap to the correct position.
	 */
	public SetCameraFocusCommand(Game game, Entity entity, boolean smoothMovement) {
		super(game, 0, 0, 0);
		entityToSet = entity;
		this.smoothMovement = smoothMovement;
	}

	/**
	 * @param game           The game instance
	 * @param entity         The entity that the camera should follow. Can be null if it shouldn't follow anything
	 * @param smoothMovement True if the camera should move smoothly rather than snap to the correct position.
	 */
	public SetCameraFocusCommand(Game game, Entity entity, boolean smoothMovement, int timeToTake) {
		super(game, entity.getCenter().x, entity.getCenter().y, timeToTake);
		entityToSet = entity;
		this.smoothMovement = smoothMovement;
		isPanning = true;
	}

	public void start() {
		if (isPanning) {
			super.start();
			this.targetOX = (int) (-1 * (entityToSet.getCenter().x * Tile.GAME_SIZE - game.getWidth() / 2));
			this.targetOY = (int) (-1 * (entityToSet.getCenter().y * Tile.GAME_SIZE - game.getHeight() / 2));
		} else {
			PlayState.camera.centerOnEntity(entityToSet, smoothMovement);
			complete();
		}
	}

	public void tick(double deltaTime) {
		if (!isPanning) {
			if (!hasStarted) {
				start();
				hasStarted = true;
			}
		} else super.tick(deltaTime);
	}

	public void complete() {
		PlayState.camera.centerOnEntity(entityToSet, smoothMovement);
		super.complete();
	}

}
