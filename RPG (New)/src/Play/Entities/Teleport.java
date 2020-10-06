package Play.Entities;

import java.awt.Color;

import Engine.Game;
import Engine.Tools.Function;
import Engine.Tools.Vec2;
import Play.PlayState;
import Play.TheaterEngine.FadeOutCommand;
import Play.TheaterEngine.TheaterEngine;

public class Teleport extends Trigger {

	private boolean hasInitiatedFadeOut = false; // Whether or not the fadeout has begun
	private static final int timeBeforeTeleport = 500; // The amount of time that should pass before teleporting in milliseconds
	private String newMapName; // The name of the map to switch to

	/**
	 * @param game          The instance of the game
	 * @param runOnInteract True if the function should be run when upon interaction; False if should be run upon collision with player
	 * @param name          The name of the teleport (for identification purposes)
	 * @param newPos        The new position to which the player should be teleported
	 */
	public Teleport(Game game, boolean runOnInteract, String name, Vec2 newPos) {
		super(game, name, runOnInteract, WillTrigger.FOREVER, null);
		newMapName = null;
		setFunction(new Function() {

			public void run() {

				// Start fadeout if haven't already
				if (!hasInitiatedFadeOut) {
					TheaterEngine.add(new FadeOutCommand(game, timeBeforeTeleport, 500, 1000, Color.black, new Function() {

						public void run() {
							// If fadeout is complete, teleport player (to new map if necessary) and reset initiatedFadeout in case it is a
							// repetitive trigger
							PlayState.player.setPos(newPos.x, newPos.y);
							PlayState.player.v = new Vec2(0, 0);
							hasInitiatedFadeOut = false;

							// Trigger PlayState to change the map at the end of this tick() cycle
							if (newMapName != null) PlayState.newMapName = newMapName;

							if (triggerType == WillTrigger.ONCE) active = false;
							wasInteractedWith = false;
						}

					}));
					hasInitiatedFadeOut = true;
				}
			}

		});
	}

	/**
	 * @param game          The instance of the game
	 * @param runOnInteract True if the function should be run when upon interaction; False if should be run upon collision with player
	 * @param name          The name of the teleport (for identification purposes)
	 * @param newPos        The new position to which the player should be teleported
	 * @param mapName       The name of the map to be switched to
	 */
	public Teleport(Game game, boolean runOnInteract, String name, Vec2 newPos, String mapName) {
		this(game, runOnInteract, name, newPos);
		newMapName = mapName;
	}
}
