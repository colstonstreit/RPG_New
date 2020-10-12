package Play.TheaterEngine.Cutscenes;

import Engine.Game;
import Engine.Tools.Vec2;
import Play.PlayState;
import Play.Entities.Creature.Facing;
import Play.Entities.Items.ItemManager.Items;
import Play.Maps.MapManager.Maps;
import Play.TheaterEngine.Commands.TheaterEngine;
import Play.TheaterEngine.Cutscenes.CutsceneManager.Cutscenes;

public class ExampleCutscene extends Cutscene {

	private final double characterSpeed = 150; // speed in ms per tile

	public ExampleCutscene(Game game) { super(game, Cutscenes.EXAMPLE); }

	public void init() {
		finished = false;

		setFlag("ASKED_AGE", false);
		addQuestion("IS_HOW_OLD?", "I'm 19!", "I'm old. Who cares?");

		fade = doNormalFade(null);
	}

	public void tick(double deltaTime) {

		if (!flagSet("ASKED_AGE")) {
			say("Sup guys!");
			wait(1000);
			addToEngine = false;
			TheaterEngine.addGroup(addSimultaneousCommands(panCam(new Vec2(0, 0), 1000), ask("How old are you?", "IS_HOW_OLD?")), false);
			addToEngine = true;
			setFlag("ASKED_AGE", true);
		}

		if (!flagSet("IS_HOW_OLD?")) {
			if (hasResponse("IS_HOW_OLD?")) {
				say("You said: " + getSelectedResponse("IS_HOW_OLD?"));
				move(PlayState.player, new Vec2(7, 7), characterSpeed);
				focusCam(PlayState.player, false, 2000);
				move(PlayState.player, new Vec2(0, 49), characterSpeed);

				fade = doNormalFade(null);
				addToEngine = false;
				fade.addAction(move(PlayState.player, new Vec2(0, 0), 0, true));
				fade.addAction(turn(PlayState.player, Facing.Right));
				addToEngine = true;

				wait(2000);
				move(PlayState.player, new Vec2(10, 49), characterSpeed);
				giveItem(Items.APPLE, 100, PlayState.player, true);
				move(PlayState.player, new Vec2(10, 25), characterSpeed);
				setFlag("IS_HOW_OLD?", true);
			}
		} else {
			finish();
			fade.addAction(teleport(PlayState.player, new Vec2(0, 0), Maps.LOL, false, null));
		}
	}

}
