package Play.TheaterEngine.Commands;

import Engine.Game;
import Play.Entities.Creature;
import Play.Entities.Creature.Facing;

public class TurnCommand extends BaseCommand {

	private Creature creatureToTurn; // The creature to turn
	private Facing newFacingDirection; // The new direction they should face

	/**
	 * @param game The instance of the game
	 * @param c    The creature to be turned
	 * @param f    The direction the creature should be turned
	 */
	public TurnCommand(Game game, Creature c, Facing f) {
		super(game);
		creatureToTurn = c;
		newFacingDirection = f;
	}

	public void start() {
		creatureToTurn.changeAnimation(newFacingDirection.toString());
		complete();
	}

}
