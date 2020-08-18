package Play;

import java.awt.Graphics;

import Engine.Game;
import Engine.Tools.Vec2;
import Engine.Tools.fRect;

public class NPC extends Entity.Dynamic.Creature {

	public NPC(Game game, String imageName, Vec2 pos) {
		super(game, "NPC", imageName, pos);
		relativeHitbox = new fRect(0, 0.5, 1, 0.5);

	}

	@Override
	public void onInteract(Entity e) {
		if (e instanceof Player) {
			Player player = (Player) e;
			switch (player.facing) {
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
		}
	}

	@Override
	public void tick(double deltaTime) {

		super.tick(deltaTime);

	}

	@Override
	public void render(Graphics g, int ox, int oy) {

		if (isOnScreen()) super.render(g, ox, oy);

	}

}
