package Play.TheaterEngine.Commands;

import Engine.Game;
import Play.PlayState;
import Play.Maps.Tile;

public class PanCameraCommand extends BaseCommand {

	protected int targetOX, targetOY; // the target offsets to be reached
	protected int initialOX, initialOY; // initial camera offsets

	private final int timeToTake; // how long to take to pan over in milliseconds
	private int timeTaken = 0; // the amount of time passed so far in milliseconds

	/**
	 * @param game       The game instance
	 * @param targetX    The target x position for the camera to center around (in world coordinates)
	 * @param targetY    The target y position for the camera to center around (in world coordinates)
	 * @param timeToTake The number of milliseconds the panning should take place during
	 */
	public PanCameraCommand(Game game, double targetX, double targetY, int timeToTake) {
		super(game);
		targetX *= Tile.GAME_SIZE;
		targetY *= Tile.GAME_SIZE;
		this.targetOX = (int) (-1 * (targetX - game.getWidth() / 2));
		this.targetOY = (int) (-1 * (targetY - game.getHeight() / 2));
		this.timeToTake = timeToTake;
	}

	public void start() {
		initialOX = PlayState.camera.ox;
		initialOY = PlayState.camera.oy;
		PlayState.camera.centerOnEntity(null, false);
	}

	public void tick(double deltaTime) {
		super.tick(deltaTime);
		// Do stuff below
		timeTaken += deltaTime;
		PlayState.camera.ox = (int) (initialOX + (targetOX - initialOX) * Math.min(1, (double) timeTaken / timeToTake));
		PlayState.camera.oy = (int) (initialOY + (targetOY - initialOY) * Math.min(1, (double) timeTaken / timeToTake));
		if (timeTaken >= timeToTake) {
			complete();
		}
	}

}
