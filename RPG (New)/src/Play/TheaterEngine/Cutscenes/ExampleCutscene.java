package Play.TheaterEngine.Cutscenes;

import java.awt.Color;
import java.util.ArrayList;

import Engine.AssetManager.CharacterSprites;
import Engine.Game;
import Engine.Tools.Function;
import Engine.Tools.Vec2;
import Play.PlayState;
import Play.Entities.Creature;
import Play.Entities.Creature.Facing;
import Play.Entities.NPC;
import Play.TheaterEngine.Commands.BaseCommand;
import Play.TheaterEngine.Cutscenes.CutsceneManager.Cutscenes;

public class ExampleCutscene extends Cutscene {

	private final double characterSpeed = 150; // speed in ms per tile

	Creature[] testEntity = new Creature[10];

	public ExampleCutscene(Game game) { super(game, Cutscenes.EXAMPLE); }

	public void init() {
		finished = false;

		int count = 0;
		for (int y = -8; y < -6; y++) {
			for (int x = -2; x <= 2; x++) {
				testEntity[count] = new NPC(game, "Josh" + (count++ + 1), CharacterSprites.PIKACHU, new Vec2(25 + x, 45 + y)).changeAnimation("Down");
			}
		}

		fade = doNormalFade(null);

		fade.addAction(move(PlayState.player, new Vec2(25, 45), 0));
		fade.addAction(turn(PlayState.player, Facing.Up));
		fade.addAction(focusCam(PlayState.player, false));
		fade.addAction(removeEntity("CueKill"));

		addQuestion("GENDER?", "Male", "Female", "Undecided");
	}

	public void tick(double deltaTime) {

		if (!flagSet("GENDER?")) {
			say("What's going on, man?");
			ask("What is your gender?", "GENDER?");

			setFlag("GENDER?", true);
		}

		if (hasResponse("GENDER?") && !flagSet("DONE_GENDER?")) {
			say("Wrong answer.");

			fade = doNormalFade(null);

			addToEngine = false;
			ArrayList<BaseCommand> commands = new ArrayList<BaseCommand>();
			for (int i = 0; i < testEntity.length; i++) {
				commands.add(addEntity(testEntity[i]));
			}
			fade.addActions(commands);
			addToEngine = true;

			setFlag("DONE_GENDER?", true);
		}

		if (flagSet("DONE_GENDER?")) {
			focusCam(testEntity[7], false, 1000);
			zoomCam(200, 500);
			say("It is time for you to die.");

			addToEngine = false;
			ArrayList<BaseCommand> commands = new ArrayList<BaseCommand>();
			for (int i = 0; i < testEntity.length; i++) {
				commands.add(move(testEntity[i], new Vec2(testEntity[i].pos.x, testEntity[i].pos.y + 6), characterSpeed));
			}
			addToEngine = true;
			addSimultaneousCommands(commands);

			say("Goodbye, Colston.");
			wait(1000);

			removeEntity(PlayState.player);
			focusCam(null, false);
			wait(2000);
			move(PlayState.player, new Vec2(25, 30), 0);

			fade = fadeOut(1000, 500, 500, Color.black, new Function() {

				public void run() { PlayState.refreshEntities(PlayState.map.id); }
			});
			addToEngine = false;
			fade.addAction(focusCam(PlayState.player, false, 0));
			fade.addAction(zoomCam(100, 0));
			addToEngine = true;

			finish(false);
		}
	}

}
