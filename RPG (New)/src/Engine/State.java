package Engine;

import java.awt.Graphics;

public abstract class State {

	protected Game game;

	public State(Game game) { this.game = game; }

	public abstract void tick(double deltaTime);

	public abstract void render(Graphics g);

	public abstract Tools.Vec2 worldToScreen(Tools.Vec2 v);

	public abstract Tools.Vec2 screenToWorld(Tools.Vec2 v);

}
