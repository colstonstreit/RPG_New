package Play.Quests;

import java.util.ArrayList;

import Engine.Game;
import Engine.Tools.Function;
import Play.Entities.Dynamic;
import Play.Entities.Entity;
import Play.Entities.NPC;
import Play.Entities.Trigger;
import Play.Entities.Trigger.WillTrigger;
import Play.Maps.MapManager.Maps;
import Play.Quests.QuestManager.Quests;
import Play.TheaterEngine.ShowDialogCommand;
import Play.TheaterEngine.TheaterEngine;

public class PikachuCornerQuest extends Quest {

	private static Trigger pikachuCorner;

	public PikachuCornerQuest(Game game) {
		super(game, Quests.PIKACHU_CORNER);
		pikachuCorner = (Trigger) new Trigger(game, "Pikachu's Corner", false, WillTrigger.ONCE, new Function() {

			public void run() {
				TheaterEngine.add(new ShowDialogCommand(game, "Nice work! Go tell Pikachu you helped him!"));
				((NPC) initiator).setText("You helped me! Thank you so much.");

				complete();
			}

		}).setShouldBeDrawn(true).setTransform(0, 0, 1, 1);
	}

	public void populateDynamics(Maps mapID, ArrayList<Dynamic> entities) {
		if (mapID == Maps.COOL_ISLAND) {
			entities.add(pikachuCorner);
		}
	}

	public boolean onInteract(Entity target) { return false; }

}
