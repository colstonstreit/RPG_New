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
		// TODO: interaction!
		System.out.println("Hello! I am an NPC and you are an " + e.type + "!");
	}

	@Override
	public void tick(double deltaTime) { handleCollisions(); }

	@Override
	public void render(Graphics g, int ox, int oy) {
		// Get screen position, and return if off screen
		Vec2 screenPos = getState().worldToScreen(pos);
		Vec2 screenCornerPos = getState().worldToScreen(pos.add(screenSize));
		if (screenPos.x > game.getWidth() || screenPos.y > game.getHeight() || screenCornerPos.x < 0 || screenCornerPos.y < 0) return;

		// Draw correct image if on screen
		if (moving)
			Game.drawImage(g, currentAnimation.currentFrame().image(), screenPos.x, screenPos.y, screenSize.x * Tile.GAME_SIZE, screenSize.y * Tile.GAME_SIZE);
		else Game.drawImage(g, currentAnimation.firstFrame().image(), screenPos.x, screenPos.y, screenSize.x * Tile.GAME_SIZE, screenSize.y * Tile.GAME_SIZE);
	}

}
