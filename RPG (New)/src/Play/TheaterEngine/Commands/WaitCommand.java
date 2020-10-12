package Play.TheaterEngine.Commands;

import Engine.Game;

public class WaitCommand extends BaseCommand {

	private long timer; // A timer to measure how much time has passed
	private int delay; // The delay in milliseconds to wait

	/**
	 * @param game  The instance of the Game object
	 * @param delay The delay in milliseconds that should be waited
	 */
	public WaitCommand(Game game, int delay) {
		super(game);
		this.delay = delay;
	}

	public void start() { timer = System.currentTimeMillis(); }

	public void tick(double deltaTime) {
		super.tick(deltaTime);
		// Complete command if the wait period has passed
		if (System.currentTimeMillis() - timer >= delay) complete();
	}
}
