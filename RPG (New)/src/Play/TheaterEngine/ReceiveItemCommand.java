package Play.TheaterEngine;

import java.awt.Graphics;

import Engine.AssetManager;
import Engine.Game;
import Engine.Sprite;
import Engine.Tools.fRect;
import Play.Entities.Creature;
import Play.Entities.Creature.Facing;
import Play.Entities.Items.ItemManager;
import Play.Entities.Items.ItemManager.Items;

public class ReceiveItemCommand extends BaseCommand {

	private Sprite itemIcon; // The icon of the item
	private Creature player; // The creature to hold their arms up with the item
	private Facing pastDirection; // The direction the creature was previously facing
	private boolean addItemsHere = false; // Whether or not the ItemManager.giveItem() should be called here without checks
	private Items item; // Item to be given
	private int count; // Amount of item to be given

	private ShowDialogCommand dialogCommand; // The dialog command to alert player to new items
	private TurnCommand turnCommand; // The turn command to turn them back to where they were facing

	/**
	 * @param game         The game instance
	 * @param item         The item to be given
	 * @param count        The amount of the item to be given
	 * @param c            The creature that should be turned when receiving the item
	 * @param addItemsHere True if the items should be added to the inventory here
	 */
	public ReceiveItemCommand(Game game, Items item, int count, Creature c, boolean addItemsHere) {
		super(game);
		this.itemIcon = AssetManager.getItemSprite(item);
		String itemName = Game.getProperCapitalization(item.name()) + (count > 1 ? "s" : "");
		dialogCommand = new ShowDialogCommand(game, "You received " + count + " " + itemName + "!");
		pastDirection = c.facing;
		turnCommand = new TurnCommand(game, c, Facing.Down);
		player = c;
		this.addItemsHere = addItemsHere;
		this.item = item;
		this.count = count;
	}

	public void start() {
		group.add(dialogCommand);
		group.add(turnCommand);
	}

	public void tick(double deltaTime) {
		super.tick(deltaTime);
		// Do stuff below
		if (dialogCommand.hasCompleted) {
			complete();
		}
	}

	public void render(Graphics g, int ox, int oy) {
		if (itemIcon != null) {
			fRect playerRect = player.worldToScreen(new fRect(player.pos.x, player.pos.y, player.size.x, player.size.y).translate(0, -1));
			Game.drawImage(g, itemIcon.image(), playerRect.x, playerRect.y, playerRect.width, playerRect.height);
		}
	}

	public void complete() {
		group.add(new TurnCommand(game, player, pastDirection));
		if (addItemsHere) ItemManager.giveItem(item, count);
		// Do stuff above
		super.complete();
	}

}
