package Play.TheaterEngine.Commands;

import Engine.Game;
import Engine.Tools.Vec2;
import Play.Entities.Dynamic;

public class MoveCommand extends BaseCommand {

	private boolean doNormSpeed = false; // whether or not a regular speed should be used rather than time
	private double speedToMove = 0; // the speed in ms per tile the entity should move at if doNormSpeed is true

	protected Dynamic e; // The entity to be moved
	private Vec2 p; // The position the entity should be moved to
	private Vec2 v; // The velocity vector the entity must follow
	private double time; // The time it should take to move there.
	private double timeElapsed; // The amount of time in milliseconds that have passed by so far.

	private boolean moveThroughThings; // whether or not the entity should ignore collisions.
	private boolean wasSolidVsStatic; // whether or not the entity was originally solid vs static entities
	private boolean wasSolidVsDynamic; // whether or not the entity was originally solid vs dynamic entities.

	/**
	 * @param game              An instance of the game
	 * @param e                 The entity to be moved
	 * @param p                 The position the entity should be moved to
	 * @param time              The amount of time in milliseconds that this movement should take
	 * @param moveThroughThings True if collision should be disregarded during this movement, False if not
	 */
	public MoveCommand(Game game, Dynamic e, Vec2 p, double time, boolean moveThroughThings) {
		super(game);
		this.e = e;
		this.p = p;
		this.time = time;
		this.moveThroughThings = moveThroughThings;
		wasSolidVsStatic = e.solidVsStatic;
		wasSolidVsDynamic = e.solidVsDynamic;
	}

	/**
	 * @param game The game instance.
	 * @param e The entity to be moved.
	 * @param p The new position for the entity.
	 * @param speedToMove The speed in ms per tile that the entity should move.
	 */
	public MoveCommand(Game game, Dynamic e, Vec2 p, double speedToMove) {
		super(game);
		this.e = e;
		this.p = p;
		this.moveThroughThings = true;
		doNormSpeed = true;
		this.speedToMove = speedToMove;
		wasSolidVsStatic = e.solidVsStatic;
		wasSolidVsDynamic = e.solidVsDynamic;
	}

	public void start() {
		// If walking at constant speed, calculate time here.
		if (doNormSpeed) time = p.distanceTo(e.pos) * speedToMove;

		// Get initial velocity (change in position) and set collision flags to false if so desired
		v = p.subtract(e.pos);
		if (moveThroughThings) e.setCollisionType(false, false);
	}

	public void tick(double deltaTime) {
		super.tick(deltaTime);

		// Add the time that has passed
		timeElapsed += deltaTime;

		// If enough time has passed or the entity is very close to its target, end the movement.
		if (timeElapsed >= time || p.subtract(e.pos).getMagnitude() <= 0.05) {
			complete();
			return;
		}

		// If the unit vector of the displacement changes from initial value, object collided: calculate new path and reset timer
		if (!moveThroughThings) {
			Vec2 newV = p.subtract(e.pos);
			if (!newV.norm().equals(v.norm())) {
				v = newV;
				time -= timeElapsed;
				timeElapsed = 0;
			}
		}

		// Set entity's velocity to the correct proportion
		e.v = v.scale(deltaTime / time);
	}

	public void complete() {
		super.complete();
		// Restore initial flags to their old states
		e.pos = p;
		e.v = new Vec2(0, 0);
		e.setCollisionType(wasSolidVsStatic, wasSolidVsDynamic);
	}

}
