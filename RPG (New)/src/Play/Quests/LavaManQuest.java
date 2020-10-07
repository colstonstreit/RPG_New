package Play.Quests;

import java.util.ArrayList;

import Engine.Game;
import Engine.AssetManager.CharacterSprites;
import Engine.Tools.Vec2;
import Play.Entities.Dynamic;
import Play.Entities.Entity;
import Play.Entities.NPC;
import Play.Maps.MapManager.Maps;
import Play.Quests.QuestManager.Quests;

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

			return phase == 0 ? "Talk to me one more time." : "Nice job, you finished this quest!";

		} else return super.getDialog(e);
	}

	public boolean onInteract(Entity target) {
		if (target == steven) {
			if (phase == 0) {
				phase++;
				steven.setText(getDialog(steven));
			} else if (phase == 1) {
				complete();
			}

			return true;
		}
		return false;
	}

}
