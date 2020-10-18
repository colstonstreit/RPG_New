package Play.TheaterEngine.Commands;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.LinkedList;

import Play.Entities.Dynamic;
import Play.TheaterEngine.Cutscenes.Cutscene;
import Play.TheaterEngine.Cutscenes.CutsceneManager;
import Play.TheaterEngine.Cutscenes.CutsceneManager.Cutscenes;

public class TheaterEngine {

	private static LinkedList<ArrayList<BaseCommand>> commandGroups = new LinkedList<ArrayList<BaseCommand>>(); // The current queue of commands
	private static boolean justCompleted = false; // whether or not a command has been completed in the last tick() cycle

	private static Cutscene currentCutscene = null; // The current cutscene that is playing

	/**
	 * Returns true if there is a command currently in progress or if one has just completed.
	 */
	public static boolean hasCommand() { return justCompleted || !commandGroups.isEmpty() || currentCutscene != null; }

	/**
	 * Adds a new command to the queue.
	 * 
	 * @param c The command to be added to the queue.
	 */
	public static void add(BaseCommand c) {
		ArrayList<BaseCommand> t = new ArrayList<BaseCommand>();
		c.group = t;
		t.add(c);
		commandGroups.add(t);
	}

	/**
	 * Adds a new group of commands to the queue.
	 * 
	 * @param commandGroup A list of commands to be added to the queue.
	 * @param separate     True if each command in the commandGroup should be done in sequential order, False if should be done concurrently
	 */
	public static void addGroup(ArrayList<BaseCommand> commandGroup, boolean separate) {
		if (separate) {
			for (int i = 0, n = commandGroup.size(); i < n; i++)
				add(commandGroup.get(i));
		} else commandGroups.add(commandGroup);
	}

	/**
	 * Cues the commands that are contained with the cutscene with the given ID number.
	 */
	public static void cueCutscene(Cutscenes cutsceneID) {
		if (currentCutscene != null) {
			System.out.println("There is already a cutscene in progress!");
			return;
		}
		currentCutscene = CutsceneManager.getCutscene(cutsceneID);
		currentCutscene.init();
	}

	/**
	 * Updates the command at the front of the queue, and removes it if it has been completed.
	 */
	public static void tick(double deltaTime) {
		// Reset just completed flag every frame
		justCompleted = false;

		// Update the first group of commands if it exists, and pop it out if it's finished.
		if (!commandGroups.isEmpty()) {
			ArrayList<BaseCommand> list = commandGroups.get(0);

			// Update each command in the first command group, and remove them as they are finished
			for (int i = 0, n = list.size(); i < n; i++) {
				BaseCommand c = list.get(i);
				if (c.hasCompleted) {
					list.remove(i);
					i--;
					n--;
					justCompleted = true;
				} else {
					c.tick(deltaTime);
				}
			}

			// If all commands in this group have been completed, remove this empty group from the command list
			if (list.isEmpty()) commandGroups.pop();
		}

		// Update the current cutscene if there is one.
		if (currentCutscene != null) {
			currentCutscene.tick(deltaTime);
			if (currentCutscene.finished) currentCutscene = null;
		}
	}

	public static void render(Graphics g, int ox, int oy) {
		// Render each of the commands in the first group
		if (!commandGroups.isEmpty()) {
			ArrayList<BaseCommand> list = commandGroups.get(0);
			for (int i = 0, n = list.size(); i < n; i++)
				list.get(i).render(g, ox, oy);
		}

	}

	/**
	 * Returns true if a current command is a Move command AND the entity controlled by that move command is the Dynamic passed in.
	 * 
	 * @param e The entity to be checked whether or not is under control by a Move command.
	 */
	public static boolean hasControl(Dynamic e) {
		if (commandGroups.isEmpty() || commandGroups.get(0).isEmpty()) return false;

		for (int i = 0, n = commandGroups.get(0).size(); i < n; i++) {
			BaseCommand c = commandGroups.get(0).get(i);
			if (c instanceof MoveCommand && ((MoveCommand) c).e == e) return true;
		}

		return false;
	}
}
