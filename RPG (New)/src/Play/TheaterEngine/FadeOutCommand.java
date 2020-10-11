package Play.TheaterEngine;

import java.awt.Color;
import java.awt.Graphics;

import Engine.Game;
import Engine.Tools.Function;

public class FadeOutCommand extends BaseCommand {

	private int fadeOutLength, holdLength, fadeInLength; // The length of time spent in the fade in/out and hold phase
	private final Color color; // The color to faded in and out

	public enum Stage { FADE_OUT, HOLD, FADE_IN }; // The stage of transitions its in
	private Stage stage = Stage.FADE_OUT; // The stage it is currently in

	private double timeElapsed; // The amount of time that has elapsed so far
	private int alpha; // The current transparency of the color to be drawn

	protected Function function; // The function to be called when everything is faded out

	/**
	 * @param game          The instance of the Game object
	 * @param fadeOutLength The number of milliseconds spent in the fading out phase
	 * @param holdLength    The number of millseconds spent in the holding phase (holds the same opaque color)
	 * @param fadeInLength  The number of milliseconds spent in the fading back in phase
	 * @param color         The color that should be faded in
	 * @param function      The function to be called when everything is faded out
	 */
	public FadeOutCommand(Game game, int fadeOutLength, int holdLength, int fadeInLength, Color color, Function function) {
		super(game);
		this.fadeOutLength = fadeOutLength;
		this.holdLength = holdLength;
		this.fadeInLength = fadeInLength;
		this.color = color;
		this.function = function;
	}

	public void tick(double deltaTime) {
		super.tick(deltaTime);
		// Update how much time has gone by
		timeElapsed += deltaTime;
		if (stage == Stage.FADE_OUT) { // If fading out, calculate alpha based on linear interpolation
			alpha = (int) Math.min(255, (timeElapsed / fadeOutLength * 255.0));
			if (timeElapsed >= fadeOutLength) {
				// Move on to the next stage if enough time has passed, run function if exists, and reset how much time has passed for new timer
				stage = Stage.HOLD;
				if (function != null) function.run();
				timeElapsed = 0;
			}
		} else if (stage == Stage.HOLD) { // If holding, keep alpha at 255 (full opacity)
			alpha = 255;
			if (timeElapsed >= holdLength) {
				stage = Stage.FADE_IN;
				timeElapsed = 0;
			}
		} else if (stage == Stage.FADE_IN) { // If fading back in, calculate alpha based on linear interpolation
			alpha = (int) Math.max(0, (255.0 - timeElapsed / fadeInLength * 255.0));
			if (timeElapsed >= fadeInLength) {
				complete();
			}
		}
	}

	public void render(Graphics g, int ox, int oy) {
		g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
		g.fillRect(0, 0, game.getWidth(), game.getHeight());
	}

	public boolean isHolding() { return stage == Stage.HOLD; }
}
