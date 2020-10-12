package Play.Entities;

import Engine.Game;
import Engine.Tools.Function;
import Engine.Tools.Vec2;
import Play.PlayState;
import Play.Maps.MapManager.Maps;
import Play.TheaterEngine.Commands.TeleportCommand;
import Play.TheaterEngine.Commands.TheaterEngine;

public class Teleport extends Trigger {

	private boolean hasInitiatedFadeOut = false; // Whether or not the fadeout has begun
	private Maps newMapName; // The name of the map to switch to

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
					TheaterEngine.add(new TeleportCommand(game, PlayState.player, newPos, newMapName, true, new Function() {

						public void run() {
							hasInitiatedFadeOut = false;
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
	public Teleport(Game game, boolean runOnInteract, String name, Vec2 newPos, Maps mapName) {
		this(game, runOnInteract, name, newPos);
		newMapName = mapName;
	}
}
