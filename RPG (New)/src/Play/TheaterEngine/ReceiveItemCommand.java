package Play.TheaterEngine;

import java.awt.Graphics;
import java.util.ArrayList;

import Engine.AssetManager;
import Engine.Game;
import Engine.Sprite;
import Engine.Tools.fRect;
import Play.Entities.Creature;
import Play.Entities.Creature.Facing;
import Play.Entities.Items.ItemManager.Items;

public class ReceiveItemCommand extends BaseCommand {

	private Sprite itemIcon;
	private Creature player;
	private Facing pastDirection;

	private ShowDialogCommand dialogCommand;
	private TurnCommand turnCommand;

	/**
	 * @param game  The game instance
	 * @param item  The item to be given
	 * @param count The amount of the item to be given
	 * @param c     The creature that should be turned when receiving the item
	 * @param group The group of commands this command belongs to
	 */
	public ReceiveItemCommand(Game game, Items item, int count, Creature c, ArrayList<BaseCommand> group) {
		super(game, group);
		this.itemIcon = AssetManager.getItemSprite(item);
		String itemName = Game.getProperCapitalization(item.name()) + (count > 1 ? "s" : "");
		dialogCommand = new ShowDialogCommand(game, "You received " + count + " " + itemName + "!");
		pastDirection = c.facing;
		turnCommand = new TurnCommand(game, c, Facing.Down);
		player = c;
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

		// Do stuff above
		super.complete();
	}

}
