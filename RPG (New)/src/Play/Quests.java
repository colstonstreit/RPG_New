package Play;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import Engine.Game;
import Engine.Tools.Vec2;
import Play.Entity.Dynamic;
import Play.Entity.Dynamic.Trigger;
import Play.Entity.Dynamic.Trigger.WillTrigger;
import Play.Entity.Dynamic.Triggerable;

public class Quests {

	private static HashMap<String, Quest> quests = new HashMap<String, Quest>(); // List of all quests
	public static LinkedList<Quest> currentQuestList = new LinkedList<Quest>(); // List of current quests

	/**
	 * Loads all quests into the quests list.
	 */
	public static void loadQuests(Game game) {
		quests.put("Test", new Quests.LavaMan(game));
		quests.put("PikachuRunToCorner", new Quests.PikachuRunToCorner(game));
	}

	/**
	 * Adds a new quest to the front of the list of quests if there is no other quest by the same name currently in the list.
	 * 
	 * @param questName The name of the quest
	 */
	public static void addQuest(String questName) {
		if (!quests.containsKey(questName)) {
			System.out.println("There exists no quest with the name: " + questName + "!");
			return;
		}

		Quest q = quests.get(questName);
		if (!currentQuestList.contains(q) && (q.numTimesCompleted == 0 || q.isRepeatable)) {
			if (q.isRepeatable) q.reset();
			q.isCompleted = false;
			currentQuestList.push(q);
		}
	}

	/**
	 * Returns true if the requested quest is currently in the list of current quests.
	 * 
	 * @param questName The name of the quest
	 */
	public static boolean doingQuest(String questName) {
		if (!quests.containsKey(questName)) {
			System.out.println("There exists no quest with the name: " + questName + ", so you can't be doing it right now!");
			return false;
		}
		return currentQuestList.contains(quests.get(questName));
	}

	/**
	 * Returns true if the requested quest has been completed. Only counts repeatable quests as being completed if countingRepeatable is set to true.
	 * 
	 * @param questName          The name of the quest
	 * @param countingRepeatable True if repeatable quests can be counted as completed, false if not
	 */
	public static boolean completedQuest(String questName, boolean countingRepeatable) {
		if (!quests.containsKey(questName)) {
			System.out.println("There exists no quest with the name: " + questName + ", so there's no way it's already completed!");
			return false;
		}
		Quest q = quests.get(questName);
		if (countingRepeatable) return q.numTimesCompleted > 0;
		else return q.numTimesCompleted > 0 && !q.isRepeatable;
	}

	/**
	 * Checks all the quests and removes the ones that are completed from the quest list.
	 */
	public static void removeCompleted() {
		for (int i = 0, n = currentQuestList.size(); i < n; i++) {
			if (currentQuestList.get(i).isCompleted) {
				currentQuestList.remove(i);
				i--;
				n--;
			}
		}
	}

	public abstract static class Quest {

		protected Game game; // An instance of the game
		public String name; // The name of the quest
		public boolean isCompleted; // Whether or not this quest has been completed

		public boolean isRepeatable; // Whether or not this quest can be repeated
		public int numTimesCompleted; // How many times this quest has been completed

		/**
		 * @param game The instance of the game
		 * @param name The name of the quest
		 */
		public Quest(Game game, String name) {
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
		 * Completes this quest, marking it for removal from the current quest list.
		 */
		public void complete() {
			isCompleted = true;
			numTimesCompleted++;
		}

		public boolean equals(Object q) { return this == q || (q instanceof Quest && name.equals(((Quest) q).name)); }

	}

	//////////////////////////////////////////////////////////////////////////////////////////

	public static class LavaMan extends Quest {

		private int phase = 0;

		public LavaMan(Game game) {
			super(game, "Test");
			isRepeatable = true;
		}

		public void reset() { phase = 0; }

		public void populateDynamics(String mapName, ArrayList<Dynamic> entities) {
			if (mapName.equals("Lol")) {

				entities.add(new NPC(game, "Steven", "Player", new Vec2(10, 10))
						.setText(phase == 0 ? "Talk to me one more time." : "Nice job, you finished this quest!"));

			}
		}

		public boolean onInteract(Entity target) {
			if (target.name.equals("Steven")) {
				if (phase == 0) {
					((NPC) target).setText("Nice job, you finished this quest!");
					phase++;
				} else if (phase == 1) {
					complete();
				}

				return true;
			}
			return false;
		}

	}

	//////////////////////////////////////////////////////////////////////////////////////////

	public static class PikachuRunToCorner extends Quest {

		public PikachuRunToCorner(Game game) { super(game, "PikachuRunToCorner"); }

		public void populateDynamics(String mapName, ArrayList<Dynamic> entities) {
			if (mapName.equals("Cool Island")) {
				entities.add((Trigger) new Trigger(game, "Pikachu's Corner", false, WillTrigger.ONCE, new Triggerable() {

					public void run() {
						TheaterEngine.add(new Command.ShowDialog(game, "Nice work! Go tell Pikachu you helped him!"));
						complete();
					}

				}).setShouldBeDrawn(true).setTransform(0, 0, 1, 1));
			}
		}

		public boolean onInteract(Entity target) { return false; }

	}
}
