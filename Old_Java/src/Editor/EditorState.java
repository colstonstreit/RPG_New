package Editor;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import Engine.AssetManager;
import Engine.Game;
import Engine.Sprite;
import Engine.State;
import Engine.Tools;
import Engine.Tools.Vec2;
import Engine.Tools.fRect;
import Play.Maps.Tile;

public class EditorState extends State {

	// TILE DRAW SIZES
	public static int tSize = 32; // size that tiles should be rendered
	private static final int tSizeSelectionArea = 64; // size that tiles in selection menu should be drawn

	// IMPORTANT COLORS
	private static final Color onButtonColor = new Color(0, 255, 0, 220); // color of buttons that are "on"
	private static final Color offButtonColor = new Color(255, 0, 0, 220); // color of buttons that are "off"
	private static final Color genericButtonColor = new Color(180, 180, 180, 180); // the button's generic (default) color
	private static final Color highlightButtonColor = new Color(255, 255, 0, 220); // color of buttons that are highlighted or different (yellow)

	// IMPORTANT REGIONS
	private final fRect tileArea = game.getFRect().getSubRect(0, 0.8, 1, 0.2); // Rectangle containing where the tile selection is drawn
	private final Rectangle tileSpace = tileArea.getSubRect(0.005, 0.04, 0.99, 0.92).cast(); // space within the tileArea where tiles actually drawn
	private final fRect buttonArea = game.getFRect().getSubRect(0.8, 0, 0.2, 1); // area within which buttons are placed
	private final fRect layerButtonArea = buttonArea.getSubRect(0.1, 0.02, 0.8, 0.13); // area where layer buttons are placed

	// IMPORTANT FLAGS
	public static boolean pressingButton = false; // whether or not the user has pressed a button on this cycle of mouse hovering (and pressing)
	public static boolean drawingGrid = true; // whether or not the map should draw the white tile grid
	public static boolean deleting = false; // whether or not the editor is in delete mode

	// TILE AREA STUFF
	private static ArrayList<fRect> tileRects = new ArrayList<fRect>(); // list of rectangles in tile area where tiles should be drawn
	public static int selectedTileIndex = 0; // the index of the currently selected tile (same as in Assets and in Tile)

	// ACTION LIST
	public enum Action { SetMove, SetSelect, SetBrush, FillRect, AddColumn, RemoveColumn, AddRow, RemoveRow, SetSolid; } // enum of different potential actions
	public static Action currentAction = Action.SetMove; // current action (is moving the map by mouse by default)

	// ADD/REMOVE ROW/COLUMN STUFF
	public static final int sizeChangingDelay = 150; // delay between row/column additions/removals
	public static long sizeChangingTimer = System.currentTimeMillis(); // timer to keep track of row/colum additions/removals

	// SET_SOLID STUFF
	public static final int solidAddingDelay = 500; // delay between actions of making certain map tiles solid
	public static long solidAddingTimer = System.currentTimeMillis(); // timer to keep track of actions making certain map tiles solid
	public static int lastTXChanged = -1, lastTYChanged = -1; // the coordinates of the last tile that was made solid

	// MOVING STUFF
	private static int ox = 100, oy = 100; // the position at which the map is drawn
	private static int lastOX = 0, lastOY = 0; // the last x and y offsets before the mouse started being dragged

	// LAYER STUFF
	public static int selectedLayer = 0; // the index of the currently selected layer
	public static final int numLayersAllowed = 3; // how many layers the map is allowed to have
	public static boolean[] layerBools = new boolean[numLayersAllowed]; // a list of booleans determining whether or not each layer should be drawn
	private Button[] layerButtons; // a list of the layer buttons for easy access

	// FILL_RECT, SET_SELECT STUFF
	private static fRect dragged = null; // the currently dragged rectangle (bound to tile grid)
	private static boolean firstPressed = true; // whether or not this is the first time the mouse has been pressed (for beginning position)
	private static boolean hasDragged = false; // whether or not there has been a selection made
	private static Vec2 firstCorner = null; // the first corner of what is dragged
	private static final int moveSelectionDelay = 75; // delay in every move of the selection area by mouse
	public static long moveSelectionTimer = System.currentTimeMillis(); // timer for move selection delay
	private static MapData tempMapData; // stores the map data within the selection area (for copying and such)

	// UNDO/REDO STUFF
	public static LinkedList<MapData> previousStates = new LinkedList<MapData>(); // a list of previous states that undo can get you to
	public static LinkedList<MapData> nextStates = new LinkedList<MapData>(); // a list of undone states to go back to

	// MAP AND BUTTON LIST
	private static EditorTileMap map = null; // the map that is being made
	private static ArrayList<Button> buttons = new ArrayList<Button>(); // the list of buttons for the GUI to access

	public EditorState(Game game) {
		super(game);

		// Create the map
		map = new EditorTileMap(game, 20, 20);
		addMapState();

		// Create the tileRects where each individual tile of Tile.tiles() will be rendered for selection area
		int numTilesWide = Math.max(1, tileSpace.width / (tSizeSelectionArea + 1)); // max to avoid division by zero
		Sprite[] tiles = AssetManager.getTileSprites();
		for (int i = 0; i < tiles.length; i++) {
			if (tiles[i] == null) break;
			int indexModNumWide = i % numTilesWide;
			tileRects.add(new fRect(tileSpace.x + indexModNumWide * (tSizeSelectionArea + 1), tileSpace.y + (tSizeSelectionArea + 1) * (i / numTilesWide),
					tSizeSelectionArea, tSizeSelectionArea));
		}

		// Create generic buttons
		addButton("Set Gridlines", "SetGrid").setColor(onButtonColor);
		addButton("Set Delete", "SetDelete").setColor(offButtonColor);
		addButton("Set Moving", "SetMove");
		addButton("Set Brush", "SetBrush");
		addButton("Set Solid", "SetSolid");
		addButton("Set Select", "SetSelect");
		addButton("Add Column", "AddColumn");
		addButton("Remove Column", "RemoveColumn");
		addButton("Add Row", "AddRow");
		addButton("Remove Row", "RemoveRow");
		addButton("Fill Rectangle", "FillRect");
		addButton("Fill Layer", "FillLayer");
		addButton("Zoom In", "ZoomIn");
		addButton("Zoom Out", "ZoomOut");
		addButton("Undo", "Undo");
		addButton("Redo", "Redo");
		addButton("New Map", "NewMap");
		addButton("Save Map", "SaveMap");
		addButton("Load Map", "LoadMap");
		addButton("Switch to Game", "SwitchToGame");

		// Create layer menu
		layerButtons = new Button[numLayersAllowed];
		for (int i = 1; i <= layerButtons.length; i++) {
			layerButtons[i - 1] = new Button("Layer " + i, "SetLayer" + i, this, layerButtonArea.getSubRect(0.5, 0.33 * (i - 1), 0.5, 0.33));
			if (i - 1 == selectedLayer) {
				layerButtons[i - 1].setColor(onButtonColor);
				layerBools[i - 1] = true;
			} else layerButtons[i - 1].setColor(offButtonColor);
			layerButtons[i - 1].setVisible(i - 1 < currentNumLayers());
		}
		new Button("Selected: 1", "SelectedLayer", this, layerButtonArea.getSubRect(0, 0, 0.5, 0.33)).setColor(highlightButtonColor);
		new Button("+", "AddLayer", this, layerButtonArea.getSubRect(0, 0.33, 0.5, 0.33));
		new Button("-", "RemoveLayer", this, layerButtonArea.getSubRect(0, 0.66, 0.5, 0.33));

		setAction(Action.SetBrush);
	}

	/**
	 * Adds a button with text=text and id=id in specified location based on the number of buttons already existing.
	 */
	private Button addButton(String text, String id) {
		int i = buttons.size();
		return new Button(text, id, this, buttonArea.getSubRect(0.05 + 0.5 * (i % 2), 0.17 + 0.05 * (int) (i / 2), 0.4, 0.04));
	}

	/**
	 * Adds a new MapData containing the information of the current map state to the undo list and clears the redo list if necessary.
	 */
	public static void addMapState() {
		// Get info from map
		MapData d = map.getMapSelection(new fRect(0, 0, map.numWide(), map.numTall()));
		// Push new state, but then if it's identical to the previous state, remove it again
		previousStates.push(d);
		if (previousStates.size() > 1 && d.equals(previousStates.get(1))) previousStates.pop();
		// Clear the redo list so there's not an infinite back and forth of undo/redo
		else nextStates.clear();
	}

	/**
	 * Handles the events that should occur when the Button b is pressed based on the button's ID.
	 */
	public void handlePress(Button b) {
		switch (b.id) {
			case "Undo": //////////////////// Revert to earlier state ////////////////////
				if (previousStates.size() > 1) {
					nextStates.push(previousStates.pop());
					map.revert(previousStates.getFirst());
				}
				break;
			case "Redo": //////////////////// Redo stuff ////////////////////
				if (nextStates.size() >= 1) {
					// Get mapData from redo list, push it onto undo list, and then revert the map
					MapData m = nextStates.pop();
					previousStates.push(m);
					map.revert(m);
				}
				break;
			case "SetMove": //////////////////// Change Moving Flag ////////////////////
				setAction(Action.SetMove);
				break;
			case "SetBrush": //////////////////// Change Brush Flag ////////////////////
				if (!Action.SetBrush.equals(currentAction)) setAction(Action.SetBrush);
				break;
			case "SetSelect": //////////////////// Change Brush Flag ////////////////////
				if (!Action.SetSelect.equals(currentAction)) setAction(Action.SetSelect);
				break;
			case "SetDelete": //////////////////// Change Delete Flag ////////////////////
				deleting = !deleting;
				selectedTileIndex = deleting ? -1 : 0;
				b.setColor(deleting ? onButtonColor : offButtonColor);
				if (!Action.SetBrush.equals(currentAction)) setAction(Action.SetBrush);
				break;
			case "SetGrid": //////////////////// Change Setting Grid Flag ////////////////////
				drawingGrid = !drawingGrid;
				b.setColor(drawingGrid ? onButtonColor : offButtonColor);
				break;
			case "FillRect": //////////////////// Fill a Rectangle ////////////////////
				setAction(Action.FillRect);
				break;
			case "FillLayer": //////////////////// Fill the Selected Layer ////////////////////
				if (JOptionPane.showConfirmDialog(null, "Are you sure you want to fill this layer?") == JOptionPane.YES_OPTION) map.fillLayer();
				break;
			case "SaveMap": //////////////////// Save Map to File ////////////////////
				JFileChooser jfc = new JFileChooser();
				jfc.setCurrentDirectory(new File(
						"C:/Users/colst/git/RPG_New/RPG (New)/res/maps/"));
				jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				jfc.setFileFilter(new FileNameExtensionFilter("map files (*.map)", "map"));
				int option = jfc.showSaveDialog(null);
				if (option == JFileChooser.APPROVE_OPTION && jfc.getSelectedFile() != null) {
					File file = jfc.getSelectedFile();
					if (file.exists()) map.save(file.getPath());
					else {
						if (!file.getPath().endsWith(".map")) file = new File(file.getPath() + ".map");
						map.save(file.getPath());
					}
				}
				break;
			case "LoadMap": //////////////////// Load Map to File ////////////////////
				jfc = new JFileChooser();
				jfc.setCurrentDirectory(new File(
						"C:/Users/colst/git/RPG_New/RPG (New)/res/maps/"));
				jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				jfc.setFileFilter(new FileNameExtensionFilter("map files (*.map)", "map"));
				option = jfc.showOpenDialog(null);
				if (option == JFileChooser.APPROVE_OPTION && jfc.getSelectedFile() != null && jfc.getSelectedFile().exists()) {
					map.load(jfc.getSelectedFile().getPath(), Tools.ResourceLoader.LOAD_FILE);
					selectedLayer = currentNumLayers() - 1;
					getButtonById("SelectedLayer").setText("Selected Layer: " + (selectedLayer + 1));
					for (int i = 0; i < layerButtons.length; i++) {
						if (i <= selectedLayer) {
							layerButtons[i].setColor(onButtonColor).setVisible(true);
							layerBools[i] = true;
						} else {
							layerButtons[i].setColor(offButtonColor).setVisible(false);
							layerBools[i] = false;
						}
					}
					ox = 100;
					oy = 100;
				}
				previousStates.clear();
				addMapState();
				break;
			case "NewMap": //////////////////// Create Blank Map ////////////////////
				// Ask for input
				JPanel getInput = new JPanel(new GridLayout(-1, 1));
				JLabel widthLabel = new JLabel("How many tiles wide should the map be?");
				JTextField widthText = new JTextField();
				JLabel heightLabel = new JLabel("How many tiles tall should the map be?");
				JTextField heightText = new JTextField();
				getInput.add(widthLabel);
				getInput.add(widthText);
				getInput.add(heightLabel);
				getInput.add(heightText);
				if (JOptionPane.showConfirmDialog(null, getInput, "New Map Settings!", JOptionPane.OK_OPTION) == JOptionPane.YES_OPTION) {
					if (widthText.getText().matches("\\d+") && heightText.getText().matches("\\d+")) {
						// If both entries are integers, make the new map
						map = new EditorTileMap(game, Integer.parseInt(widthText.getText()), Integer.parseInt(heightText.getText()));
						selectedLayer = 0;
						getButtonById("SelectedLayer").setText("Selected Layer: " + (selectedLayer + 1));
						for (int i = 0; i < layerButtons.length; i++) {
							if (i <= selectedLayer) {
								layerButtons[i].setColor(onButtonColor).setVisible(true);
								layerBools[i] = true;
							} else {
								layerButtons[i].setColor(offButtonColor).setVisible(false);
								layerBools[i] = false;
							}
						}
						ox = 100;
						oy = 100;
					} else JOptionPane.showMessageDialog(null, "You must enter two integers!");
				}
				previousStates.clear();
				addMapState();
				break;
			case "ZoomIn": //////////////////// Make Tiles Bigger (Zoom In) ////////////////////
				tSize = (tSize + 4 > 48) ? 48 : tSize + 4;
				break;
			case "ZoomOut": //////////////////// Make Tiles Smaller (Zoom Out) ////////////////////
				tSize = (tSize - 4 < 4) ? 4 : tSize - 4;
				break;
			case "SelectedLayer": //////////////////// Cycle Selected Layer, Visibility ////////////////////
				selectedLayer = (selectedLayer + 1) % currentNumLayers();
				b.setText("Selected: " + (selectedLayer + 1));
				for (int i = 0; i < layerButtons.length; i++) {
					layerButtons[i].setColor((i <= selectedLayer) ? onButtonColor : offButtonColor);
					layerBools[i] = i <= selectedLayer;
				}
				break;
			case "SetLayer1": //////////////////// Change Visibility Flag Layer 1 ////////////////////
				layerBools[0] = !layerBools[0];
				b.setColor(layerBools[0] ? onButtonColor : offButtonColor);
				break;
			case "SetLayer2": //////////////////// Change Visibility Flag Layer 2 ////////////////////
				layerBools[1] = !layerBools[1];
				b.setColor(layerBools[1] ? onButtonColor : offButtonColor);
				break;
			case "SetLayer3": //////////////////// Change Visibility Flag Layer 3 ////////////////////
				layerBools[2] = !layerBools[2];
				b.setColor(layerBools[2] ? onButtonColor : offButtonColor);
				break;
			case "AddLayer": //////////////////// Add a New Layer ////////////////////
				map.changeLayers(1);
				selectedLayer = currentNumLayers() - 1;
				getButtonById("SelectedLayer").setText("Selected: " + (selectedLayer + 1));
				for (int i = 0; i < layerButtons.length; i++) {
					layerButtons[i].setColor((i <= selectedLayer) ? onButtonColor : offButtonColor).setVisible(i <= selectedLayer);
					layerBools[i] = i <= selectedLayer;
				}
				break;
			case "RemoveLayer": //////////////////// Remove a New Layer ////////////////////
				map.changeLayers(-1);
				selectedLayer = currentNumLayers() - 1;
				getButtonById("SelectedLayer").setText("Selected: " + (selectedLayer + 1));
				for (int i = 0; i < layerButtons.length; i++) {
					layerButtons[i].setColor((i <= selectedLayer) ? onButtonColor : offButtonColor).setVisible(i <= selectedLayer);
					layerBools[i] = i <= selectedLayer;
				}
				break;
			case "AddColumn": //////////////////// Add a Column ////////////////////
				setAction(Action.AddColumn);
				break;
			case "RemoveColumn": //////////////////// Remove a Column ////////////////////
				setAction(Action.RemoveColumn);
				break;
			case "AddRow": //////////////////// Add a Row ////////////////////
				setAction(Action.AddRow);
				break;
			case "RemoveRow": //////////////////// Remove a Row ////////////////////
				setAction(Action.RemoveRow);
				break;
			case "SetSolid":
				setAction(Action.SetSolid);
				break;
			case "SwitchToGame": //////////////////// Switch to Game State ////////////////////
				game.changeState(Game.States.PLAY);
				break;
		}
		// Set pressing button so tiles can't be edited
		pressingButton = true;
	}

	public void tick(double deltaTime) {

		// Check to see if any buttons have been pressed
		for (Button b : buttons)
			b.tick();

		// Check to see if any of the tiles in the selection area have been clicked, and set selectedTileIndex accordingly
		for (int i = 0; i < tileRects.size(); i++) {
			Tools.fRect r = tileRects.get(i);
			if (r.intersects(game.mouseBounds())) {
				if (game.mousePressed()) pressingButton = true;
				if (game.mouseClicked(1)) {
					selectedTileIndex = i;
					pressingButton = true;
					deleting = false;
					getButtonById("SetDelete").setColor(offButtonColor);
				}
				break;
			}
		}

		// If the mouse isn't pressed and either brush or setSolid is set as action, add a state (will be removed if identical in addMapState())
		if ((currentAction == Action.SetBrush || currentAction == Action.SetSolid) && !game.mousePressed()) {
			EditorState.addMapState();
		}

		// Calculate map offsets if mouse is dragged while currentAction is SetMove
		if (currentAction == Action.SetMove && !pressingButton) {
			if (game.mouseDragged()) {
				Tools.Vec2 offset = game.mouseDraggedOffsets();
				ox = (int) (lastOX + offset.x);
				oy = (int) (lastOY + offset.y);
			} else {
				lastOX = ox;
				lastOY = oy;
			}
		}

		// Deal with FillRect and SetSelect mode stuff
		if (currentAction == Action.FillRect || currentAction == Action.SetSelect) {

			if (game.mousePressed() && firstPressed && !hasDragged) {
				// if nothing has been selected or dragged out yet, set firstCorner to tile where the dragging started, but only if it's within the map area
				fRect mouseBounds = game.mouseBounds();
				firstCorner = screenToWorld(new Vec2(mouseBounds.x, mouseBounds.y));
				if (firstCorner.x >= 0 && firstCorner.x < map.numWide() && firstCorner.y >= 0 && firstCorner.y < map.numTall()) {
					firstCorner = new Vec2((int) Game.clamp(firstCorner.x, 0, map.numWide() - 1), (int) Game.clamp(firstCorner.y, 0, map.numTall() - 1));
					firstPressed = false;
				} else firstCorner = null;

			} else if (game.mouseDragged() && !firstPressed && !hasDragged) {

				// Step 2: if we have a corner but the mouse hasn't finished dragging yet, get temp rect for viewing based off other corner (so far)
				fRect mouseBounds = game.mouseBounds();
				Vec2 firstCornerCopy = new Vec2(firstCorner.x, firstCorner.y);
				Vec2 tempCorner = screenToWorld(new Vec2(mouseBounds.x, mouseBounds.y));
				Vec2 corner = new Vec2((int) Game.clamp(tempCorner.x, 0, map.numWide() - 1), (int) Game.clamp(tempCorner.y, 0, map.numTall() - 1));

				// Swap the corner variables to get them in the right order
				if (firstCornerCopy.x > corner.x) {
					int t = (int) firstCornerCopy.x;
					firstCornerCopy.x = corner.x;
					corner.x = t;
				}
				if (firstCornerCopy.y > corner.y) {
					int t = (int) firstCornerCopy.y;
					firstCornerCopy.y = corner.y;
					corner.y = t;
				}

				// Set dragged to new Rect based on corners for render() to draw
				dragged = new fRect(firstCornerCopy.x, firstCornerCopy.y, corner.x - firstCornerCopy.x + 1, corner.y - firstCornerCopy.y + 1);

			} else if (game.mouseHasFinalDragged() && dragged != null && firstCorner != null) {

				// if mouse has finished dragging, set dragged rectangle based on the original corner where dragging started
				game.mouseFinalDragged();
				fRect mouseBounds = game.mouseBounds();
				Vec2 beginning = new Vec2(firstCorner.x, firstCorner.y);
				Vec2 tempEnd = screenToWorld(new Vec2(mouseBounds.x, mouseBounds.y));
				Vec2 end = new Vec2((int) Game.clamp(tempEnd.x, 0, map.numWide() - 1), (int) Game.clamp(tempEnd.y, 0, map.numWide() - 1));

				// Swap coordinates again if necessary for right order
				if (beginning.x > end.x) {
					int t = (int) beginning.x;
					beginning.x = end.x;
					end.x = t;
				}
				if (beginning.y > end.y) {
					int t = (int) beginning.y;
					beginning.y = end.y;
					end.y = t;
				}

				// set firstCorner to null for next drag
				firstCorner = null;

				// Go ahead and fill the rect and reset if action is FillRect; if Select, keep the rectangle and set flag and mapData
				if (currentAction == Action.FillRect) {
					map.fillRect(dragged);
					dragged = null;
					firstPressed = true;
					hasDragged = false;
				} else if (currentAction == Action.SetSelect) {
					dragged = new fRect(beginning.x, beginning.y, end.x - beginning.x + 1, end.y - beginning.y + 1);
					hasDragged = true;
					tempMapData = map.getMapSelection(dragged);
				}

			} else if (currentAction == Action.SetSelect && hasDragged) {

				// If a rectangle is selected in select mode
				if (!pressingButton && game.mouseClicked(1) || game.keyUp(KeyEvent.VK_ESCAPE)) {
					// get rid of selection if mouse is clicked or escape is pressed and reset
					hasDragged = false;
					firstPressed = true;
					dragged = null;
					tempMapData = null;
				} else if (game.keyUp(KeyEvent.VK_BACK_SPACE)) {
					// clear map data from selection if backspace is pressed and update map selection data
					int index = EditorState.selectedTileIndex;
					EditorState.selectedTileIndex = -1;
					map.fillRect(dragged);
					EditorState.selectedTileIndex = index;
					tempMapData = map.getMapSelection(dragged);
				} else if (game.keyUp('f')) {
					// fill selection with the currently selected tile and update map selection data
					map.fillRect(dragged);
					tempMapData = map.getMapSelection(dragged);
				} else if (game.keyDown(KeyEvent.VK_RIGHT) && System.currentTimeMillis() - moveSelectionTimer >= moveSelectionDelay) {
					// Move selection right
					moveSelectionTimer = System.currentTimeMillis();
					dragged.x += 1;
				} else if (game.keyDown(KeyEvent.VK_LEFT) && System.currentTimeMillis() - moveSelectionTimer >= moveSelectionDelay) {
					// Move selection left
					moveSelectionTimer = System.currentTimeMillis();
					dragged.x -= 1;
				} else if (game.keyDown(KeyEvent.VK_UP) && System.currentTimeMillis() - moveSelectionTimer >= moveSelectionDelay) {
					// Move selection up
					moveSelectionTimer = System.currentTimeMillis();
					dragged.y -= 1;
				} else if (game.keyDown(KeyEvent.VK_DOWN) && System.currentTimeMillis() - moveSelectionTimer >= moveSelectionDelay) {
					// Move selection down
					moveSelectionTimer = System.currentTimeMillis();
					dragged.y += 1;
				} else if (game.keyUp(KeyEvent.VK_ENTER)) {
					// Paste selection when enter is pressed
					map.fillRect(dragged, tempMapData, true);
				}

			}
		}

		// Tick the map only if the mouse is pressed over it (and not over a button)
		if (!pressingButton && game.mousePressed()) map.tick(ox, oy);

		// Set pressingButton to false if the mouse is no longer pressed
		if (!game.mousePressed()) pressingButton = false;

		// Handle various helpful inputs
		int mapMoveSpeed = (int) Game.clamp(tSize * 2.0 / 5, 12, 128);
		if (game.keyDown('a')) ox += mapMoveSpeed;
		if (game.keyDown('d')) ox -= mapMoveSpeed;
		if (game.keyDown('w')) oy += mapMoveSpeed;
		if (game.keyDown('s')) oy -= mapMoveSpeed;

		if (game.keyUp('q')) handlePress(getButtonById("ZoomOut"));
		if (game.keyUp('e')) handlePress(getButtonById("ZoomIn"));
		if (game.keyUp(KeyEvent.VK_OPEN_BRACKET) || game.mouseZoomedIn()) selectedTileIndex = (selectedTileIndex - 1 + tileRects.size()) % tileRects.size();
		if (game.keyUp(KeyEvent.VK_CLOSE_BRACKET) || game.mouseZoomedOut()) selectedTileIndex = (selectedTileIndex + 1) % tileRects.size();
		if (game.keyUp('g')) handlePress(getButtonById("SetGrid"));
		if (game.keyUp('f') && currentAction != Action.SetSelect) handlePress(getButtonById("FillLayer"));
		if (game.keyUp('r')) handlePress(getButtonById("FillRect"));
		if (game.keyUp('x')) handlePress(getButtonById("SetDelete"));
		if (game.keyUp('z')) handlePress(getButtonById("Undo"));
		if (game.keyUp('y')) handlePress(getButtonById("Redo"));
		if (game.keyUp(KeyEvent.VK_1)) {
			ox = 100;
			oy = 100;
		}
	}

	public void render(Graphics g) {

		// Draw the map
		map.render(g, ox, oy);

		// Draw a cyan transparent rectangle over the hovered tile, as well as a string containing the tile's coordinates for reference
		fRect mousePos = game.mouseBounds();
		int tx = (int) (mousePos.x - ox) / EditorState.tSize - ((mousePos.x - ox < 0) ? 1 : 0);
		int ty = (int) (mousePos.y - oy) / EditorState.tSize - ((mousePos.y - oy < 0) ? 1 : 0);
		if (tx >= 0 && ty >= 0 && tx < map.numWide() && ty < map.numTall()) {
			new fRect(tx * tSize + ox, ty * tSize + oy, tSize, tSize).fill(g, new Color(0, 255, 255, 120));
			g.setColor(Color.white);
			g.setFont(new Font("Times New Roman", Font.BOLD, 36));
			g.drawString("<" + tx + "," + ty + ">", 10, 36);
		}

		// Draw the tiles in the selection area, as well as an indicator over the selected tile
		tileArea.fill(g, Color.LIGHT_GRAY);
		for (int i = 0; i < tileRects.size(); i++) {
			Rectangle r = tileRects.get(i).cast();
			Tile.getTile(i).render(g, 0, 0, r.x, r.y, tSizeSelectionArea);
			fRect rf = new fRect(r.x, r.y, r.width, r.height);
			if (i == selectedTileIndex) {
				rf.fill(g, new Color(255, 255, 255, 90));
				rf.draw(g, Color.black);
			}
		}

		// Draw the currently dragged area if it exists as well as any tiles inside of it (for motion)
		if (dragged != null) {
			// Get screen coordinates of rectangle corners
			Vec2 startPos = worldToScreen(new Vec2(dragged.x, dragged.y));
			Vec2 endPos = worldToScreen(new Vec2(dragged.x + dragged.width, dragged.y + dragged.height));
			if (tempMapData != null) {
				for (int z = 0; z < tempMapData.tiles.length; z++) {
					for (int y = 0; y < tempMapData.tiles[z].length; y++) {
						for (int x = 0; x < tempMapData.tiles[z][y].length; x++) {
							// draw tiles in selection over everything else if the tile is within the map area
							if (tempMapData.tiles[z][y][x] != -1 && x + (int) dragged.x >= 0 && x + (int) dragged.x < map.numWide() && y + (int) dragged.y >= 0
									&& y + (int) dragged.y < map.numTall()) {
								Tile.getTile(tempMapData.tiles[z][y][x]).render(g, x, y, (int) startPos.x, (int) startPos.y, tSize);

								// Draw grid and solids
								if (z == tempMapData.tiles.length - 1) {
									if (EditorState.drawingGrid && !tempMapData.solids[y][x]) {
										g.setColor(Color.white);
										g.drawRect(ox + (int) (x + dragged.x) * EditorState.tSize, oy + (int) (dragged.y + y) * EditorState.tSize,
												EditorState.tSize - 1, EditorState.tSize - 1);
									}
									if (tempMapData.solids[y][x]) {
										g.setColor(new Color(0, 0, 0, 80));
										g.fillRect(ox + (int) (x + dragged.x) * EditorState.tSize, oy + (int) (dragged.y + y) * EditorState.tSize,
												EditorState.tSize - 1, EditorState.tSize - 1);
										g.setColor(Color.red);
										g.drawRect(ox + (int) (x + dragged.x) * EditorState.tSize, oy + (int) (dragged.y + y) * EditorState.tSize,
												EditorState.tSize - 1, EditorState.tSize - 1);
									}
								}
							}
						}
					}
				}
			}
			// Fill transparent rectangle over the selection area for easy viewing
			new fRect(startPos.x, startPos.y, endPos.x - startPos.x, endPos.y - startPos.y).fill(g, new Color(255, 192, 203, 150));
		}

		// Draw all of the buttons to the screen
		for (Button b : buttons)
			b.render(g);
	}

	/**
	 * Returns the Button matching the String id that is passed in. Returns null if the button does not exist.
	 */
	public Button getButtonById(String id) {
		for (Button b : buttons) {
			if (b.id.equals(id)) return b;
		}
		System.out.printf("No button found with id '%s'!%n", id);
		return null;
	}

	/**
	 * Sets the currentAction to a certain Action, changing all of the other corresponding buttons to disactivated.
	 */
	public void setAction(Action action) {
		for (Action a : Action.values()) {
			Button b = getButtonById(a.toString());
			if (a != action) b.setColor(offButtonColor);
			else b.setColor(action == currentAction ? offButtonColor : onButtonColor);
		}
		currentAction = action == currentAction ? null : action;

		hasDragged = false;
		firstPressed = true;
		dragged = null;
		tempMapData = null;
	}

	/**
	 * Returns the current number of layers in the map. Returns 1 if the map is null.
	 */
	public int currentNumLayers() { return (map != null) ? map.numLayers() : 1; }

	//////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Immutable type that stores the data contained within a map for easy copying and access later.
	 */
	protected static class MapData {

		final int[][][] tiles; // stores tile id data
		final boolean[][] solids; // stores solid collision data

		/**
		 * @param tiles  The tile id data
		 * @param solids The solid collision data
		 */
		protected MapData(int[][][] tiles, boolean[][] solids) {
			this.tiles = tiles;
			this.solids = solids;
		}

		/**
		 * Returns true if another MapData d has the exact same tile data and solid data, and false if not.
		 * 
		 * @param d The MapData to be compared
		 * @return true if d is identical to this
		 */
		public final boolean equals(MapData d) {
			if (tiles.length != d.tiles.length || tiles[0].length != d.tiles[0].length || tiles[0][0].length != d.tiles[0][0].length) return false;
			for (int z = 0; z < tiles.length; z++) {
				for (int y = 0; y < tiles[z].length; y++) {
					for (int x = 0; x < tiles[z][y].length; x++) {
						if (tiles[z][y][x] != d.tiles[z][y][x]) return false;
						if (z == 0 && solids[y][x] != d.solids[y][x]) return false;
					}
				}
			}
			return true;
		}

		/**
		 * Returns a string representation of this MapData object (what the map saver would print out for this data)
		 * 
		 * @return string representation identical to map save()
		 */
		public final String toString() {
			StringBuilder b = new StringBuilder();
			for (int z = 0; z < tiles.length; z++) {
				for (int y = 0; y < tiles[z].length; y++) {
					for (int x = 0; x < tiles[z][y].length; x++) {
						b.append("" + tiles[z][y][x] + ((x == tiles[z][y].length - 1) ? "\n" : " "));
					}
				}
				b.append("BREAK\n");
			}

			for (int y = 0; y < solids.length; y++) {
				for (int x = 0; x < solids[y].length; x++) {
					b.append("" + (solids[y][x] ? 1 : 0) + ((x == solids[y].length - 1) ? "\n" : " "));
				}
			}
			b.append("BREAK\n");
			return b.toString();
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////

	private class Button {

		private Color color; // the button's color it should be drawn: generic by default
		private Color borderColor; // the button's border color: white by default
		private String text; // what text the button contains
		private String id; // the id of the button for handling purposes
		private Tools.fRect r; // the rectangle that this button is located within
		private EditorState handler; // the handler that will handle this button's corresponding action
		private boolean visible; // whether or not this button should be drawn and handled: true by default
		private boolean hasBorder; // whether or not the border should be drawn

		public Button(String text, String id, EditorState handler, Tools.fRect r) {
			this.r = r;
			this.handler = handler;
			this.id = id;
			setText(text);
			setColor(genericButtonColor);
			setBorderColor(Color.white);
			setDrawBorder(true);
			setVisible(true);
			buttons.add(this);
		}

		public void tick() {
			// handle this button's events if the mouse intersects and is clicked within it
			if (game.mouseBounds().intersects(r) && visible) {
				if (game.mousePressed()) EditorState.pressingButton = true;
				if (game.mouseClicked(1)) handler.handlePress(this);
			}
		}

		public void render(Graphics g) {
			// draw the button to the screen
			if (!visible) return;

			r.fill(g, color);
			if (hasBorder) r.draw(g, borderColor);

			g.setFont(new Font("Times New Roman", Font.BOLD, (int) r.height * 7 / 16));
			Game.drawCenteredString(g, Color.black, text, r, true);
		}

		/**
		 * Sets this button's color to the given Color c.
		 */
		public Button setColor(Color c) {
			this.color = c;
			return this;
		}

		/**
		 * Sets this button's border color to the given Color c.
		 */
		public Button setBorderColor(Color c) {
			this.borderColor = c;
			return this;
		}

		/**
		 * Sets this button to be visible or invisible, based on the Boolean b passed in.
		 */
		public Button setVisible(boolean b) {
			this.visible = b;
			return this;
		}

		/**
		 * Sets this button to have or not have a border drawn, based on the Boolean b passed in.
		 */
		public Button setDrawBorder(boolean b) {
			this.hasBorder = b;
			return this;
		}

		/**
		 * Sets this button's text to the given String s.
		 */
		public Button setText(String s) {
			this.text = s;
			return this;
		}
	}

	public Vec2 worldToScreen(Vec2 v) { return new Vec2(v.x * tSize + ox, v.y * tSize + oy); }

	public Vec2 screenToWorld(Vec2 v) { return new Vec2((v.x - ox) / tSize, (v.y - oy) / tSize); }

	public fRect worldToScreen(fRect r) {
		Vec2 topCorner = worldToScreen(new Vec2(r.x, r.y));
		Vec2 bottomCorner = worldToScreen(new Vec2(r.x + r.width, r.y + r.height));
		return new fRect(topCorner.x, topCorner.y, bottomCorner.x - topCorner.x, bottomCorner.y - topCorner.y);
	}

	public fRect screenToWorld(fRect r) {
		Vec2 topCorner = screenToWorld(new Vec2(r.x, r.y));
		Vec2 bottomCorner = screenToWorld(new Vec2(r.x + r.width, r.y + r.height));
		return new fRect(topCorner.x, topCorner.y, bottomCorner.x - topCorner.x, bottomCorner.y - topCorner.y);
	}

}
