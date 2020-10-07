package Play.Entities;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;

import Engine.Animation;
import Engine.AssetManager;
import Engine.AssetManager.CharacterSprites;
import Engine.Game;
import Engine.Sprite;
import Engine.Tools.Vec2;
import Engine.Tools.fRect;
import Play.PlayState;
import Play.Maps.Tile;

public abstract class Creature extends Dynamic {

	protected HashMap<String, Animation> animations = new HashMap<>(); // A hashmap containing the different player animations
	protected Animation currentAnimation; // The current animation to be shown
	protected boolean moving; // Whether or not the player is currently moving.

	public enum Facing { Up, Down, Left, Right };
	protected Facing facing;

	/**
	 * @param game      The instance of the game
	 * @param type      The type of entity (i.e. "Player" or "NPC")
	 * @param spriteName The name of the image that can be fetched from characterImages in Assets
	 */
	public Creature(Game game, String name, CharacterSprites spriteName, Vec2 pos) {
		super(game, name);
		this.pos = pos;
		setCollisionType(true, true);
		moving = false;
		facing = Facing.Down;
		setDefaultAnimations(spriteName);

		// Set Down as default
		changeAnimation("Down");
	}

	/**
	 * Handles moving flag, default animation changes, and collisions.
	 */
	public void tick(double deltaTime) {
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

	/**
	 * Draws the correct animation image to the screen.
	 */
	public void render(Graphics g, int ox, int oy) {
		if (isOnScreen()) {
			Vec2 screenPos = worldToScreen(pos);
			// Draw correct image based on moving flag
			if (moving) Game.drawImage(g, currentAnimation.currentFrame().image(), screenPos.x, screenPos.y, size.x * Tile.GAME_SIZE, size.y * Tile.GAME_SIZE);
			else Game.drawImage(g, currentAnimation.firstFrame().image(), screenPos.x, screenPos.y, size.x * Tile.GAME_SIZE, size.y * Tile.GAME_SIZE);

			if (Entity.showHitboxes) worldToScreen(hitbox()).draw(g, Color.white);

		}
	}

	/**
	 * Sets the default up, down, left, and right animations based on set format, gathered from image imageName (from Assets).
	 * 
	 * @param spriteName The name of the spritesheet that animations should be obtained from (from characterImages in Assets)
	 */
	protected void setDefaultAnimations(CharacterSprites spriteName) {
		Sprite spritesheet = AssetManager.getCharacterSpriteSheet(spriteName);
		animations.put("Down", new Animation(100, spritesheet, new int[][] { { 0 , 0 } , { 0 , 1 } , { 0 , 2 } , { 0 , 1 } }));
		animations.put("Up", new Animation(100, spritesheet, new int[][] { { 1 , 0 } , { 1 , 1 } , { 1 , 2 } , { 1 , 1 } }));
		animations.put("Right", new Animation(100, spritesheet, new int[][] { { 2 , 0 } , { 2 , 1 } , { 2 , 2 } , { 2 , 1 } }));
		animations.put("Left", new Animation(100, spritesheet, new int[][] { { 3 , 0 } , { 3 , 1 } , { 3 , 2 } , { 3 , 1 } }));
	}

	/**
	 * Changes the animation to that requested by the key.
	 * 
	 * @param key The name of the animation to be switched to.
	 */
	public void changeAnimation(String key) {

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

		// Set facing direction if relevant
		for (Facing f : Facing.values()) {
			if (key.equals(f.toString())) facing = f;
		}
	}

	/**
	 * Resolves collisions between this Creature and the surrounding map, as well as other Dynamic Entities within the game.
	 */
	protected void handleCollisions() {

		// Handle collisions in the x direction, static then dynamic
		if (v.x != 0) {
			pos.x += v.x;

			if (solidVsStatic) {
				fRect hitbox = hitbox();
				double hitboxLeftDistance = hitbox.x - pos.x;
				double hitboxRightDistance = hitbox.x + hitbox.width - pos.x;
				for (fRect r : PlayState.map.getColliders()) {
					if (hitbox.intersects(r)) {
						if (v.x > 0 && pos.x + hitboxRightDistance > r.x) pos.x = r.x - hitboxRightDistance;
						else if (v.x < 0 && pos.x + hitboxLeftDistance < r.x + 1) pos.x = r.x + 1 - hitboxLeftDistance;
					}
				}
			}

			if (solidVsDynamic) {
				for (Dynamic e : PlayState.entities) {
					if (e == this || !e.solidVsDynamic) continue;
					fRect hitbox = hitbox();
					double hitboxLeftDistance = hitbox.x - pos.x;
					double hitboxRightDistance = hitbox.x + hitbox.width - pos.x;

					fRect other;
					if (hitbox.intersects(other = e.hitbox())) {
						if (v.x > 0 && pos.x + hitboxRightDistance > other.x) pos.x = other.x - hitboxRightDistance;
						else if (v.x < 0 && pos.x + hitboxLeftDistance < other.x + other.width) pos.x = other.x + other.width - hitboxLeftDistance;
					}
				}
			}
		}

		// Handle collisions in the y direction, static then dynamic
		if (v.y != 0) {
			pos.y += v.y;
			if (solidVsStatic) {
				fRect hitbox = hitbox();
				double hitboxTopDistance = hitbox.y - pos.y;
				double hitboxBottomDistance = hitbox.y + hitbox.height - pos.y;
				for (fRect r : PlayState.map.getColliders()) {
					if (hitbox.intersects(r)) {
						if (v.y > 0 && pos.y + hitboxBottomDistance > r.y) pos.y = r.y - hitboxBottomDistance;
						else if (v.y < 0 && pos.y + hitboxTopDistance < r.y + 1) pos.y = r.y + 1 - hitboxTopDistance;
					}
				}
			}

			if (solidVsDynamic) {
				for (Dynamic e : PlayState.entities) {
					if (e == this || !e.solidVsDynamic) continue;
					fRect hitbox = hitbox();
					double hitboxTopDistance = hitbox.y - pos.y;
					double hitboxBottomDistance = hitbox.y + hitbox.height - pos.y;

					fRect other;
					if (hitbox.intersects(other = e.hitbox())) {
						if (v.y > 0 && pos.y + hitboxBottomDistance > other.y) pos.y = other.y - hitboxBottomDistance;
						else if (v.y < 0 && pos.y + hitboxTopDistance < other.y + other.height) pos.y = other.y + other.height - hitboxTopDistance;
					}
				}
			}
		}
	}

}
