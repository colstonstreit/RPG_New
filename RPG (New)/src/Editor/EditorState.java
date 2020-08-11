package Editor;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import Engine.Assets;
import Engine.Game;
import Engine.Sprite;
import Engine.State;
import Engine.Tools;
import Engine.Tools.Vec2;
import Play.Tile;

public class EditorState extends State {

	public static int tSize = 32; // size that tiles should be rendered
	private static final int tSizeSelectionArea = 64; // size that tiles in selection menu should be drawn

	private static final Color onButtonColor = new Color(0, 255, 0, 220); // color of buttons that are "on"
	private static final Color offButtonColor = new Color(255, 0, 0, 220); // color of buttons that are "off"
	private static final Color genericButtonColor = new Color(180, 180, 180, 180); // the button's generic (default) color
	private static final Color highlightButtonColor = new Color(255, 255, 0, 220); // color of buttons that are highlighted or different (yellow)

	private final Tools.fRect tileArea = game.getFRect().getSubRect(0.9, 0, 0.1, 1); // Rectangle containing where the tile selection is drawn
	private final Rectangle tileSpace = tileArea.getSubRect(0.02, 0.005, 0.96, 0.99).cast(); // space within the tileArea where tiles actually drawn
	private final Tools.fRect buttonArea = game.getFRect().getSubRect(0.70, 0, 0.2, 1); // area within which buttons are placed
	private final Tools.fRect layerButtonArea = buttonArea.getSubRect(0.1, 0.02, 0.8, 0.13); // area where layer buttons are placed

	public static boolean pressingButton = false; // whether or not the user has pressed a button on this cycle of mouse hovering (and pressing)
	public static boolean drawingGrid = false; // whether or not the map should draw the white tile grid
	public static boolean deleting = false; // whether or not the editor is in delete mode

	private static ArrayList<Tools.fRect> tileRects = new ArrayList<Tools.fRect>(); // list of rectangles in tile area where tiles should be drawn
	public static int selectedTileIndex = 0; // the index of the currently selected tile (same as in Assets and in Tile)

	public enum Action { SetMove, SetSelect, SetBrush, FillRect, AddColumn, RemoveColumn, AddRow, RemoveRow, SetSolid; } // enum of different potential actions
	public static Action currentAction = Action.SetMove; // current action (is moving the map by mouse by default)

	public static final int sizeChangingDelay = 150; // delay between row/column additions/removals
	public static long sizeChangingTimer = System.currentTimeMillis(); // timer to keep track of row/colum additions/removals

	public static final int solidAddingDelay = 500; // delay between actions of making certain map tiles solid
	public static long solidAddingTimer = System.currentTimeMillis(); // timer to keep track of actions making certain map tiles solid
	public static int lastTXChanged = -1, lastTYChanged = -1; // the coordinates of the last tile that was made solid

	private static int ox = 100, oy = 100; // the position at which the map is drawn
	private static int lastOX = 0, lastOY = 0; // the last x and y offsets before the mouse started being dragged

	public static int selectedLayer = 0; // the index of the currently selected layer
	public static final int numLayersAllowed = 3; // how many layers the map is allowed to have
	public static boolean[] layerBools = new boolean[numLayersAllowed]; // a list of booleans determining whether or not each layer should be drawn
	private Button[] layerButtons; // a list of the layer buttons for easy access

	public static Tools.fRect draggedArea = null; // contains the rectangle that is currently being dragged in fillRect mode

	private EditorTileMap map = null; // the map that is being made
	private ArrayList<Button> buttons = new ArrayList<Button>(); // the list of buttons for the GUI to access

	public EditorState(Game game) {
		super(game);

		// Create the map
		map = new EditorTileMap(game, 20, 20);

		// Create the tileRects where each individual tile of Tile.tiles() will be rendered for selection area
		int numTilesWide = tileSpace.width / (tSizeSelectionArea + 1);
		Sprite[] tiles = Assets.getTileSprites();
		for (int i = 0; i < tiles.length; i++) {
			if (tiles[i] == null) break;
			int indexModNumWide = i % numTilesWide;
			tileRects.add(new Tools.fRect(tileSpace.x + indexModNumWide * (tSizeSelectionArea + 1), tileSpace.y + (tSizeSelectionArea + 1) * (i / numTilesWide),
					tSizeSelectionArea, tSizeSelectionArea));
		}

		// Create the right-hand side of buttons
		addButton("Set Delete", "SetDelete").setColor(offButtonColor);
		addButton("Set Gridlines", "SetGrid").setColor(offButtonColor);
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
		addButton("Switch to Game", "SwitchToGame");
		addButton("New Map", "NewMap");
		addButton("Save Map", "SaveMap");
		addButton("Load Map", "LoadMap");

		// Create the left-hand side of buttons
		layerButtons = new Button[numLayersAllowed];
		for (int i = 1; i <= layerButtons.length; i++) {
			// create layer buttons with titles and correct positions, and set them visible if they should be
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
	
	private Button addButton(String text, String id) {
		int i = buttons.size();
		return new Button(text, id, this, buttonArea.getSubRect(0.05 + 0.5 * (i % 2), 0.17 + 0.05 * (int) (i / 2), 0.4, 0.04));
	}

	/**
	 * Handles the events that should occur when the Button b is pressed based on the button's ID.
	 */
	public void handlePress(Button b) {
		switch (b.id) {
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

		// Deal with FillRect mode stuff, checking to see if one should be drawn and calling the map to fill if necessary
		draggedArea = (game.mouseDragged() && !pressingButton && currentAction == Action.FillRect) ? Tools.fRect.castToFRect(game.mouseCurrentDragged()) : null;
		if (game.mouseHasFinalDragged()) {
			if (currentAction == Action.FillRect && !pressingButton) map.fillRect(game.mouseFinalDragged(), ox, oy);
			else game.mouseFinalDragged();
		}

		// Tick the map only if the mouse is pressed over it (and not over a button)
		if (!pressingButton && game.mousePressed()) map.tick(ox, oy);

		// Set pressingButton to false if the mouse is no longer pressed
		if (!game.mousePressed()) pressingButton = false;

		// Handle various helpful inputs
		int mapMoveSpeed = (int) Game.clamp(tSize * 2.0 / 5, 12, 128);
		if (game.keyDown('a') || game.keyDown(KeyEvent.VK_LEFT)) ox += mapMoveSpeed;
		if (game.keyDown('d') || game.keyDown(KeyEvent.VK_RIGHT)) ox -= mapMoveSpeed;
		if (game.keyDown('w') || game.keyDown(KeyEvent.VK_UP)) oy += mapMoveSpeed;
		if (game.keyDown('s') || game.keyDown(KeyEvent.VK_DOWN)) oy -= mapMoveSpeed;

		if (game.keyUp('q')) handlePress(getButtonById("ZoomOut"));
		if (game.keyUp('e')) handlePress(getButtonById("ZoomIn"));
		if (game.keyUp(KeyEvent.VK_OPEN_BRACKET) || game.mouseZoomedIn()) selectedTileIndex = (selectedTileIndex - 1 + tileRects.size()) % tileRects.size();
		if (game.keyUp(KeyEvent.VK_CLOSE_BRACKET) || game.mouseZoomedOut()) selectedTileIndex = (selectedTileIndex + 1) % tileRects.size();
		if (game.keyUp('g')) handlePress(getButtonById("SetGrid"));
		if (game.keyUp('f')) handlePress(getButtonById("FillLayer"));
		if (game.keyUp('r')) handlePress(getButtonById("FillRect"));
		if (game.keyUp('z')) handlePress(getButtonById("SetDelete"));
		if (game.keyUp(KeyEvent.VK_1)) {
			ox = 100;
			oy = 100;
		}
	}

	public void render(Graphics g) {

		// Draw the map
		map.render(g, ox, oy);

		// Draw a cyan transparent rectangle over the hovered tile, as well as a string containing the tile's coordinates for reference
		Tools.fRect mousePos = game.mouseBounds();
		int tx = (int) (mousePos.x - ox) / EditorState.tSize - ((mousePos.x - ox < 0) ? 1 : 0);
		int ty = (int) (mousePos.y - oy) / EditorState.tSize - ((mousePos.y - oy < 0) ? 1 : 0);
		if (tx >= 0 && ty >= 0 && tx < map.numWide() && ty < map.numTall()) {
			new Tools.fRect(tx * tSize + ox, ty * tSize + oy, tSize, tSize).fill(g, new Color(0, 255, 255, 120));
			g.setColor(Color.white);
			g.setFont(new Font("Times New Roman", Font.BOLD, 36));
			g.drawString("<" + tx + "," + ty + ">", 10, 36);
		}

		// Draw the tiles in the selection area, as well as an indicator over the selected tile
		tileArea.fill(g, Color.LIGHT_GRAY);
		for (int i = 0; i < tileRects.size(); i++) {
			Rectangle r = tileRects.get(i).cast();
			Tile.getTile(i).render(g, 0, 0, r.x, r.y, tSizeSelectionArea);
			Tools.fRect rf = new Tools.fRect(r.x, r.y, r.width, r.height);
			if (i == selectedTileIndex) {
				rf.fill(g, new Color(255, 255, 255, 90));
				rf.draw(g, Color.black);
			}
		}

		// Draw the currently dragged area if it exists
		if (draggedArea != null) draggedArea.fill(g, new Color(170, 170, 170, 100));

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
	}

	/**
	 * Returns the current number of layers in the map. Returns 1 if the map is null.
	 */
	public int currentNumLayers() { return (map != null) ? map.numLayers() : 1; }

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
			if(hasBorder) r.draw(g, borderColor);
			
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

	@Override
	public Vec2 worldToScreen(Vec2 v) { return new Tools.Vec2(v.x * tSize + ox, v.y * tSize + oy); }

	@Override
	public Vec2 screenToWorld(Vec2 v) { return new Tools.Vec2((v.x - ox) / tSize, (v.y - oy) / tSize); }


}
