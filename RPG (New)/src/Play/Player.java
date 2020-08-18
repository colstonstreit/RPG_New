package Play;

import java.awt.Graphics;
import java.util.HashMap;

import Engine.Animation;
import Engine.Assets;
import Engine.Game;
import Engine.Sprite;
import Engine.Tools.Vec2;
import Engine.Tools.fRect;

public class Player extends Entity.Dynamic {

	protected HashMap<String, Animation> animations = new HashMap<>(); // A hashmap containing the different player animations
	protected Animation currentAnimation; // The current animation to be shown
	protected boolean moving = false; // Whether or not the player is currently moving.

	/**
	 * @param game An instance of the game object
	 * @param x    The initial x coordinate in the world
	 * @param y    The initial y coordinate in the world
	 */
	public Player(Game game, double x, double y) {
		super(game, "Player");
		this.pos = new Vec2(x, y);

		// Set player defaults
		solidVsStatic = true;
		solidVsDynamic = true;
		screenSize = new Vec2(1.5, 1.5);
		relativeHitbox = new fRect(4.0 / 16, 11.0 / 16, 8.0 / 16, 5.0 / 16);

		// Get spritesheet and set animations
		Sprite playerSprites = Assets.getCharacterSpriteSheet("Player");
		animations.put("Down", new Animation(100, playerSprites, new int[][] { { 0 , 0 } , { 0 , 1 } , { 0 , 2 } , { 0 , 1 } }));
		animations.put("Up", new Animation(100, playerSprites, new int[][] { { 1 , 0 } , { 1 , 1 } , { 1 , 2 } , { 1 , 1 } }));
		animations.put("Right", new Animation(100, playerSprites, new int[][] { { 2 , 0 } , { 2 , 1 } , { 2 , 2 } , { 2 , 1 } }));
		animations.put("Left", new Animation(100, playerSprites, new int[][] { { 3 , 0 } , { 3 , 1 } , { 3 , 2 } , { 3 , 1 } }));

		// Set down as default
		changeAnimation("Down");
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
		if (v.x != 0) {
			pos.x += v.x;
			if (solidVsStatic) {
				fRect hitbox = new fRect(pos.x + screenSize.x * relativeHitbox.x, pos.y + screenSize.y * relativeHitbox.y, screenSize.x * relativeHitbox.width,
						screenSize.y * relativeHitbox.height);
				double hitboxLeft = screenSize.x * relativeHitbox.x;
				double hitboxRight = screenSize.x * (relativeHitbox.x + relativeHitbox.width);
				for (fRect r : PlayState.map.getColliders()) {
					if (hitbox.intersects(r)) {
						if (v.x > 0 && pos.x + hitboxRight > r.x) pos.x = r.x - hitboxRight;
						else if (v.x < 0 && pos.x + hitboxLeft < r.x + 1) pos.x = r.x + 1 - hitboxLeft;
					}
				}
			}
		}

		if (v.y != 0) {
			pos.y += v.y;
			if (solidVsStatic) {
				fRect hitbox = new fRect(pos.x + screenSize.x * relativeHitbox.x, pos.y + screenSize.y * relativeHitbox.y, screenSize.x * relativeHitbox.width,
						screenSize.y * relativeHitbox.height);
				double hitboxTop = screenSize.y * relativeHitbox.y;
				double hitboxBottom = screenSize.y * (relativeHitbox.y + relativeHitbox.height);
				for (fRect r : PlayState.map.getColliders()) {
					if (hitbox.intersects(r)) {
						if (v.y > 0 && pos.y + hitboxBottom > r.y) pos.y = r.y - hitboxBottom;
						else if (v.y < 0 && pos.y + hitboxTop < r.y + 1) pos.y = r.y + 1 - hitboxTop;
					}
				}
			}
		}

		// Update animation
		currentAnimation.tick();
	}

	@Override
	public void render(Graphics g, int ox, int oy) {
		// Get screen position, then draw current animation frame if moving and idle animation frame if not.
		Vec2 screenPos = getState().worldToScreen(pos);
		if (moving)
			Game.drawImage(g, currentAnimation.currentFrame().image(), screenPos.x, screenPos.y, screenSize.x * Tile.GAME_SIZE, screenSize.y * Tile.GAME_SIZE);
		else Game.drawImage(g, currentAnimation.firstFrame().image(), screenPos.x, screenPos.y, screenSize.x * Tile.GAME_SIZE, screenSize.y * Tile.GAME_SIZE);

	}

	@Override
	public void onInteract(Entity e) {}

	/**
	 * Changes the animation to that requested by the key.
	 * 
	 * @param key The name of the animation to be switched to.
	 */
	protected void changeAnimation(String key) {
		if (!animations.containsKey(key)) {
			// Print out error if the key is not in the Map, and don't change anything.
			System.out.println("Animations Hashmap does not contain an Animation with key: " + key + ".");
			return;
		}
		// If key is in the map, switch to it only if that animation is not already the current animation!
		Animation requestedAnimation = animations.get(key);
		if (currentAnimation != requestedAnimation) {
			currentAnimation = animations.get(key);
			currentAnimation.start();
		}
	}

}
