package Play.TheaterEngine.Cutscenes;

import java.util.HashMap;

import Engine.Game;

public class CutsceneManager {

	public enum Cutscenes { EXAMPLE };

	private static HashMap<Cutscenes, Cutscene> cutsceneMap = new HashMap<Cutscenes, Cutscene>();

	/**
	 * Loads all of the cutscenes.
	 */
	public static void loadCutscenes(Game game) { cutsceneMap.put(Cutscenes.EXAMPLE, new ExampleCutscene(game)); }

	/**
	 * Returns the cutscene by the given id. It could return null if the cutscene does not exist.
	 * 
	 * @param id The ID of the cutscene to be obtained.
	 * @return The cutscene desired, or null if the ID does not match any.
	 */
	public static Cutscene getCutscene(Cutscenes id) {
		if (!cutsceneMap.containsKey(id)) {
			System.out.println("There exists no cutscene with the id: " + id + "!");
			return null;
		}
		return cutsceneMap.get(id);
	}

}
