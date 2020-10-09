package Play.TheaterEngine;

import java.awt.Graphics;
import java.util.ArrayList;

import Engine.Game;

public abstract class BaseCommand {

	protected Game game; // an instance of the Game object
	protected ArrayList<BaseCommand> group; // The group of commands this
	public boolean hasStarted = false, hasCompleted = false; // Whether or not the command has been started and completed

	public BaseCommand(Game game) {
		this.game = game;
		group = null;
	}

	/**
	 * Performs tasks that can only be done once at the start of the command.
	 */
	public void start() {}

	/**
	 * Calls the start method if hasn't already, then updates the command.
	 */
	public void tick(double deltaTime) {
		if (!hasStarted) {
			start();
			hasStarted = true;
		}
	}

	/**
	 * Renders the command if applicable.
	 */
	public void render(Graphics g, int ox, int oy) {}

	/**
	 * Completes the command.
	 */
	public void complete() { hasCompleted = true; }

}
