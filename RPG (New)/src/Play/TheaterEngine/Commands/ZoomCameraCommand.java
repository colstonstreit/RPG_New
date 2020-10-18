package Play.TheaterEngine.Commands;

import Engine.Game;
import Engine.Tools.Vec2;
import Play.PlayState;
import Play.Entities.Entity;
import Play.Entities.Trigger;
import Play.Entities.Trigger.WillTrigger;
import Play.Maps.Tile;

public class ZoomCameraCommand extends BaseCommand {

	private SetCameraFocusCommand focusCamCommand;
	private Entity wasFocusedOn = null;

	private int msDelay;
	private int startingTileSize;
	private int targetTileSize;
	private Trigger toCenterOn;

	private double timer = 0;

	/**
	 * @param game           The game instance
	 * @param zoomPercentage The percentage of Tile.NORM_GAME_SIZE to set the camera zoom to.
	 * @param msDelay        The number of milliseconds that this zooming motion should take.
	 */
	public ZoomCameraCommand(Game game, double zoomPercentage, int msDelay) {
		super(game);
		this.msDelay = msDelay;
		targetTileSize = (int) (Tile.NORM_GAME_SIZE * zoomPercentage / 100);
	}

	public void start() {
		Vec2 center = PlayState.player.screenToWorld(new Vec2(game.getWidth() / 2, game.getHeight() / 2));
		toCenterOn = (Trigger) new Trigger(game, "Center", true, WillTrigger.ONCE, null).setPos(center.x, center.y).setSize(0, 0);
		focusCamCommand = new SetCameraFocusCommand(game, toCenterOn, false);
		startingTileSize = Tile.GAME_SIZE;
		wasFocusedOn = PlayState.camera.e;
		group.add(focusCamCommand);
	}

	public void tick(double deltaTime) {
		super.tick(deltaTime);
		// Do stuff below
		if (focusCamCommand.hasCompleted) {
			timer += deltaTime;
			if (timer >= msDelay) {
				Tile.GAME_SIZE = targetTileSize;
				complete();
			} else {
				Tile.GAME_SIZE = startingTileSize + (int) (timer / msDelay * (targetTileSize - startingTileSize));
			}
		}
	}

	public void complete() {
		focusCamCommand = new SetCameraFocusCommand(game, wasFocusedOn, false);
		group.add(focusCamCommand);
		// Do stuff above
		super.complete();
	}

}
