package Play.TheaterEngine.Commands;

import Engine.Game;
import Play.PlayState;
import Play.Entities.Dynamic;

public class RemoveEntityCommand extends BaseCommand {

	private Dynamic entityToRemove; // The entity to be removed
	private String nameToRemove; // The name of the entity to be removed

	/**
	 * @param game The game instance
	 * @param nameToRemove The name of the entity to be removed
	 */
	public RemoveEntityCommand(Game game, String nameToRemove) {
		super(game);
		this.nameToRemove = nameToRemove;
	}

	/**
	 * @param game The game instance
	 * @param entityToRemove The entity itself to be removed (passed in as a variable that was already declared when added)
	 */
	public RemoveEntityCommand(Game game, Dynamic entityToRemove) {
		super(game);
		this.entityToRemove = entityToRemove;
		if (entityToRemove != null) this.nameToRemove = entityToRemove.name;
	}

	public void start() {
		boolean foundEntity = false;
		for (int i = PlayState.entities.size() - 1; i >= 0; i--) {
			Dynamic d = PlayState.entities.get(i);
			if ((entityToRemove != null && entityToRemove == d) || (nameToRemove != null && nameToRemove.equals(d.name))) {
				foundEntity = true;
				PlayState.entities.remove(i);
				break;
			}
		}
		if (!foundEntity) System.out.println("Could not find the entity to remove with the name: " + nameToRemove);
		complete();
	}

}
