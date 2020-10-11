package Play.TheaterEngine;

import Engine.Game;
import Play.PlayState;
import Play.Entities.Entity;

public class SetCameraFocusCommand extends BaseCommand {

	private Entity entityToSet; // The entity to set for the camera to focus on
	private boolean smoothMovement; // Whether or not the camera should snap to the new correct position

	/**
	 * @param game           The game instance
	 * @param entity         The entity that the camera should follow. Can be null if it shouldn't follow anything
	 * @param smoothMovement True if the camera should move smoothly rather than snap to the correct position.
	 */
	public SetCameraFocusCommand(Game game, Entity entity, boolean smoothMovement) {
		super(game);
		entityToSet = entity;
		this.smoothMovement = smoothMovement;
	}

	public void start() {
		PlayState.camera.centerOnEntity(entityToSet, smoothMovement);
		complete();
	}

}
