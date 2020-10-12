package Play.Entities;

import java.awt.Graphics;

import Engine.AssetManager.CharacterSprites;
import Engine.Game;
import Engine.Tools.Vec2;
import Engine.Tools.fRect;
import Play.LootTable;
import Play.TheaterEngine.Commands.ShowDialogCommand;
import Play.TheaterEngine.Commands.TheaterEngine;

public class NPC extends Creature {

	private LootTable<String> textOptions;

	public NPC(Game game, String name, CharacterSprites spriteName, Vec2 pos) {
		super(game, name, spriteName, pos);
		this.textOptions = new LootTable<String>().add("I AM ERROR", 1);
		relativeHitbox = new fRect(0, 0.5, 1, 0.5);
	}

	public void onInteract(Entity e) {
		if (e instanceof Creature) {
			Creature creature = (Creature) e;
			switch (creature.facing) {
				case Up:
					changeAnimation("Down");
					break;
				case Down:
					changeAnimation("Up");
					break;
				case Left:
					changeAnimation("Right");
					break;
				case Right:
					changeAnimation("Left");
					break;
			}
			v = new Vec2(0, 0);
			TheaterEngine.add(new ShowDialogCommand(game, textOptions.get()));
		}
	}

	public void tick(double deltaTime) {

		super.tick(deltaTime);

	}

	public void render(Graphics g, int ox, int oy) {

		if (isOnScreen()) {
			super.render(g, ox, oy);
			// worldToScreen(interactableRegion()).draw(g, Color.white);
		}

	}

	/**
	 * Sets this NPC's text options (all equally likely) and then returns the NPC.
	 * 
	 * @param options An array of text options that will all be equally likely to be chosen.
	 */
	public NPC setText(String... options) {
		textOptions = new LootTable<String>().addSet(options, new double[] { 1 });
		return this;
	}

	/**
	 * Sets this NPC's text options to the given loot table so that some options will be more or less likely than others. It then returns this NPC.
	 * 
	 * @param options The LootTable with text options and corresponding weights set.
	 */
	public NPC setText(LootTable<String> options) {
		textOptions = options;
		return this;
	}

}
