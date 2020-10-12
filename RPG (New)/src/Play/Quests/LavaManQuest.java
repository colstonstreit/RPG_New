package Play.Quests;

import java.util.ArrayList;

import Engine.AssetManager.CharacterSprites;
import Engine.Game;
import Engine.Tools.Vec2;
import Play.PlayState;
import Play.Entities.Dynamic;
import Play.Entities.Entity;
import Play.Entities.NPC;
import Play.Entities.Items.ItemManager;
import Play.Entities.Items.ItemManager.Items;
import Play.Maps.MapManager.Maps;
import Play.Quests.QuestManager.Quests;
import Play.TheaterEngine.Commands.ReceiveItemCommand;
import Play.TheaterEngine.Commands.ShowDialogCommand;
import Play.TheaterEngine.Commands.TheaterEngine;

public class LavaManQuest extends Quest {

	private int phase = 0;

	private static NPC steven;

	public LavaManQuest(Game game) {
		super(game, Quests.LAVA_MAN);
		isRepeatable = true;
		steven = new NPC(game, "Steven", CharacterSprites.PLAYER, new Vec2(10, 10))
				.setText(phase == 0 ? "Talk to me one more time." : "Nice job, you finished this quest!");
	}

	public void reset() { phase = 0; }

	public void populateDynamics(Maps mapID, ArrayList<Dynamic> entities) {
		if (mapID == Maps.LOL) {
			entities.add(steven.setText(getDialog(steven)));
		}
	}

	public String getDialog(Entity e) {
		if (e == steven) {

			return phase == 0 ? "Talk to me one more time." : "Nice job, you finished this quest! Have some apples.";

		} else return super.getDialog(e);
	}

	public boolean onInteract(Entity target) {
		if (target == steven) {
			if (phase == 0) {
				phase++;
				steven.setText(getDialog(steven));
			} else if (phase == 1) {
				complete();
				TheaterEngine.add(new ReceiveItemCommand(game, Items.APPLE, 50, PlayState.player, false));
				if (50 != ItemManager.giveItem(Items.APPLE, 50)) {
					TheaterEngine.add(new ShowDialogCommand(game, "Uh oh! Looks like you couldn't fit everything in your inventory!"));
				}
			}

			return true;
		} else if (target == initiator) {
			if (phase == 0) {
				if (ItemManager.hasItem(Items.APPLE, 5)) {
					ItemManager.takeItem(Items.APPLE, 5);
					ItemManager.printContents();
				} else {
					((NPC) initiator).setText("Aw, you don't have five apples for me. :(");
				}
			}
			return true;
		}
		return false;
	}

}
