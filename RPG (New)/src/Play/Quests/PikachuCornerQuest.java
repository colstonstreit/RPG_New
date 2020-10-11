package Play.Quests;

import java.util.ArrayList;

import Engine.Game;
import Engine.Tools.Function;
import Play.PlayState;
import Play.Entities.Dynamic;
import Play.Entities.Entity;
import Play.Entities.NPC;
import Play.Entities.Trigger;
import Play.Entities.Trigger.WillTrigger;
import Play.Entities.Items.ItemManager;
import Play.Entities.Items.ItemManager.Items;
import Play.Maps.MapManager.Maps;
import Play.Quests.QuestManager.Quests;
import Play.TheaterEngine.ReceiveItemCommand;
import Play.TheaterEngine.ShowDialogCommand;
import Play.TheaterEngine.TheaterEngine;

public class PikachuCornerQuest extends Quest {

	private static Trigger pikachuCorner;
	private int phase = 0;

	public PikachuCornerQuest(Game game) {
		super(game, Quests.PIKACHU_CORNER);
		pikachuCorner = (Trigger) new Trigger(game, "Pikachu's Corner", false, WillTrigger.ONCE, new Function() {

			public void run() {
				TheaterEngine.add(new ShowDialogCommand(game, "Nice work! Go tell Pikachu you helped him!"));
				((NPC) initiator).setText("You helped me! Thank you so much.");
				phase++;
			}

		}).setShouldBeDrawn(true).setTransform(0, 0, 1, 1);
	}

	public void populateDynamics(Maps mapID, ArrayList<Dynamic> entities) {
		if (mapID == Maps.COOL_ISLAND) {
			entities.add(pikachuCorner);
		}
	}

	public boolean onInteract(Entity target) {
		if (target == initiator) {
			if (phase == 1) {
				TheaterEngine.add(new ReceiveItemCommand(game, Items.ORANGE, 1, PlayState.player, false));
				if (1 != ItemManager.giveItem(Items.ORANGE, 1)) {
					TheaterEngine.add(new ShowDialogCommand(game, "Uh oh! Looks like you couldn't fit everything in your inventory!"));
				}
				complete();
				return true;
			}
			return true;
		}

		return false;
	}

}
