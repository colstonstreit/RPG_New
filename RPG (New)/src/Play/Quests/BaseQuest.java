package Play.Quests;

import java.util.ArrayList;

import Engine.Game;
import Play.Entities.Dynamic;
import Play.Entities.Entity;

public abstract class BaseQuest {

	protected Entity initiator;

	protected Game game; // An instance of the game
	public String name; // The name of the quest
	public boolean isCompleted; // Whether or not this quest has been completed

	public boolean isRepeatable; // Whether or not this quest can be repeated
	public int numTimesCompleted; // How many times this quest has been completed

	/**
	 * @param game The instance of the game
	 * @param name The name of the quest
	 */
	public BaseQuest(Game game, String name) {
		this.game = game;
		this.name = name;
	}

	/**
	 * Resets any variables upon being called (usually so repeatable quests can work again).
	 */
	public void reset() {}

	/**
	 * Adds the correct entities to the provided list if the correct map is passed in.
	 * 
	 * @param mapName  The name of the current map (for selection purposes)
	 * @param entities The list of entities which new entities will be added to
	 */
	public abstract void populateDynamics(String mapName, ArrayList<Dynamic> entities);

	/**
	 * Called when a player interacts with a target to see if the target has something to do with this quest.
	 * 
	 * @param target The entity that the player has interacted with
	 */
	public abstract boolean onInteract(Entity target);

	/**
	 * Returns the text that a particular NPC should have given parameters of the quest.
	 * 
	 * @param e The entity that dialog should be obtained for.
	 */
	public String getDialog(Entity e) { return "I AM ERROR"; }

	/**
	 * Completes this quest, marking it for removal from the current quest list.
	 */
	public void complete() {
		isCompleted = true;
		numTimesCompleted++;
	}

	public boolean equals(Object q) { return this == q || (q instanceof BaseQuest && name.equals(((BaseQuest) q).name)); }

}
