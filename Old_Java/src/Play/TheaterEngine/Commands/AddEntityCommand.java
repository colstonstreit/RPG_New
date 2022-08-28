package Play.TheaterEngine.Commands;

import Engine.Game;
import Play.PlayState;
import Play.Entities.Dynamic;

public class AddEntityCommand extends BaseCommand {

	private Dynamic entityToAdd; // The entity to add to the scene

	/**
	 * @param game The game instance
	 * @param entity The entity to be added to the scene
	 */
	public AddEntityCommand(Game game, Dynamic entity) {
		super(game);
		entityToAdd = entity;
	}

	public void start() {
		
		if (entityToAdd == null) {
			System.out.println("You can't add a null entity!");
			complete();
			return;
		}
		
		boolean foundEntity = false;
		for (int i = 0; i < PlayState.entities.size(); i++) {
			Dynamic d = PlayState.entities.get(i);
			if (d.name.equals(entityToAdd.name)) {
				System.out.println("There already exists an entity with the name: " + d.name + "! Did not add the entity.");
				foundEntity = true;
				break;
			}
		}
		if (!foundEntity) PlayState.entities.add(entityToAdd);
		complete();
	}

}
