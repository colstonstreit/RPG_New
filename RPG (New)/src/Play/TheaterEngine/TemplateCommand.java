package Play.TheaterEngine;

import java.awt.Graphics;

import Engine.Game;

public class TemplateCommand extends BaseCommand {

	private TemplateCommand(Game game) { super(game); }

	public void start() {}

	public void tick(double deltaTime) {
		super.tick(deltaTime);
		// Do stuff below

	}

	public void render(Graphics g, int ox, int oy) {}

	public void complete() {

		// Do stuff above
		super.complete();
	}

}
