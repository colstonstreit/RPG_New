package Engine;

public class Animation {

	private long lastTime; // the last time the animation was updated
	private int msDelay; // the delay between each frame
	private Sprite[] frames; // an array of images for the animation
	private int currentFrame; // index of currentFrame

	/**
	 * @param msDelay     The delay in milliseconds between each frame
	 * @param spritesheet The spritesheet containing the images the frames are on
	 * @param frames      An int[][] consisting of tile coordinates {x, y} of frames on the spreadsheet
	 */
	public Animation(int msDelay, Sprite spritesheet, int[][] frames) {
		this.msDelay = msDelay;
		this.frames = new Sprite[frames.length];
		for (int i = 0; i < frames.length; i++) {
			this.frames[i] = spritesheet.crop(frames[i][0], frames[i][1], 1, 1);
		}
		start();
	}

	/**
	 * Resets/starts the animation.
	 */
	public void start() {
		lastTime = System.currentTimeMillis();
		currentFrame = 0;
	}

	public void tick() {
		// Only change the frame if enough time has passed.
		if (System.currentTimeMillis() - lastTime >= msDelay) {
			lastTime = System.currentTimeMillis();
			currentFrame = (currentFrame + 1) % frames.length;
		}
	}

	/**
	 * Returns the current frame.
	 */
	public Sprite currentFrame() { return frames[currentFrame]; }

	/**
	 * Returns the first frame of the animation (perhaps the idle animation).
	 */
	public Sprite firstFrame() { return frames[0]; }

}
