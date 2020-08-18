package Engine;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public abstract class Engine extends Canvas implements Runnable {

	private static final long serialVersionUID = 1L;

	private static JFrame frame; // frame object
	private static Thread thread; // main thread
	private static Keys keys; // key input object
	private static Mouse mouse; // mouse input object

	private static Engine game; // game loop object

	@SuppressWarnings("unused")
	private String title; // title of window
	@SuppressWarnings("unused")
	private static int fps = 0; // number of fps
	@SuppressWarnings("unused")
	private static int tps = 0; // number of tps

	public static final double targetTPS = 60.0; // target TPS and FPS

	private boolean isRunning = false; // whether or not the current thread is running

	/**
	 * Engine Constructor, taking in the width, height, and title of the window and initializing the mouse and key objects
	 * 
	 * @param width
	 * @param height
	 * @param title
	 */
	public Engine(int width, int height, String title) {
		this.title = title;
		frame = new JFrame(title);

		keys = new Keys();
		mouse = new Mouse();

		frame.add(this);
		setSize(width, height);

		addKeyListener(keys);
		addMouseListener(mouse);
		addMouseMotionListener(mouse);
		addMouseWheelListener(mouse);

		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.pack();

		frame.setLocationRelativeTo(null);

		thread = new Thread(this, "Game Thread");
	}

	/**
	 * Called to initialize resources in the game.init() function, set frame to visible, and start the thread.
	 */
	protected synchronized void start() {
		isRunning = true;
		game.init();
		frame.setVisible(true);

		requestFocus();
		thread.start();
	}

	/**
	 * Called to join the game thread and exit the program.
	 */
	private synchronized void stop() {
		frame.dispose();
		frame.setVisible(false);
		System.exit(0);
	}

	/**
	 * This function is called when the thread is started and establishes the game loop of the program. It calls the tick and render functions of the child Game
	 * object to the frequency of the targetTPS.
	 */
	public void run() {

		long lastTime = System.nanoTime();
		double nsPerTick = 1000000000 / targetTPS;
		double delta = 0;

		long tickTimer = System.nanoTime();
		long secondTimer = System.currentTimeMillis();

		while (isRunning) {
			long now = System.nanoTime();
			delta += (now - lastTime) / nsPerTick;
			lastTime = now;
			if (delta >= 1) {
				delta--;
				tick((double) (now - tickTimer) / 10000000);
				tickTimer = now;
				render();
			}

			now = System.currentTimeMillis();
			if (now - secondTimer >= 1000) {
				secondTimer += 1000;
				frame.setTitle(title + " | TPS: " + tps + ", FPS: " + fps);
				fps = 0;
				tps = 0;
			}

			try {
				Thread.sleep((long) (1000 / (5 * targetTPS)));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		stop();
	}

	/**
	 * Updates at the frequency of the targetTPS, called with the time elapsed since the last update. Also calls the child Game object's tick method.
	 * 
	 * @param elapsedTime
	 */
	private void tick(double elapsedTime) {
		tps++;
		game.cTick(elapsedTime);
		keys.tick();
		mouse.tick();
	}

	/**
	 * Renders the window as quickly as possible. By default sets background to black and then calls the child Game object's render method.
	 */
	private void render() {
		fps++;

		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(3);
			fps--;
			return;
		}

		Graphics g = bs.getDrawGraphics();
		//////////////////////////////////
		g.setColor(Color.black);
		g.fillRect(0, 0, getWidth(), getHeight());
		game.cRender(g);
		//////////////////////////////////
		g.dispose();
		bs.show();

	}

	/**
	 * Requires the child Game object to have an init() method, which is called from the Engine's start method.
	 */
	public abstract void init();

	/**
	 * Requires the child Game object to have a tick() method, which is called at the frequency of the targetTPS.
	 */
	public abstract void cTick(double elapsedTime);

	/**
	 * Requires the child Game object to have a render() method, which is called as much as possible.
	 */
	public abstract void cRender(Graphics g);

	/**
	 * Resizes the canvas with a certain width and height, resetting the frame's location while it is invisible.
	 * 
	 * @param width
	 * @param height
	 */
	public void resizeCanvas(int width, int height) {
		frame.setVisible(false);
		this.setSize(width, height);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	/**
	 * Sets the window's title.
	 */
	public void setTitle(String s) { this.title = s; }

	/**
	 * Returns true if the key with the given keyCode is currently pressed down.
	 * 
	 * @param keyCode
	 */
	public boolean keyDown(int keyCode) { return keys.pressed(keyCode); }

	/**
	 * Returns true if the key with the specified character (ex. 'a') is currently pressed down. Returns false automatically if c is not a symbol.
	 * 
	 * @param c
	 */
	public boolean keyDown(char c) { return (Character.isAlphabetic(c) || Character.isDigit(c)) ? keys.pressed((int) Character.toUpperCase(c)) : false; }

	/**
	 * Returns true if the key with the given keyCode has just been released.
	 * 
	 * @param keyCode
	 */
	public boolean keyUp(int keyCode) { return keys.released(keyCode); }

	/**
	 * Returns true if the key with the specified character (ex. 'a') has just been released. Returns false automatically if c is not a symbol.
	 */
	public boolean keyUp(char c) { return (Character.isAlphabetic(c) || Character.isDigit(c)) ? keys.released((int) Character.toUpperCase(c)) : false; }

	/**
	 * Returns a Tools.fRect containing the x and y of the mouse position.
	 */
	public Tools.fRect mouseBounds() { return mouse.getMouseBounds(); }

	/**
	 * Returns a Rectangle containing the mouse's final dragged area. Returns null if the mouse has not yet dragged a rectangle.
	 */
	public Rectangle mouseFinalDragged() { return mouse.getFinalDraggedArea(); }

	/**
	 * Returns true if the user has dragged a rectangle across the screen and it hasn't been processed.
	 */
	public boolean mouseHasFinalDragged() { return mouse.hasFinalDraggedArea(); }

	/**
	 * Returns a Rectangle containing the mouse's current dragged area, which may return null if the mouse is not currently dragging a rectangle.
	 */
	public Rectangle mouseCurrentDragged() { return mouse.getCurrentDraggedArea(); }

	/**
	 * Returns true if the mouse button with the given button ID has just been released.
	 * 
	 * @param buttonID 1 if left button, 2 if scroll button, 3 if right button
	 */
	public boolean mouseClicked(int buttonID) { return mouse.clicked(buttonID); }

	/**
	 * Returns true if a mouse button is currently pressed down.
	 */
	public boolean mousePressed() { return mouse.pressed(); }

	/**
	 * Returns true if the mouse is currently being dragged.
	 */
	public boolean mouseDragged() { return mouse.dragged(); }

	/**
	 * Returns a Tools.fRect containing the mouse offsets during dragging (if it's being dragged).
	 */
	public Tools.Vec2 mouseDraggedOffsets() { return mouse.getDraggedOffsets(); }

	/**
	 * Returns true if the mouse has just been zoomed in (scroll up)
	 */
	public boolean mouseZoomedIn() { return mouse.scrolledDir() < 0; }

	/**
	 * Returns true if the mouse has just been zoomed out. (scroll down)
	 */
	public boolean mouseZoomedOut() { return mouse.scrolledDir() > 0; }

	/**
	 * Sets the child Game object whose init, tick, and render methods will be called. This is called in the child's constructor.
	 */
	public void setChild(Engine engine) { game = engine; }

	/**
	 * Returns the window object.
	 */
	public JFrame getFrame() { return frame; }

	/**
	 * Returns a Tools.fRect comprising the entire window.
	 */
	public Tools.fRect getFRect() { return new Tools.fRect(0, 0, getWidth(), getHeight()); }

	/**
	 * Sets isRunning to false, which ends the program.
	 */
	public void endProgram() { isRunning = false; }

	/**
	 * Fills a rectangle with the specified position and size.
	 * 
	 * @param g      Graphics object
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public static void fillRect(Graphics g, double x, double y, double width, double height) {
		g.fillRect((int) round(x, 1), (int) round(y, 1), (int) round(width, 1), (int) round(height, 1));
	}

	/**
	 * Draws the boundary of a rectangle with the specified position, size, and border width.
	 * 
	 * @param g
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param borderWidth
	 */
	public static void drawRect(Graphics g, double x, double y, double width, double height, int borderWidth) {
		if (borderWidth >= width / 2 || borderWidth >= height / 2) fillRect(g, x, y, width, height);
		else {
			if (borderWidth <= 0) return;
			for (int i = 0; i < borderWidth; i++) {
				g.drawRect((int) round(x, 1) + i, (int) round(y, 1) + i, (int) round(width, 1) - 2 * i, (int) round(height, 1) - 2 * i);
			}
		}
	}

	/**
	 * Draws a line from one point to another.
	 * 
	 * @param g
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	public static void drawLine(Graphics g, double x1, double y1, double x2, double y2) {
		g.drawLine((int) round(x1, 1), (int) round(y1, 1), (int) round(x2, 1), (int) round(y2, 1));
	}

	/**
	 * Draws an image at the specified position with the specified dimensions.
	 * 
	 * @param g
	 * @param img
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public static void drawImage(Graphics g, BufferedImage img, double x, double y, double width, double height) {
		g.drawImage(img, (int) round(x, 1), (int) round(y, 1), (int) round(width, 1), (int) round(height, 1), null);
	}

	/**
	 * Draws an image at the specified position with the specified dimensions at a specified angle.
	 * 
	 * @param g
	 * @param image
	 * @param angle
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public static void drawRotatedImage(Graphics g, BufferedImage image, double angle, double x, double y, double width, double height) {
		Graphics2D g2 = (Graphics2D) g;
		g2.rotate(angle, x + width / 2, y + height / 2);
		g2.drawImage(image, (int) round(x, 1), (int) round(y, 1), (int) round(width, 1), (int) round(height, 1), null);
		g2.rotate(-angle, x + width / 2, y + height / 2);
	}

	/**
	 * Draws a string centered within the specified Tools.fRect. If resize is specified to true, the font will be automatically adjusted down to fit.
	 * 
	 * @param g
	 * @param c
	 * @param text
	 * @param r
	 * @param resize
	 */
	public static void drawCenteredString(Graphics g, Color c, String text, Tools.fRect r, boolean resize) {
		Font font = g.getFont();
		FontMetrics fm;
		double width, height;
		do {
			fm = g.getFontMetrics();
			width = fm.stringWidth(text);
			height = fm.getHeight();
			if (width >= r.width || height >= r.height) {
				g.setFont(new Font(g.getFont().getName(), g.getFont().getStyle(), g.getFont().getSize() - 1));
			} else break;
		} while (resize);
		drawString(g, c, text, r.x + (r.width - width) / 2, r.y + (r.height - height) / 2 + fm.getAscent());
		g.setFont(font);
	}

	/**
	 * Draws a string at the specified coordinate values with a certain color.
	 * 
	 * @param g
	 * @param c
	 * @param text
	 * @param x
	 * @param y
	 */
	public static void drawString(Graphics g, Color c, String text, double x, double y) {
		g.setColor(c);
		g.drawString(text, (int) round(x, 1), (int) round(y, 1));
	}

	/**
	 * Returns the specified value rounded to the nearest number that is specified as toNearest. (Round 3 to the nearest Math.PI, for example).
	 * 
	 * @param n
	 * @param toNearest
	 */
	public static double round(double n, double toNearest) { return ((int) ((n + toNearest / 2) / toNearest)) * toNearest; }

	/**
	 * Clamps the given value within two boundaries, min and max in that order.
	 * 
	 * @param n
	 * @param min
	 * @param max
	 */
	public static double clamp(double n, double min, double max) { return n < min ? min : (n > max ? max : n); }

	//////////////////////////////////////////////////////////////////////////

	private class Keys implements KeyListener {

		private boolean[] keyStates = new boolean[256]; // list of current key states
		private boolean[] pastKeyStates = new boolean[256]; // list of past key states

		/**
		 * Sets the key that has been pressed down to true.
		 */
		public void keyPressed(KeyEvent e) { keyStates[e.getKeyCode()] = true; }

		/**
		 * Sets the key that has been released to false.
		 */
		public void keyReleased(KeyEvent e) { keyStates[e.getKeyCode()] = false; }

		/**
		 * Loops through all of the key states and sets the pastKeyStates value to the current key state value.
		 */
		public void tick() {
			for (int i = 0; i < keyStates.length; i++) {
				pastKeyStates[i] = keyStates[i];
			}
		}

		/**
		 * Returns true if the key with the specified keyCode is currently pressed down.
		 * 
		 * @param keyCode
		 */
		public boolean pressed(int keyCode) { return keyStates[keyCode]; }

		/**
		 * Returns true if the key with the specified keyCode has just been released.
		 * 
		 * @param keyCode
		 */
		public boolean released(int keyCode) { return pastKeyStates[keyCode] && !keyStates[keyCode]; }

		public void keyTyped(KeyEvent e) {}
	}

	///////////////////////////////////////////////////////////////////////////

	private class Mouse implements MouseListener, MouseMotionListener, MouseWheelListener {

		private int mouseX, mouseY = 0; // mouse position
		private int startX, startY = 0; // the starting mouse position when held down to be dragged
		private boolean pressed = false; // whether or not the mouse is pressed
		private boolean dragged = false; // whether or not the mouse is being dragged

		private boolean wasClicked = false; // whether or not the mouse was clicked (pressed and released)
		private int buttonClicked = 0; // the button ID of the button that was clicked.

		private double mouseScrolled = 0; // the direction of the mouse wheel's scrolling.

		private Rectangle finalDraggedArea = null; // the Rectangle dragged out by the mouse.

		/**
		 * Resets the mouseScrolled direction and the button ID that was clicked, as well as updates the wasClicked boolean.
		 */
		public void tick() {
			mouseScrolled = 0;
			wasClicked = false;
			buttonClicked = MouseEvent.NOBUTTON;
		}

		/**
		 * Called when the mouse is clicked. Sets the mouse position and identifies which button was clicked.
		 */
		public void mouseClicked(MouseEvent e) {
			mouseX = e.getX();
			mouseY = e.getY();
			wasClicked = true;
			buttonClicked = e.getButton();
		}

		/**
		 * Called whenever a mouse button is pressed. Sets the mouse position and sets the pressed flag to true.
		 */
		public void mousePressed(MouseEvent e) {
			mouseX = e.getX();
			mouseY = e.getY();
			pressed = true;
		}

		/**
		 * Called whenever a mouse button is released. Sets the mouse position and calculates dragged area if the user had been dragging the mouse.
		 */
		public void mouseReleased(MouseEvent e) {
			mouseX = e.getX();
			mouseY = e.getY();
			pressed = false;
			if (dragged) {
				dragged = false;
				finalDraggedArea = getDragged(startX, startY, mouseX, mouseY);
			}
		}

		/**
		 * Called whenever the mouse is dragged while pressed. Sets the dragged flag and updates the starting drag positions if wasn't already being dragged.
		 */
		public void mouseDragged(MouseEvent e) {
			mouseX = e.getX();
			mouseY = e.getY();
			if (!dragged) {
				dragged = true;
				startX = e.getX();
				startY = e.getY();
			}
		}

		/**
		 * Updates the mouse position whenever the mouse is moved.
		 */
		public void mouseMoved(MouseEvent e) {
			mouseX = e.getX();
			mouseY = e.getY();
		}

		/**
		 * Updates the mouseScrolled direction whenever the mouse is scrolled.
		 */
		public void mouseWheelMoved(MouseWheelEvent e) { mouseScrolled = e.getWheelRotation(); }

		/**
		 * Returns a rectangle determined by the two points given.
		 * 
		 * @param x1
		 * @param y1
		 * @param x2
		 * @param y2
		 */
		public Rectangle getDragged(int x1, int y1, int x2, int y2) {
			int width = Math.abs(x2 - x1);
			int height = Math.abs(y2 - y1);
			if (x1 <= x2 && y1 <= y2) {
				return new Rectangle(x1, y1, width, height);
			} else if (x1 >= x2 && y1 >= y2) {
				return new Rectangle(x2, y2, width, height);
			} else if (x1 <= x2 && y1 >= y2) {
				return new Rectangle(x1, y2, width, height);
			} else if (x1 >= x2 && y1 <= y2) {
				return new Rectangle(x2, y1, width, height);
			} else {
				return new Rectangle(x1, y1, 1, 1);
			}
		}

		/**
		 * Returns the dragged area if it exists and then resets the final dragged area to null. Returns null if it doesn't exist.
		 */
		public Rectangle getFinalDraggedArea() {
			if (finalDraggedArea == null) return null;
			Rectangle r = (Rectangle) finalDraggedArea.clone();
			finalDraggedArea = null;
			return r;
		}

		/**
		 * Returns true if the user has dragged a rectangle across the screen and it hasn't been processed.
		 */
		public boolean hasFinalDraggedArea() { return finalDraggedArea != null; }

		/**
		 * Returns the current dragged area if the mouse is currently being dragged. Returns null otherwise.
		 */
		public Rectangle getCurrentDraggedArea() {
			if (dragged) {
				return getDragged(startX, startY, mouseX, mouseY);
			}
			return null;
		}

		/**
		 * Returns a Tools.fRect containing the mouse offsets during dragging (if it's being dragged).
		 */
		public Tools.Vec2 getDraggedOffsets() {
			if (dragged) return new Tools.Vec2(mouseX - startX, mouseY - startY);
			else return null;
		}

		/**
		 * Returns true if the mouse is currently being dragged.
		 */
		public boolean dragged() { return dragged; }

		/**
		 * Returns a Tools.fRect containing the mouse's position.
		 */
		public Tools.fRect getMouseBounds() { return new Tools.fRect(mouseX, mouseY, 1, 1); }

		/**
		 * Returns true if the specified mouse button was clicked.
		 * 
		 * @param buttonID left = 1, scroll wheel = 2, right = 3
		 */
		public boolean clicked(int buttonID) { return wasClicked && buttonClicked == buttonID; } // 1, 2, 3 : left, middle, right

		/**
		 * Returns true if the mouse is currently pressed.
		 */
		public boolean pressed() { return pressed; }

		/**
		 * Returns the value of the mouse's scrolled direction. -1 if mouse was scrolled up, 1 if mouse was scrolled down.
		 */
		public double scrolledDir() {
			return mouseScrolled; // -1 if scroll up, 1 if scroll down
		}

		public void mouseEntered(MouseEvent e) {}

		public void mouseExited(MouseEvent e) {}
	}

}
