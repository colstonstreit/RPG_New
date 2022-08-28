package Play.TheaterEngine.Commands;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import Engine.Game;
import Engine.Tools.fRect;

public class ShowDialogCommand extends BaseCommand {

	private static final int numLines = 4;
	protected static final Font dialogFont = new Font("Times New Roman", Font.BOLD, 24);
	private static final int normalDelayBetweenCharacters = 20;
	private static final int fastDelayBetweenCharacters = 1;

	private fRect dialogBox; // The box where dialog should be displayed
	private fRect[] textBoxes = new fRect[numLines];

	private ArrayList<String> linesFromDialog = new ArrayList<String>();
	private ArrayList<String> currentLineBreaks;
	private String[] currentLinesDrawn = new String[numLines];
	private int lineBeingAddedTo = 0;
	private boolean addingText = false;

	private int delay = normalDelayBetweenCharacters;
	private int numCharactersAdded;

	private double timePassed = 0;

	/**
	 * @param game   An instance of the game object
	 * @param dialog The dialog that should be shown on screen. \n separates distinct sets of dialog from each other.
	 */
	public ShowDialogCommand(Game game, String dialog) {
		super(game);

		dialogBox = new fRect(0, 0, game.getWidth(), game.getHeight()).getSubRect(0.05, 0.7, 0.9, 0.25);
		for (int i = 0; i < numLines; i++) {
			textBoxes[i] = dialogBox.getSubRect(0.04, 0.04 + 0.92 / numLines * i, 0.92, 0.92 / numLines);
		}

		String[] linesFromDialogArray = dialog.split("\n");
		for (int i = 0; i < linesFromDialogArray.length; i++) {
			linesFromDialog.add(linesFromDialogArray[i]);
		}
	}

	public void tick(double deltaTime) {
		super.tick(deltaTime);

		// Update how much time has passed
		timePassed += deltaTime;

		// If enough time has passed and the current line being added to should have a line
		if (timePassed >= delay && lineBeingAddedTo < numLines && lineBeingAddedTo < currentLineBreaks.size()) {
			addingText = true;

			// Add how many characters there are
			numCharactersAdded += (int) (timePassed / delay);

			// For each addition, either add the character or switch to the next line if that line is finished
			for (int i = 1; i <= (int) (timePassed / delay); i++) {
				if (numCharactersAdded >= currentLineBreaks.get(lineBeingAddedTo).length()) {
					currentLinesDrawn[lineBeingAddedTo] = currentLineBreaks.get(lineBeingAddedTo);
					lineBeingAddedTo++;
					numCharactersAdded = 0;
					break;
				} else {
					currentLinesDrawn[lineBeingAddedTo] = currentLineBreaks.get(lineBeingAddedTo).substring(0, numCharactersAdded);
				}
			}

			// Subtract the amount of time that's passed
			timePassed -= delay * (int) (timePassed / delay);

			// Set the adding text flag to false if the user needs to confirm they've read everything before continuing
			if (lineBeingAddedTo >= numLines || lineBeingAddedTo == currentLineBreaks.size()) addingText = false;
		}

		// If enter is pressed
		if (game.keyUp(KeyEvent.VK_ENTER)) {
			// If currently adding text, set the quick delay when enter is pressed
			if (addingText) {
				delay = fastDelayBetweenCharacters;
			} else {
				// Otherwise, if there are still more lines to be shown, remove them so four more can start
				if (currentLineBreaks.size() > numLines) {
					for (int i = 0; i < numLines; i++) {
						currentLineBreaks.remove(0);
						currentLinesDrawn[i] = "";
					}
					lineBeingAddedTo = 0;
					timePassed = 0;
					delay = normalDelayBetweenCharacters;
				} else {
					// Otherwise, complete the command if this is the only set of dialogue left; if it isn't, switch to the next set
					if (linesFromDialog.size() == 1) {
						complete();
					} else {
						linesFromDialog.remove(0);
						currentLineBreaks = null;
						for (int i = 0; i < numLines; i++) {
							currentLinesDrawn[i] = "";
						}
						lineBeingAddedTo = 0;
						timePassed = 0;
						delay = normalDelayBetweenCharacters;
					}
				}
			}
		}
	}

	/**
	 * Returns a list of the lines that are spliced out of the line that is passed in.
	 * 
	 * @param line The line to be spliced into several lines that fix in the dialogue box
	 * @param g    The graphics instance needed for FontMetrics to figure out the string width
	 */
	private ArrayList<String> getLines(String line, Graphics g) {
		ArrayList<String> temp = new ArrayList<String>();

		String tempString = line;
		while (!tempString.equals("")) {

			// Set up a temp line and break the remaining string left into discrete words
			String tempLine = "";
			String[] words = tempString.split("\\s+");
			int i = 0;

			// While the addition of another word doesn't increase the string width enough to keep it from fitting, add another word
			while (i < words.length && g.getFontMetrics().stringWidth(tempLine + " " + words[i]) < textBoxes[0].width) {
				tempLine += " " + words[i];
				i++;
			}

			// Add the new line to the list, then get rid of that piece of the dialog for the next iteration
			temp.add(tempLine.trim());
			tempString = tempString.substring(tempLine.length() - 1).trim();
		}

		return temp;
	}

	public void render(Graphics g, int ox, int oy) {

		// Fill and draw before drawing text on top
		dialogBox.fill(g, new Color(0, 0, 0, 150));
		dialogBox.draw(g, Color.white);
		g.setFont(dialogFont);
		g.setColor(Color.white);

		if (currentLineBreaks == null) currentLineBreaks = getLines(linesFromDialog.get(0), g);

		for (int i = 0; i < numLines; i++) {
			g.drawString((currentLinesDrawn[i] == null) ? "" : currentLinesDrawn[i], (int) textBoxes[i].x,
					(int) (textBoxes[i].y + g.getFontMetrics().getAscent()));
		}

	}

}
