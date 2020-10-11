package Play.Quests;

import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedList;

import Engine.Game;
import Play.Entities.Entity;
import Play.TheaterEngine.FadeOutCommand;
import Play.TheaterEngine.TheaterEngine;

public class QuestManager {

	public static enum Quests { LAVA_MAN, PIKACHU_CORNER }

	private static HashMap<Quests, Quest> quests = new HashMap<Quests, Quest>(); // List of all quests
	public static LinkedList<Quest> currentQuestList = new LinkedList<Quest>(); // List of current quests

	private static Game game; // The instance of the game

	/**
	 * Loads all quests into the quests list.
	 */
	public static void loadQuests(Game game) {
		quests.put(Quests.LAVA_MAN, new LavaManQuest(game));
		quests.put(Quests.PIKACHU_CORNER, new PikachuCornerQuest(game));

		QuestManager.game = game;
	}

	/**
	 * Sets the initiator of the quest with name questName as the passed-in entity.
	 * 
	 * @param questName The name of the quest to be set
	 * @param initiator The initiator of the test called questName
	 */
	public static void setInitiator(Quests questName, Entity initiator) {
		if (!quests.containsKey(questName)) {
			System.out.println("There exists no quest with the name: " + questName + ", so obviously nobody could have initiated it!");
			return;
		}
		quests.get(questName).initiator = initiator;
	}

	/**
	 * Adds a new quest to the front of the list of quests if there is no other quest by the same name currently in the list.
	 * 
	 * @param questName The name of the quest
	 */
	public static void addQuest(Quests questName) {
		if (!quests.containsKey(questName)) {
			System.out.println("There exists no quest with the name: " + questName + "!");
			return;
		}

		Quest q = quests.get(questName);
		if (!currentQuestList.contains(q) && (q.numTimesCompleted == 0 || q.isRepeatable)) {
			if (q.isRepeatable) q.reset();
			q.isCompleted = false;
			currentQuestList.push(q);
			TheaterEngine.add(new FadeOutCommand(game, 500, 500, 500, Color.black, null));
		}
	}

	/**
	 * Returns true if the requested quest is currently in the list of current quests.
	 * 
	 * @param questName The name of the quest
	 */
	public static boolean doingQuest(Quests questName) {
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
	public static boolean completedQuest(Quests questName, boolean countingRepeatable) {
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

}
