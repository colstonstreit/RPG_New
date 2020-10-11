package Play.TheaterEngine.Cutscenes;

import java.util.ArrayList;

import Engine.Game;
import Play.Maps.MapManager.Maps;
import Play.TheaterEngine.GetInputCommand;
import Play.TheaterEngine.GetInputCommand.InputResponse;
import Play.TheaterEngine.ShowDialogCommand;
import Play.TheaterEngine.TheaterEngine;
import Play.TheaterEngine.Cutscenes.CutsceneManager.Cutscenes;

public class ExampleCutscene extends Cutscene {

	public ExampleCutscene(Game game) { super(game, Cutscenes.EXAMPLE, Maps.COOL_ISLAND); }

	public void init() {
		finished = false;

		flags.put("ASKED_AGE", false);

		ArrayList<String> ageOptions = new ArrayList<String>();
		ageOptions.add("I'm 19!");
		ageOptions.add("I'm old. Who cares?");
		questions.put("AGE", ageOptions);
		responses.put("AGE", new InputResponse());
		flags.put("AGE", false);
	}

	public void tick(double deltaTime) {

		if (!flags.get("ASKED_AGE")) {
			TheaterEngine.add(new ShowDialogCommand(game, "Sup guys!"));
			TheaterEngine.add(new GetInputCommand(game, "How old are you?", questions.get("AGE"), responses.get("AGE")));
			flags.put("ASKED_AGE", true);
		}

		if (!flags.get("AGE")) {
			if (responses.get("AGE").hasReponse()) {
				System.out.println("User said: " + questions.get("AGE").get(responses.get("AGE").getResponse()));
				flags.put("AGE", true);
			}
		} else {
			finished = true;
		}
	}

}
