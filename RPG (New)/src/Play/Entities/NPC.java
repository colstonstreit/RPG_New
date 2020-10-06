package Play.Entities;

import java.awt.Graphics;

import Engine.Game;
import Engine.Tools.Vec2;
import Engine.Tools.fRect;
import Play.TheaterEngine.ShowDialogCommand;
import Play.TheaterEngine.TheaterEngine;

public class NPC extends Creature {

	private String[] textOptions;

	public NPC(Game game, String name, String imageName, Vec2 pos) {
		super(game, name, imageName, pos);
		this.textOptions = new String[] { "I AM ERROR" };
		relativeHitbox = new fRect(0, 0.5, 1, 0.5);
	}

	public void onInteract(Entity e) {
		if (e instanceof Creature) {
			Creature creature = (Creature) e;
			switch (creature.facing) {
				case Up:
					changeAnimation("Down");
					break;
				case Down:
					changeAnimation("Up");
					break;
				case Left:
					changeAnimation("Right");
					break;
				case Right:
					changeAnimation("Left");
					break;
			}
			v = new Vec2(0, 0);
			TheaterEngine.add(new ShowDialogCommand(game, textOptions[(int) (Math.random() * textOptions.length)]));
		}
	}

	public void tick(double deltaTime) {

		super.tick(deltaTime);

	}

	public void render(Graphics g, int ox, int oy) {

		if (isOnScreen()) {
			super.render(g, ox, oy);
			// worldToScreen(interactableRegion()).draw(g, Color.white);
		}

	}

	/**
	 * Sets this NPC's text options and then returns the NPC.
	 */
	public NPC setText(String... options) {
		textOptions = options;
		return this;
	}

}
