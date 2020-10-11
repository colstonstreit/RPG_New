package Play.TheaterEngine.Cutscenes;

import java.util.ArrayList;
import java.util.HashMap;

import Engine.Game;
import Play.Maps.MapManager.Maps;
import Play.TheaterEngine.GetInputCommand.InputResponse;
import Play.TheaterEngine.Cutscenes.CutsceneManager.Cutscenes;

public abstract class Cutscene {

	protected HashMap<String, ArrayList<String>> questions = new HashMap<String, ArrayList<String>>();
	protected HashMap<String, InputResponse> responses = new HashMap<String, InputResponse>();
	protected HashMap<String, Boolean> flags = new HashMap<String, Boolean>();

	protected Game game;
	protected Cutscenes cutsceneID;
	protected Maps mapID;
	public boolean finished;

	public Cutscene(Game game, Cutscenes cutsceneID, Maps mapID) {
		this.game = game;
		this.cutsceneID = cutsceneID;
		this.mapID = mapID;
	}

	public abstract void init();

	public abstract void tick(double deltaTime);

}
