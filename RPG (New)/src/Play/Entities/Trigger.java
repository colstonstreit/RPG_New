package Play.Entities;

import java.awt.Color;
import java.awt.Graphics;

import Engine.Game;
import Engine.Tools.Function;
import Play.PlayState;
import Play.TheaterEngine.Commands.TheaterEngine;

public class Trigger extends Dynamic {

	protected Function functionToBeRun; // An anonymous function that will be called when the Player passes through it.
	protected boolean active = true; // whether or not the function has been triggered yet
	protected boolean runOnInteract; // whether this will be called upon running into the trigger or if interacting with trigger

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
	public Trigger(Game game, String name, boolean runOnInteract, WillTrigger triggerType, Function functionToBeRun) {
		super(game, name);
		this.runOnInteract = runOnInteract;
		this.triggerType = triggerType;
		this.functionToBeRun = functionToBeRun;
		this.setCollisionType(true, runOnInteract);
	}

	/**
	 * Runs upon the entity interacting with this (explicitly through enter button, for example).
	 */
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

	/**
	 * Checks for collision with player and runs a certain function if it should.
	 */
	public void tick(double deltaTime) {
		// Trigger runs upon collision with the player if TheaterEngine not in control.
		if (!runOnInteract && !TheaterEngine.hasCommand()) {
			for (Dynamic e : PlayState.entities) {
				if (e == this || !(e instanceof Player)) continue;
				Player p = (Player) e;
				if (!p.hitbox().intersects(hitbox())) continue;
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
	public Trigger setFunction(Function t) {
		this.functionToBeRun = t;
		return this;
	}

}
