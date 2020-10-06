package Play.Quests;

import java.util.ArrayList;

import Engine.Game;
import Engine.Tools.Function;
import Play.Entities.Dynamic;
import Play.Entities.Entity;
import Play.Entities.NPC;
import Play.Entities.Trigger;
import Play.Entities.Trigger.WillTrigger;
import Play.TheaterEngine.ShowDialogCommand;
import Play.TheaterEngine.TheaterEngine;

public class PikachuRunToCornerQuest extends BaseQuest {

	private static Trigger pikachuCorner;

	public PikachuRunToCornerQuest(Game game) {
		super(game, "PikachuRunToCorner");
		pikachuCorner = (Trigger) new Trigger(game, "Pikachu's Corner", false, WillTrigger.ONCE, new Function() {

			public void run() {
				TheaterEngine.add(new ShowDialogCommand(game, "Nice work! Go tell Pikachu you helped him!"));
				((NPC) initiator).setText("You helped me! Thank you so much.");

				complete();
			}

		}).setShouldBeDrawn(true).setTransform(0, 0, 1, 1);
	}

	public void populateDynamics(String mapName, ArrayList<Dynamic> entities) {
		if (mapName.equals("Cool Island")) {
			entities.add(pikachuCorner);
		}
	}

	public boolean onInteract(Entity target) { return false; }

}
