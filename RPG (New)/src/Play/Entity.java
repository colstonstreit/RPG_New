package Play;

import java.awt.Graphics;
import java.util.HashMap;

import Engine.Animation;
import Engine.Assets;
import Engine.Game;
import Engine.Sprite;
import Engine.State;
import Engine.Tools.Vec2;
import Engine.Tools.fRect;

public abstract class Entity {

	protected final Game game; // instance of the game

	public Vec2 pos; // position on the screen (in world units)
	public Vec2 screenSize; // size on the screen (in world units | 1 tile = 1 unit)
	public fRect relativeHitbox; // relative hitbox based on screenSize

	protected final String type; // type of entity (i.e. "Player")

	/**
	 * @param game An instance of the game object
	 * @param type The type of entity (i.e. "Player" or "NPC")
	 */
	public Entity(Game game, String type) {
		this.game = game;
		this.type = type;
		pos = new Vec2(0, 0);
		screenSize = new Vec2(1, 1);
		relativeHitbox = new fRect(0, 0, 1, 1);
	}

	public abstract void tick(double deltaTime);

	public abstract void render(Graphics g, int ox, int oy);

	/**
	 * Returns the game's current State.
	 */
	protected State getState() { return game.currentState(); }

	/**
	 * Returns a Vec2 representing the center of the entity in world space
	 */
	public Vec2 getCenter() { return new Vec2(pos.x + 0.5 * screenSize.x, pos.y + 0.5 * screenSize.y); }

	/**
	 * Returns an fRect containing the Entity's hitbox in world coordinates.
	 */
	public fRect hitbox() {
		return new fRect(pos.x + screenSize.x * relativeHitbox.x, pos.y + screenSize.y * relativeHitbox.y, screenSize.x * relativeHitbox.width,
				screenSize.y * relativeHitbox.height);
	}

	//////////////////////////////////////////////////////////////////////////////////////////

	public static abstract class Dynamic extends Entity {

		public Vec2 v; // velocity vector
		public boolean solidVsDynamic; // whether this entity is solid against dynamic entities or not
		public boolean solidVsStatic; // whether this entity is solid against static world tiles or not

		/**
		 * @param game An instance of the game object
		 * @param type The type of entity (i.e. "Player" or "NPC")
		 */
		public Dynamic(Game game, String type) {
			super(game, type);
			v = new Vec2(0, 0);
			solidVsDynamic = false;
			solidVsStatic = false;
		}

		public abstract void onInteract(Entity e);

		//////////////////////////////////////////////////////////////////////////////////////////

		public static abstract class Creature extends Dynamic {

			protected HashMap<String, Animation> animations = new HashMap<>(); // A hashmap containing the different player animations
			protected Animation currentAnimation; // The current animation to be shown
			protected boolean moving; // Whether or not the player is currently moving.

			protected enum Facing { Up, Down, Left, Right };
			protected Facing facing;

			/**
			 * @param game      The instance of the game
			 * @param type      The type of entity (i.e. "Player" or "NPC")
			 * @param imageName The name of the image that can be fetched from characterImages in Assets
			 */
			public Creature(Game game, String type, String imageName, Vec2 pos) {
				super(game, type);
				this.pos = pos;
				solidVsStatic = true;
				solidVsDynamic = true;
				moving = false;
				facing = Facing.Down;
				setDefaultAnimations(imageName);

				// Set Down as default
				changeAnimation("Down");
			}

			/**
			 * Sets the default up, down, left, and right animations based on set format, gathered from image imageName (from Assets).
			 * 
			 * @param imageName The name of the spritesheet that animations should be obtained from (from characterImages in Assets)
			 */
			protected void setDefaultAnimations(String imageName) {
				Sprite spritesheet = Assets.getCharacterSpriteSheet(imageName);
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

	}

}
