package Play;

import java.awt.Graphics;

import Engine.Game;
import Engine.Tools.Vec2;
import Engine.Tools.fRect;

public class Player extends Entity.Dynamic.Creature {

	/**
	 * @param game An instance of the game object
	 * @param x    The initial x coordinate in the world
	 * @param y    The initial y coordinate in the world
	 */
	public Player(Game game, Vec2 pos) {
		super(game, "Player", "Pikachu", pos);
		this.pos = pos;

		// Set player defaults
		screenSize = new Vec2(1, 1);
		relativeHitbox = new fRect(4.0 / 16, 11.0 / 16, 8.0 / 16, 5.0 / 16);

	}

	@Override
	public void tick(double deltaTime) {

		// Handle input
		if (game.keyDown('s') && !game.keyDown('w')) v.y = 0.1 * deltaTime;
		else if (game.keyDown('w') && !game.keyDown('s')) v.y = -0.1 * deltaTime;
		else v.y = 0;

		if (game.keyDown('a') && !game.keyDown('d')) v.x = -0.1 * deltaTime;
		else if (game.keyDown('d') && !game.keyDown('a')) v.x = 0.1 * deltaTime;
		else v.x = 0;

		// Handle animations
		moving = Math.abs(v.x) > 0 || Math.abs(v.y) > 0;
		if (v.y > 0) changeAnimation("Down");
		else if (v.y < 0) changeAnimation("Up");
		else if (v.x > 0) changeAnimation("Right");
		else if (v.x < 0) changeAnimation("Left");

		// Handle collisions
		handleCollisions();

		// Update animation
		currentAnimation.tick();
	}

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

	@Override
	public void onInteract(Entity e) {}

}
