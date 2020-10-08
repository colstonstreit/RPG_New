package Play.Entities.Items;

import Engine.Game;
import Play.Entities.Dynamic;
import Play.Entities.Entity;
import Play.Entities.Items.ItemManager.Items;

public abstract class Item extends Dynamic {

	public final Items id;
	public final boolean stackable;

	public Item(Game game, Items id, String name, boolean stackable) {
		super(game, name);
		this.id = id;
		this.stackable = stackable;
	}

	public abstract boolean onUse();

}

class Money extends Item {

	public Money(Game game) { super(game, Items.MONEY, "Money", true); }

	public boolean onUse() { return false; }

	public void onInteract(Entity e) {}
	

}
