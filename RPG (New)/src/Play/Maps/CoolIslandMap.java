package Play.Maps;

import java.util.ArrayList;

import Engine.Game;
import Engine.Tools.Vec2;
import Play.Entities.Dynamic;
import Play.Entities.Entity;
import Play.Entities.NPC;
import Play.Entities.Teleport;
import Play.Quests.QuestManager;

public class CoolIslandMap extends TileMap {

	private static NPC sparky, squirty, bulby;
	private static Teleport throughLava;

	public CoolIslandMap(Game game) {
		super(game, "Cool Island");
		QuestManager.setInitiator("PikachuRunToCorner", sparky = new NPC(game, "Sparky", "Pikachu", new Vec2(19, 29)));
		QuestManager.setInitiator("LavaMan", squirty = new NPC(game, "Squirty", "Squirtle", new Vec2(30, 29)));
		bulby = new NPC(game, "Bulby", "Bulbasaur", new Vec2(15, 15)).setText("Hi!", "You're awesome!", "I feel sad.",
				"There comes a time in a young boy's life where they realize that everything they thought they knew was wrong. "
						+ "Girls don't have cooties; they're actually quite likable. All those toys you played with when you were little "
						+ "suddenly seem to have no importance. \nIt's a true shame because it's sad to think that we all must lose the "
						+ "childhood innocence that the world once praised of us... Sigh. So how are you?");

		throughLava = (Teleport) new Teleport(game, true, "throughLava", new Vec2(11, 12), "Lol").setShouldBeDrawn(true).setCollisionType(true, true)
				.setTransform(20, 24, 10, 5);
	}

	public void populateDynamics(ArrayList<Dynamic> entities) {
		entities.add(sparky.setText(getDialog(sparky)));
		entities.add(squirty.setText(getDialog(squirty)));
		entities.add(bulby);
		entities.add(throughLava);
	}

	public String getDialog(Entity e) {
		if (e == sparky) {
			return !QuestManager.completedQuest("PikachuRunToCorner", false) ? "Hey, would you run to the top-left corner for me?"
					: "You helped me! Thank you so much.";
		} else if (e == squirty) {
			return !QuestManager.doingQuest("LavaMan") && !QuestManager.completedQuest("LavaMan", false)
					? "I've got a quest for you! Go talk to the man past the lava. He'll explain what you need to do."
					: "Have fun!";
		} else return super.getDialog(e);
	}

	public boolean onInteract(Entity target) {
		if (target == squirty) {
			QuestManager.addQuest("LavaMan", false);
			squirty.setText(getDialog(squirty));
			return true;
		} else if (target == sparky) {
			QuestManager.addQuest("PikachuRunToCorner", true);
			return true;
		}
		return false;
	}

}
