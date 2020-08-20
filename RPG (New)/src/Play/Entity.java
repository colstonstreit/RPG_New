package Play;

import java.awt.Color;
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
	protected Vec2 size; // size on the screen (in world units | 1 tile = 1 unit)
	protected fRect relativeHitbox; // relative hitbox based on screenSize

	protected final String type; // type of entity (i.e. "Player")

	/**
	 * @param game An instance of the game object
	 * @param type The type of entity (i.e. "Player" or "NPC")
	 */
	public Entity(Game game, String type) {
		this.game = game;
		this.type = type;
		setPos(0, 0);
		setSize(1, 1);
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
	public Vec2 getCenter() { return new Vec2(pos.x + 0.5 * size.x, pos.y + 0.5 * size.y); }

	/**
	 * Returns true if this Entity is on the screen, and false if not.
	 */
	public boolean isOnScreen() {
		Vec2 screenPos = worldToScreen(pos);
		Vec2 screenCornerPos = worldToScreen(pos.add(size));
		return !(screenPos.x > game.getWidth() || screenPos.y > game.getHeight() || screenCornerPos.x < 0 || screenCornerPos.y < 0);
	}

	/**
	 * Returns an fRect containing the Entity's hitbox in world coordinates.
	 */
	public fRect hitbox() {
		return new fRect(pos.x + size.x * relativeHitbox.x, pos.y + size.y * relativeHitbox.y, size.x * relativeHitbox.width, size.y * relativeHitbox.height);
	}

	/**
	 * Sets the size of the entity to the width and height provided, then returns the entity.
	 */
	public Entity setSize(double width, double height) {
		size = new Vec2(width, height);
		return this;
	}

	/**
	 * Sets the position of the entity to the x and y provided, then returns the entity.
	 */
	public Entity setPos(double x, double y) {
		pos = new Vec2(x, y);
		return this;
	}

	/**
	 * Sets both the position and size of the entity to the x, y, width, and height provided, then returns the entity.
	 */
	public Entity setTransform(double x, double y, double width, double height) { return setPos(x, y).setSize(width, height); }

	/**
	 * Converts a vector from world coordinates to screen coordinates, and returns the converted vector.
	 */
	public Vec2 worldToScreen(Vec2 v) { return getState().worldToScreen(v); }

	/**
	 * Converts a vector from screen coordinates to world coordinates, and returns the converted vector.
	 */
	public Vec2 screenToWorld(Vec2 v) { return getState().screenToWorld(v); }

	/**
	 * Converts an fRect from world coordinates to screen coordinates, and returns the converted fRect.
	 */
	public fRect worldToScreen(fRect r) { return getState().worldToScreen(r); }

	/**
	 * Converts an fRect from screen coordinates to world coordinates, and returns the converted fRect.
	 */
	public fRect screenToWorld(fRect r) { return getState().screenToWorld(r); }

	//////////////////////////////////////////////////////////////////////////////////////////

	public static abstract class Dynamic extends Entity {

		public Vec2 v; // velocity vector
		public boolean solidVsDynamic; // whether this entity is solid against dynamic entities or not
		public boolean solidVsStatic; // whether this entity is solid against static world tiles or not

		protected fRect interactableRegion; // relative image coordinates of interact zone for onInteract() purposes

		/**
		 * @param game An instance of the game object
		 * @param type The type of entity (i.e. "Player" or "NPC")
		 */
		public Dynamic(Game game, String type) {
			super(game, type);
			v = new Vec2(0, 0);
			setCollisionType(false, false);
			interactableRegion = new fRect(0, 0, 1, 1);
		}

		public abstract void onInteract(Entity e);

		/**
		 * Returns an fRect containing the Dynamic's interact rectangle in world coordinates.
		 */
		public fRect interactableRegion() {
			return new fRect(pos.x + size.x * interactableRegion.x, pos.y + size.y * interactableRegion.y, size.x * interactableRegion.width,
					size.y * interactableRegion.height);
		}

		/**
		 * Sets the solidVsDynamic and solidVsStatic flags to those passed in, then returns the Dynamic.
		 * 
		 * @param solidVsStatic  Whether or not the Dynamic should be solid against the map
		 * @param solidVsDynamic Whether or not the Dynamic should be solid against other dynamics.
		 */
		public Dynamic setCollisionType(boolean solidVsStatic, boolean solidVsDynamic) {
			this.solidVsStatic = solidVsStatic;
			this.solidVsDynamic = solidVsDynamic;
			return this;
		}

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
				setCollisionType(true, true);
				moving = false;
				facing = Facing.Down;
				setDefaultAnimations(imageName);

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
				Vec2 screenPos = worldToScreen(pos);
				// Draw correct image based on moving flag
				if (moving)
					Game.drawImage(g, currentAnimation.currentFrame().image(), screenPos.x, screenPos.y, size.x * Tile.GAME_SIZE, size.y * Tile.GAME_SIZE);
				else Game.drawImage(g, currentAnimation.firstFrame().image(), screenPos.x, screenPos.y, size.x * Tile.GAME_SIZE, size.y * Tile.GAME_SIZE);
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

		//////////////////////////////////////////////////////////////////////////////////////////

		public static interface Triggerable {

			void run();
		}

		public static class Trigger extends Dynamic {

			protected Triggerable functionToBeRun; // An anonymous function that will be called when the Player passes through it.
			protected boolean active = true; // whether or not the function has been triggered yet
			protected boolean runOnInteract; // whether this will be called upon running into the trigger or if interacting with trigger
			public final String name; // The name of this trigger (for identification purposes)

			public enum WillTrigger { ONCE, FOREVER }; // Enum containing the various types of triggers that can happen
			protected WillTrigger triggerType; // How often the trigger is active

			protected boolean shouldBeDrawn = false; // Whether or not this trigger should be drawn on screen
			protected boolean wasInteractedWith = false; // Whether or not this trigger has been interacted with

			/**
			 * @param game            The instance of the Game object
			 * @param name            The name of the trigger (for identification purposes)
			 * @param runOnInteract   True if the function should be run when the trigger is interacted with; False if should be run upon collision with player
			 * @param triggerType     Either WillTrigger.ONCE if it should only be active once; or WillTrigger.FOREVER if it should always be active
			 * @param functionToBeRun A custom function that will be called at the appropriate time based on other parameters.
			 */
			public Trigger(Game game, String name, boolean runOnInteract, WillTrigger triggerType, Triggerable functionToBeRun) {
				super(game, "Trigger");
				this.name = name;
				this.runOnInteract = runOnInteract;
				this.triggerType = triggerType;
				this.functionToBeRun = functionToBeRun;
			}

			public void onInteract(Entity e) {
				// Run custom function only if the trigger has been interacted with
				if (runOnInteract) {
					wasInteractedWith = true;
					switch (triggerType) {
						case ONCE:
							if (active) {
								active = false;
								if (functionToBeRun != null) functionToBeRun.run();
							}
							break;
						case FOREVER:
							if (functionToBeRun != null) functionToBeRun.run();
							break;
					}
				}
			}

			public void tick(double deltaTime) {
				// If this Trigger runs upon collision with the player or it was interacted with
				if (!runOnInteract || wasInteractedWith) {
					for (Dynamic e : PlayState.entities) {
						if (e == this || !(e instanceof Player)) continue;
						Player p = (Player) e;
						if (!wasInteractedWith && !p.hitbox().intersects(hitbox())) continue;
						switch (triggerType) {
							case ONCE:
								if (active) {
									p.onInteract(this);
									active = false;
									if (functionToBeRun != null) functionToBeRun.run();
								}
								break;
							case FOREVER:
								p.onInteract(this);
								if (functionToBeRun != null) functionToBeRun.run();
								break;
						}
					}
				}
			}

			public void render(Graphics g, int ox, int oy) { if (shouldBeDrawn && active) worldToScreen(hitbox()).draw(g, Color.white); }

			/**
			 * Sets whether or not the Trigger's hitbox should be drawn or not, then returns the Trigger.
			 */
			public Trigger setShouldBeDrawn(boolean b) {
				this.shouldBeDrawn = b;
				return this;
			}

			/**
			 * Sets this trigger's function to be the new Triggerable t.
			 * 
			 * @param t The new Triggerable function that should be called upon interaction.
			 */
			public Trigger setFunction(Triggerable t) {
				this.functionToBeRun = t;
				return this;
			}

			//////////////////////////////////////////////////////////////////////////////////////////

			public static class Teleport extends Trigger {

				private long timer; // measure the time passed before teleporting should happen
				private boolean hasInitiatedFadeOut = false; // Whether or not the fadeout has begun
				private static final int timeBeforeTeleport = 1000; // The amount of time that should pass before teleporting in milliseconds
				private String newMapName; // The name of the map to switch to

				/**
				 * @param game          The instance of the game
				 * @param runOnInteract True if the function should be run when upon interaction; False if should be run upon collision with player
				 * @param name          The name of the teleport (for identification purposes)
				 * @param newPos        The new position to which the player should be teleported
				 */
				public Teleport(Game game, boolean runOnInteract, String name, Vec2 newPos) {
					super(game, name, runOnInteract, WillTrigger.FOREVER, null);
					newMapName = null;
					setFunction(new Triggerable() {

						public void run() {

							// Start fadeout and timer if haven't already
							if (!hasInitiatedFadeOut) {
								TheaterEngine.add(new Command.FadeOut(game, timeBeforeTeleport, 1000, 2000, Color.black));
								timer = System.currentTimeMillis();
								hasInitiatedFadeOut = true;
							}

							// If fadeout is complete, teleport player (to new map if necessary) and reset initiatedFadeout in case it is a repetitive trigger
							if (System.currentTimeMillis() - timer >= timeBeforeTeleport) {
								PlayState.player.setPos(newPos.x, newPos.y);
								PlayState.player.v = new Vec2(0, 0);
								hasInitiatedFadeOut = false;

								// Trigger PlayState to change the map at the end of this tick() cycle
								if (newMapName != null) PlayState.newMapName = newMapName;

								if (triggerType == WillTrigger.ONCE) active = false;
								wasInteractedWith = false;
							} else active = true; // Set active back to true if fadeout isn't complete so the run method keeps getting called
						}

					});
				}

				/**
				 * @param game          The instance of the game
				 * @param runOnInteract True if the function should be run when upon interaction; False if should be run upon collision with player
				 * @param name          The name of the teleport (for identification purposes)
				 * @param newPos        The new position to which the player should be teleported
				 * @param mapName       The name of the map to be switched to
				 */
				public Teleport(Game game, boolean runOnInteract, String name, Vec2 newPos, String mapName) {
					this(game, runOnInteract, name, newPos);
					newMapName = mapName;
				}
			}
		}
	}
}
