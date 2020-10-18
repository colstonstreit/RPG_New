package Play.TheaterEngine.Cutscenes;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

import Engine.Game;
import Engine.Tools.Function;
import Engine.Tools.Vec2;
import Play.PlayState;
import Play.Entities.Creature;
import Play.Entities.Creature.Facing;
import Play.Entities.Dynamic;
import Play.Entities.Entity;
import Play.Entities.Items.ItemManager.Items;
import Play.Maps.MapManager.Maps;
import Play.TheaterEngine.Commands.AddEntityCommand;
import Play.TheaterEngine.Commands.BaseCommand;
import Play.TheaterEngine.Commands.FadeOutCommand;
import Play.TheaterEngine.Commands.GetInputCommand;
import Play.TheaterEngine.Commands.GetInputCommand.InputResponse;
import Play.TheaterEngine.Commands.MoveCommand;
import Play.TheaterEngine.Commands.PanCameraCommand;
import Play.TheaterEngine.Commands.ReceiveItemCommand;
import Play.TheaterEngine.Commands.RemoveEntityCommand;
import Play.TheaterEngine.Commands.SetCameraFocusCommand;
import Play.TheaterEngine.Commands.ShowDialogCommand;
import Play.TheaterEngine.Commands.TeleportCommand;
import Play.TheaterEngine.Commands.TheaterEngine;
import Play.TheaterEngine.Commands.TurnCommand;
import Play.TheaterEngine.Commands.WaitCommand;
import Play.TheaterEngine.Commands.ZoomCameraCommand;
import Play.TheaterEngine.Cutscenes.CutsceneManager.Cutscenes;

public abstract class Cutscene {

	protected HashMap<String, ArrayList<String>> questionOptions = new HashMap<String, ArrayList<String>>(); // map of questions
	protected HashMap<String, InputResponse> responses = new HashMap<String, InputResponse>(); // map of responses to questions
	protected HashMap<String, Boolean> flags = new HashMap<String, Boolean>(); // map of flags that can be set

	protected Game game; // The game instance
	protected Cutscenes cutsceneID; // The ID of this cutscene
	protected FadeOutCommand fade; // The Fade Out cue, when things can be set up for after.
	protected boolean addToEngine = true; // Whether or not the commands should be added to the TheaterEngine.
	public boolean finished; // Whether or not the cutscene has finished.

	/**
	 * @param game       The game instance.
	 * @param cutsceneID The ID of this cutscene.
	 */
	public Cutscene(Game game, Cutscenes cutsceneID) {
		this.game = game;
		this.cutsceneID = cutsceneID;
	}

	/**
	 * Initializes the settings underlying the cutscene, such as questions or flags.
	 */
	public abstract void init();

	/**
	 * Updates the cutscene, where flags are checked so certain things can be done with each iteration.
	 */
	public abstract void tick(double deltaTime);

	/**
	 * Finishes the cutscene by setting the finished flag and resetting the entities on the map.
	 * 
	 * @param doFadeOut True if a fadeout should occur, false if you want to do it yourself or something else in the cutscene. If false, the entities will not
	 *                  be reset.
	 */
	public void finish(boolean doFadeOut) {
		if (doFadeOut) {
			fade = doNormalFade(new Function() {

				public void run() { PlayState.refreshEntities(PlayState.map.id); }
			});
		}

		questionOptions.clear();
		responses.clear();
		flags.clear();

		finished = true;
	}

	/**
	 * Sets the flag named flagName to the provided truth value, whether that flag exists currently or not.
	 * 
	 * @param flagName The name of the flag to be set.
	 * @param b        The boolean value it should be set to.
	 */
	public void setFlag(String flagName, boolean b) { flags.put(flagName, b); }

	/**
	 * Adds a question with the given questionName to the questionOption hashMap and links it to a given set of response options, which are passed in as
	 * parameters. It creates the ArrayList from the given response options and also creates a response in the HashMap with the same ID for later
	 * identification.
	 * 
	 * @param questionName    The name of the question to be asked.
	 * @param responseOptions An array of Strings passed in as possible response options to the question.
	 */
	public void addQuestion(String questionName, String... responseOptions) {
		setFlag(questionName, false);
		ArrayList<String> potentialResponses = new ArrayList<String>();
		for (int i = 0; i < responseOptions.length; i++) {
			potentialResponses.add(responseOptions[i]);
		}
		questionOptions.put(questionName, potentialResponses);
		responses.put(questionName, new InputResponse());
	}

	/**
	 * Returns true if the given flag exists and is set to true, and false otherwise.
	 * 
	 * @param flagName The name of the flag to be checked.
	 * @return True if the given flag exists and is set to true, and false otherwise.
	 */
	public boolean flagSet(String flagName) { return flags.containsKey(flagName) && flags.get(flagName); }

	/**
	 * Checks to see if a response has been recorded for the given question. Returns true if there has.
	 * 
	 * @param questionName The name of the question for which you want to know if it's been answered.
	 * @return True if there exists a response with the given name and that it is not the default value of -1.
	 */
	public boolean hasResponse(String questionName) { return responses.containsKey(questionName) && responses.get(questionName).hasReponse(); }

	/**
	 * Returns the selected response (String, not index) to the given question. Could return null if the questionName is not valid.
	 * 
	 * @param questionName The name of the question for which you want to know the selected answer.
	 * @return The string that was chosen, or null if questionName is invalid.
	 */
	public String getSelectedResponse(String questionName) {
		if (!hasResponse(questionName)) return null;
		else return questionOptions.get(questionName).get(responses.get(questionName).getResponse());
	}

	/**
	 * Creates a list of commands that will be run simultaneously made up of the list of commands passed in with commas between them. It then returns the list
	 * it created.
	 * 
	 * @param commands A list of comma-separated commands to be run simultaneously.
	 * @return The ArrayList of commands that was created.
	 */
	public ArrayList<BaseCommand> addSimultaneousCommands(BaseCommand... commands) {
		ArrayList<BaseCommand> commandList = new ArrayList<BaseCommand>();
		for (int i = 0; i < commands.length; i++) {
			commandList.add(commands[i]);
		}
		return addSimultaneousCommands(commandList);
	}

	/**
	 * Creates a list of commands that will be run simultaneously made up of the list of commands passed in with commas between them. It then returns the list
	 * it created.
	 * 
	 * @param commands An ArrayList of commands to be run simultaneously.
	 * @return The ArrayList of commands that was created.
	 */
	public ArrayList<BaseCommand> addSimultaneousCommands(ArrayList<BaseCommand> commands) {
		if (!finished && addToEngine) TheaterEngine.addGroup(commands, false);
		return commands;
	}

	/**
	 * Adds a new ShowDialogCommand to the TheaterEngine with the given dialog.
	 * 
	 * @param dialog The dialog to be said! \n separates different lines.
	 * @return The ShowDialogCommand that was created.
	 */
	public ShowDialogCommand say(String dialog) {
		ShowDialogCommand command = new ShowDialogCommand(game, dialog);
		if (!finished && addToEngine) TheaterEngine.add(command);
		return command;
	}

	/**
	 * Asks a new question via GetInputCommand with the given text and the given question name.
	 * 
	 * @param question     The text of the question to be asked, e.g. "Hi, are you a happy person? Yes or no?"
	 * @param questionName The name of the question for later identification, e.g. "IS_HAPPY?"
	 * @return The GetInputCommand after it was created.
	 */
	public GetInputCommand ask(String question, String questionName) {
		GetInputCommand command = new GetInputCommand(game, question, questionOptions.get(questionName), responses.get(questionName));
		if (!finished && addToEngine) TheaterEngine.add(command);
		return command;
	}

	/**
	 * Adds a new FadeOutCommand with the given parameters, then returns the command.
	 * 
	 * @param fadeOutLength The number of milliseconds spent in the fading out phase
	 * @param holdLength    The number of millseconds spent in the holding phase (holds the same opaque color)
	 * @param fadeInLength  The number of milliseconds spent in the fading back in phase
	 * @param color         The color that should be faded in
	 * @param function      The function to be called when everything is faded out
	 * @return The FadeOutCommand that was created.
	 */
	public FadeOutCommand fadeOut(int fadeOutLength, int holdLength, int fadeInLength, Color c, Function f) {
		FadeOutCommand command = new FadeOutCommand(game, fadeOutLength, holdLength, fadeInLength, c, f);
		if (!finished && addToEngine) TheaterEngine.add(command);
		return command;
	}

	/**
	 * Fades in normally, like what I'd probably do at the beginning of a cutscene.
	 *
	 * @param f The function to be called when the fadeOut is finished.
	 * @return The FadeOutCommand that was created.
	 */
	public FadeOutCommand doNormalFade(Function f) { return fadeOut(500, 500, 500, Color.black, f); }

	/**
	 * Adds a new Wait command to wait the given number of milliseconds.
	 * 
	 * @param msDelay The number of milliseconds to wait.
	 * @return The WaitCommand that was created.
	 */
	public WaitCommand wait(int msDelay) {
		WaitCommand command = new WaitCommand(game, msDelay);
		if (!finished && addToEngine) TheaterEngine.add(command);
		return command;
	}

	/**
	 * @param e                 The entity to be moved
	 * @param newPos            The position the entity should be moved to
	 * @param timeToTake        The amount of time in milliseconds that this movement should take
	 * @param moveThroughThings True if collision should be disregarded during this movement, False if not
	 * @return The MoveCommand that was created.
	 */
	public MoveCommand move(Dynamic e, Vec2 newPos, double timeToTake, boolean moveThroughThings) {
		MoveCommand command = new MoveCommand(game, e, newPos, timeToTake, moveThroughThings);
		if (!finished && addToEngine) TheaterEngine.add(command);
		return command;
	}

	/**
	 * Moves an entity to a new position at a speed dependent on how far the position is. Based off MoveCommand.NORM_MS_PER_TILE.
	 * 
	 * @param e      The entity to be moved.
	 * @param newPos The position the entity should be moved to.
	 * @param speed  The speed in ms per tile that the entity should move.
	 * @return The MoveCommand that was created.
	 */
	public MoveCommand move(Dynamic e, Vec2 newPos, double speed) {
		MoveCommand command = new MoveCommand(game, e, newPos, speed);
		if (!finished && addToEngine) TheaterEngine.add(command);
		return command;
	}

	/**
	 * Teleports the given entity to the specified location. Can do a fade out or a customizable function if desired.
	 * 
	 * @param e        The entity to be teleported.
	 * @param newPos   The new position for the entity.
	 * @param boolean  doFadeOut True if the fade out should occur.
	 * @param function The function to occur during the fade out.
	 * @return The TeleportCommand that was created.
	 */
	public TeleportCommand teleport(Dynamic e, Vec2 newPos, boolean doFadeOut, Function function) {
		TeleportCommand command = new TeleportCommand(game, e, newPos, doFadeOut, function);
		if (!finished && addToEngine) TheaterEngine.add(command);
		return command;
	}

	/**
	 * Teleports the given entity to the specified location on the new map. Can do a fade out or a customizable function if desired.
	 * 
	 * @param e        The entity to be teleported.
	 * @param newPos   The new position for the entity.
	 * @param newMap   The new map ID to be teleported to.
	 * @param boolean  doFadeOut True if the fade out should occur.
	 * @param function The function to occur during the fade out.
	 * @return The TeleportCommand that was created.
	 */
	public TeleportCommand teleport(Dynamic e, Vec2 newPos, Maps newMap, boolean doFadeOut, Function function) {
		TeleportCommand command = new TeleportCommand(game, e, newPos, newMap, doFadeOut, function);
		if (!finished && addToEngine) TheaterEngine.add(command);
		return command;
	}

	/**
	 * Turns the given creature to face the given direction.
	 * 
	 * @param c         The creature to be turned.
	 * @param direction The direction the creature should turn to face.
	 * @return The TurnCommand that was created.
	 */
	public TurnCommand turn(Creature c, Facing direction) {
		TurnCommand command = new TurnCommand(game, c, direction);
		if (!finished && addToEngine) TheaterEngine.add(command);
		return command;
	}

	/**
	 * Pans the camera to where the specified position will be in the middle of the screen and will take the given amount of time.
	 * 
	 * @param targetPos  The target center position in world coordinates to reach.
	 * @param timeToTake The number of milliseconds it should take to pan to the specified location.
	 * @return The PanCameraCommand that was created.
	 */
	public PanCameraCommand panCam(Vec2 targetPos, int timeToTake) {
		PanCameraCommand command = new PanCameraCommand(game, targetPos.x, targetPos.y, timeToTake);
		if (!finished && addToEngine) TheaterEngine.add(command);
		return command;
	}

	/**
	 * Centers the camera on the given entity and sets it to follow the entity smoothly if smoothMovement is true.
	 * 
	 * @param e              The entity to be centered on.
	 * @param smoothMovement Whether or not the camera should smoothly follow the entity after the command is finished.
	 * @return The SetCameraFocusCommand that was created.
	 */
	public SetCameraFocusCommand focusCam(Entity e, boolean smoothMovement) {
		SetCameraFocusCommand command = new SetCameraFocusCommand(game, e, smoothMovement);
		if (!finished && addToEngine) TheaterEngine.add(command);
		return command;
	}

	/**
	 * Pans the camera to the given entity and sets it to follow the entity smoothly if smoothMovement is true.
	 * 
	 * @param e              The entity to be centered on.
	 * @param smoothMovement Whether or not the camera should smoothly follow the entity after the command is finished.
	 * @param timeToTake     The amount of time the command should take in panning over to the entity before focusing on it.
	 * @return The SetCameraFocusCommand that was created.
	 */
	public SetCameraFocusCommand focusCam(Entity e, boolean smoothMovement, int timeToTake) {
		SetCameraFocusCommand command = new SetCameraFocusCommand(game, e, smoothMovement, timeToTake);
		if (!finished && addToEngine) TheaterEngine.add(command);
		return command;
	}

	/**
	 * Gives an item to the specified creature by making them face down towards the camera. If addItemsHere is true it will actually add the item to the
	 * inventory.
	 * 
	 * @param game         The game instance
	 * @param item         The item to be given
	 * @param count        The amount of the item to be given
	 * @param c            The creature that should be turned when receiving the item
	 * @param addItemsHere True if the items should be added to the inventory here
	 * @return The ReceiveItemCommand that was created.
	 */
	public ReceiveItemCommand giveItem(Items item, int count, Creature c, boolean addItemsHere) {
		ReceiveItemCommand command = new ReceiveItemCommand(game, item, count, c, addItemsHere);
		if (!finished && addToEngine) TheaterEngine.add(command);
		return command;
	}

	/**
	 * Adds the given entity to the scene.
	 * 
	 * @param entity The Entity to be added to the scene.
	 * @return The AddEntityCommand that was created.
	 */
	public AddEntityCommand addEntity(Dynamic entity) {
		AddEntityCommand command = new AddEntityCommand(game, entity);
		if (!finished && addToEngine) TheaterEngine.add(command);
		return command;
	}

	/**
	 * Removes the entity matching the given entity (by reference) from the scene.
	 * 
	 * @param entity The entity to be removed.
	 * @return The RemoveEntityCommand that was created.
	 */
	public RemoveEntityCommand removeEntity(Dynamic entity) {
		RemoveEntityCommand command = new RemoveEntityCommand(game, entity);
		if (!finished && addToEngine) TheaterEngine.add(command);
		return command;
	}

	/**
	 * Removes the entity with the given name from the scene.
	 * 
	 * @param name The name of the entity to be removed.
	 * @return The RemoveEntityCommand that was created.
	 */
	public RemoveEntityCommand removeEntity(String name) {
		RemoveEntityCommand command = new RemoveEntityCommand(game, name);
		if (!finished && addToEngine) TheaterEngine.add(command);
		return command;
	}

	/**
	 * Zooms the camera to the given percentage of Tile.NORM_GAME_SIZE in the given number of milliseconds.
	 * 
	 * @param percentage The percentage of Tile.NORM_GAME_SIZE to zoom to (aka, 100% would be the same, 200% would be zoomed in double)
	 * @param msDelay    The number of milliseconds that this zooming action should take
	 * @return The ZoomCameraCommand that was created.
	 */
	public ZoomCameraCommand zoomCam(double percentage, int msDelay) {
		ZoomCameraCommand command = new ZoomCameraCommand(game, percentage, msDelay);
		if (!finished && addToEngine) TheaterEngine.add(command);
		return command;
	}

}
