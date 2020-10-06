package Play.Quests;

import java.util.ArrayList;

import Engine.Game;
import Engine.Tools.Vec2;
import Play.Entities.Dynamic;
import Play.Entities.Entity;
import Play.Entities.NPC;

public class LavaManQuest extends BaseQuest {

	private int phase = 0;

	private static NPC steven;

	public LavaManQuest(Game game) {
		super(game, "LavaMan");
		isRepeatable = true;
		steven = new NPC(game, "Steven", "Player", new Vec2(10, 10)).setText(phase == 0 ? "Talk to me one more time." : "Nice job, you finished this quest!");
	}

	public void reset() { phase = 0; }

	public void populateDynamics(String mapName, ArrayList<Dynamic> entities) {
		if (mapName.equals("Lol")) {
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
