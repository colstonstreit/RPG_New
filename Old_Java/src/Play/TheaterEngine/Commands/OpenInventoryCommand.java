package Play.TheaterEngine.Commands;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;

import Engine.AssetManager;
import Engine.Game;
import Engine.Sprite;
import Engine.Tools.Vec2;
import Engine.Tools.fRect;
import Play.Entities.Items.ItemManager;
import Play.Entities.Items.ItemManager.Items;

public class OpenInventoryCommand extends BaseCommand {

	private static fRect totalRect, outerRect; // Whole screen and part of screen with item areas on it respectively
	private static fRect mainTextRect, switchScreenRect, sortRect; // "INVENTORY", info on how to switch screens, and sort message respectively
	private static ArrayList<ItemArea> itemAreas; // List of all of the items, obtained from ItemManager
	private static int numAcross, numTall, numPerScreen, numScreens; // # item areas across and down, with their product and then the # of screens required
	private static int offsetX, offsetY; // center the group of item boxes

	private static int currentScreen = 0; // The current screen of items the user is on

	public OpenInventoryCommand(Game game) {
		super(game);
		// Define rectangles
		totalRect = game.getFRect().getSubRect(0.05, 0.05, 0.9, 0.9);
		outerRect = totalRect.getSubRect(0, 0.08, 1, 0.9);
		mainTextRect = totalRect.getSubRect(0, 0, 1, 0.08);
		switchScreenRect = totalRect.getSubRect(0.65, 0, 0.35, 0.08);
		sortRect = totalRect.getSubRect(0, 0, 0.35, 0.08);

		// Calculate most variables
		itemAreas = new ArrayList<ItemArea>();
		numAcross = (int) (outerRect.width / ItemArea.outer.width);
		numTall = (int) (outerRect.height / ItemArea.outer.height);
		numPerScreen = numAcross * numTall;
		offsetX = (int) ((outerRect.width - numAcross * ItemArea.outer.width) / 2.0);
		offsetY = (int) ((outerRect.height - numTall * ItemArea.outer.height) / 2.0);
	}

	public void start() {
		itemAreas.clear();
		int size = ItemManager.size();
		numScreens = (int) Math.max(1, Math.ceil((double) size / numPerScreen));
		currentScreen = Math.min(currentScreen, numScreens - 1);
		for (int i = 0; i < size; i++) {
			ItemArea item = new ItemArea(ItemManager.getItemID(i), ItemManager.getAmount(i));
			itemAreas.add(item);
		}
	}

	public void tick(double deltaTime) {
		super.tick(deltaTime);
		// Do stuff below

		// Close window if n is pressed, sort if s, move right one screen if e and left one screen if q.
		if (game.keyUp('n')) {
			complete();
		} else if (game.keyUp('s')) {
			ItemManager.sort();
			start();
		} else if (game.keyUp('e')) {
			currentScreen = (currentScreen + 1) % numScreens;
		} else if (game.keyUp('q')) {
			currentScreen = (currentScreen - 1 + numScreens) % numScreens;
		}
	}

	public void render(Graphics g, int ox, int oy) {
		// Fill the rectangle
		totalRect.fill(g, new Color(128, 128, 128, 240));

		// Write all the stuff at the top
		g.setFont(new Font("Times New Roman", Font.BOLD, 32));
		Game.drawCenteredString(g, Color.white, "INVENTORY", mainTextRect, false);
		Game.drawCenteredString(g, Color.white, "q <=> e | " + (currentScreen + 1) + "/" + numScreens, switchScreenRect, true);
		Game.drawCenteredString(g, Color.white, "Press s to sort!", sortRect, true);

		// Loop through all of the potential item spots on this screen (breaks when it goes off the screen)
		for (int i = 0 + currentScreen * numPerScreen; i < ItemManager.MAX_NUM_SLOTS; i++) {
			if ((i - currentScreen * numPerScreen) / numAcross >= numTall) break;

			// Draw each potential item area
			Vec2 pos = new Vec2(outerRect.x + ((i - currentScreen * numPerScreen) % numAcross) * ItemArea.outer.width + offsetX,
					outerRect.y + ((i - currentScreen * numPerScreen) / numAcross) * ItemArea.outer.height + offsetY);
			ItemArea.outer.translate(pos.x, pos.y).draw(g, Color.white);

			if (i >= itemAreas.size()) continue;

			// If there is actually an item in this area, then get it, draw it, and fill in its information in its box

			ItemArea item = itemAreas.get(i);
			if (item.itemSprite != null) {
				Game.drawImage(g, item.itemSprite.image(), pos.x + ItemArea.imageRect.x, pos.y + ItemArea.imageRect.y, ItemArea.imageRect.width,
						ItemArea.imageRect.height);
			}
			g.setFont(new Font("Times New Roman", Font.BOLD, 18));
			Game.drawCenteredString(g, Color.white, Game.getProperCapitalization(item.item.name()), ItemArea.nameRect.translate(pos.x, pos.y), true);
			Game.drawCenteredString(g, Color.white, "Count: " + item.count, ItemArea.countRect.translate(pos.x, pos.y), true);
		}
	}

	public void complete() {

		// Do stuff above
		super.complete();
	}

	private static class ItemArea {

		private static final fRect outer = new fRect(0, 0, 64, 96); // Whole area including padding
		private static final fRect imageRect = new fRect(8, 8, 48, 48); // Where image should be drawn
		private static final fRect textRect = new fRect(8, 48, 48, 40); // Where text should be drawn
		private static final fRect nameRect = textRect.getSubRect(0, 0, 1, 0.5), countRect = textRect.getSubRect(0, 0.5, 1, 0.5); // Item name and count
		private Items item; // Item this area holds
		private int count = 0; // Amount of this item
		private Sprite itemSprite; // Item sprite

		public ItemArea(Items item, int count) {
			this.item = item;
			this.count = count;
			itemSprite = AssetManager.getItemSprite(item);
		}
	}

}
