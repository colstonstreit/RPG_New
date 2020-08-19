package Play;

import java.awt.Graphics;

import Engine.Game;
import Engine.Tools.Vec2;
import Engine.Tools.fRect;

public class NPC extends Entity.Dynamic.Creature {

	private String text;

	public NPC(Game game, String imageName, Vec2 pos) {
		super(game, "NPC", imageName, pos);
		this.text = "I AM ERROR";
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
			TheaterEngine.add(new Command.ShowDialog(game, text));
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
	 * Sets this NPC's text and then returns the NPC.
	 */
	public NPC setText(String s) {
		text = s;
		return this;
	}

}
