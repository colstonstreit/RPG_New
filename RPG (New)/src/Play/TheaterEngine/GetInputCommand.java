package Play.TheaterEngine;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import Engine.Game;
import Engine.Tools.fRect;

public class GetInputCommand extends ShowDialogCommand {

	private final ArrayList<String> possibleResponses; // A list of possible responses to the question
	private InputResponse responseIndex; // The response index to set (an object since it can't return it: still can set the memory like a pointer!)
	private int currentIndex = 0; // The currently selected response

	private boolean finishedTextBox = false; // Whether or not the ShowDialog command has completed yet

	private fRect optionBox; // The box containing all of the optiosn to choose from

	/**
	 * @param game              The game instance
	 * @param question          The question to be asked
	 * @param possibleResponses An ArrayList<String> of the possible responses one could return
	 * @param response          The InputResponse object that the chosen response should be saved under
	 */
	public GetInputCommand(Game game, String question, ArrayList<String> possibleResponses, InputResponse response) {
		super(game, question);
		this.possibleResponses = possibleResponses;
		this.responseIndex = response;
	}

	public void tick(double deltaTime) {
		
		// Update the dialog box
		super.tick(deltaTime);
		
		// Cycle through options if they are shown
		if (finishedTextBox) {
			if (game.keyUp('w') || game.keyUp('a')) currentIndex = (currentIndex + possibleResponses.size() - 1) % possibleResponses.size();
			else if (game.keyUp('s') || game.keyUp('d')) currentIndex = (currentIndex + 1) % possibleResponses.size();
		}
	}

	public void render(Graphics g, int ox, int oy) {

		// Draw the question dialog box, then return if that commmand hasn't finished yet
		super.render(g, ox, oy);
		if (!finishedTextBox) return;

		// Generate the option box if it hasn't been generated already (needs the fontMetrics object in graphics), then draw it
		if (optionBox == null) {
			g.setFont(dialogFont);
			// Calculate the maximum string width so the box can be dynamically sized
			int maxWidth = Integer.MIN_VALUE;
			for (String s : possibleResponses) {
				maxWidth = Math.max(maxWidth, g.getFontMetrics().stringWidth(s));
			}
			optionBox = new fRect(0.95 * game.getWidth() - maxWidth - 0.02 * game.getWidth(),
					0.65 * game.getHeight() - possibleResponses.size() * 0.05 * game.getHeight(), maxWidth + 0.02 * game.getWidth(),
					possibleResponses.size() * 0.05 * game.getHeight());
		}

		optionBox.fill(g, new Color(0, 0, 0, 150));
		optionBox.draw(g, Color.white);

		// For each option, construct the box it should fit into and then write the option in yellow if that's selected, and white if not
		fRect tempOptionBox;
		for (int i = 0, n = possibleResponses.size(); i < n; i++) {
			tempOptionBox = optionBox.getSubRect(0, 0 + i * 1.0 / n, 1, 1.0 / n);
			if (i == currentIndex) Game.drawCenteredString(g, Color.yellow, possibleResponses.get(i), tempOptionBox, false);
			else Game.drawCenteredString(g, Color.white, possibleResponses.get(i), tempOptionBox, false);
		}

	}

	public void complete() {
		if (!finishedTextBox) {
			// Cue the options to show once the parent ShowDialog object calls the complete() function
			finishedTextBox = true;
		} else {
			// Now that the options are shown, save the selected index and complete for real
			responseIndex.response = currentIndex;
			responseIndex.beenShared = false;
			super.complete();
		}
	}

	public static class InputResponse {

		private int response = -1; // The response index to the question
		private boolean beenShared = false; // Whether or not the response has been looked at yet

		public int getResponse() {
			beenShared = true;
			return response;
		}

		public boolean hasReponse() { return response != -1; }

		public boolean hasBeenShared() { return beenShared; }
	}

}
