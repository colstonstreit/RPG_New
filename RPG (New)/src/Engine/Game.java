package Engine;

import java.awt.Graphics;
import java.awt.Toolkit;

import Editor.EditorState;
import Play.Maps;
import Play.PlayState;
import Play.Quests;
import Play.Tile;

public class Game extends Engine {

	private static final long serialVersionUID = 1L;

	private PlayState playState = null;
	private EditorState editor = null;

	private State currentState;

	public enum States { PLAY, EDITOR };

	public Game(int width, int height, String title) {
		super(width, height, title);
		this.setChild(this);
	}

	public static void main(String[] args) {
		Game game = new Game(640, 640, "Test");
		game.start();
	}

	public void init() {
		Assets.loadAssets();
		Maps.loadMaps(this);
		Quests.loadQuests(this);
		changeState(States.PLAY);
	}

	public void cTick(double deltaTime) {
		Tile.tickTiles();
		currentState.tick(deltaTime);
	}

	public void cRender(Graphics g) { currentState.render(g); }

	public void changeState(States s) {
		switch (s) {
			case PLAY:
				resizeCanvas(640, 640);
				if (playState == null) playState = new PlayState(this);
				currentState = playState;
				setTitle("RPG Game");
				break;
			case EDITOR:
				resizeCanvas((int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 0.99),
						(int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.95));
				if (editor == null) editor = new EditorState(this);
				currentState = editor;
				setTitle("RPG Map Editor");
				break;
		}
	}

	public State currentState() { return currentState; }

}
