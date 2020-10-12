package Play.TheaterEngine.Commands;

import java.awt.Color;

import Engine.Game;
import Engine.Tools.Function;
import Engine.Tools.Vec2;
import Play.PlayState;
import Play.Entities.Dynamic;
import Play.Maps.MapManager;
import Play.Maps.MapManager.Maps;

public class TeleportCommand extends FadeOutCommand {

	private static final int timeBeforeTeleport = 500; // The amount of time that should pass before teleporting in milliseconds
	private static final int holdingTime = 500; // The amount of time that should pass while everything is faded out
	private static final int fadingInTime = 1000; // The amount of time that it should take to fade back in

	private Dynamic e; // The entity to teleport
	private Vec2 newPos; // The new position for the entity
	private Maps newMap; // The map it should be teleported to (null if same map)
	private boolean doFadeOut; // True if it should do the fade out with the teleport in the middle.
	private Function thisFunction; // The code this particular command ought to run

	/**
	 * @param game         The game instance
	 * @param e            The entity to be teleported
	 * @param newPos       The new location for the entity
	 * @param doFadeOut    True if a fade out should occur, and false if the teleportation should be instantaneous
	 * @param thisFunction A function to be called at the peak of the fadeout
	 */
	public TeleportCommand(Game game, Dynamic e, Vec2 newPos, boolean doFadeOut, Function thisFunction) {
		super(game, timeBeforeTeleport, 500, 1000, Color.black, null);
		this.e = e;
		this.newPos = newPos;
		this.newMap = null;
		this.doFadeOut = doFadeOut;
		this.thisFunction = thisFunction;
	}

	/**
	 * @param game         The game instance
	 * @param e            The entity to be teleported
	 * @param newPos       The new location for the entity
	 * @param newMap       The new map to be warped to.
	 * @param doFadeOut    True if a fade out should occur, and false if the teleportation should be instantaneous
	 * @param thisFunction A function to be called at the peak of the fadeout, can be null
	 */
	public TeleportCommand(Game game, Dynamic e, Vec2 newPos, Maps newMap, boolean doFadeOut, Function thisFunction) {
		super(game, timeBeforeTeleport, holdingTime, fadingInTime, Color.black, null);
		this.e = e;
		this.newPos = newPos;
		this.newMap = newMap;
		this.doFadeOut = doFadeOut;
		this.thisFunction = thisFunction;
	}

	public void start() {
		if (!doFadeOut) {
			if (newMap != null && PlayState.map.id != MapManager.get(newMap).id) {
				PlayState.changeMap(newMap);
			}
			e.setPos(newPos.x, newPos.y);
			e.setVel(0, 0);
			if (thisFunction != null) thisFunction.run();
			complete();
		} else {
			if (newMap == null || PlayState.map.id == MapManager.get(newMap).id) {
				function = new Function() {

					public void run() {
						e.setPos(newPos.x, newPos.y);
						e.setVel(0, 0);
						if (thisFunction != null) thisFunction.run();
					}
				};
			} else {
				function = new Function() {

					public void run() {
						PlayState.changeMap(newMap);
						e.setPos(newPos.x, newPos.y);
						e.setVel(0, 0);
						if (thisFunction != null) thisFunction.run();
					}
				};
			}
		}
	}
}
