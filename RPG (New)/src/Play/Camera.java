package Play;

import Engine.Game;
import Engine.State;
import Engine.Tools.Vec2;

public class Camera {

	private Game game;
	private Entity e;
	public int ox, oy;
	
	private static final int cameraInertia = 10;

	public Camera(Game game, int ox, int oy) {
		this.game = game;
		this.ox = ox;
		this.oy = oy;
	}

	public State getState() { return game.currentState(); }

	public void centerOnEntity(Entity toFollow) { e = toFollow; }

	public void tick(double deltaTime) {
		if (e != null) {
			Vec2 screenPos = getState().worldToScreen(e.getCenter()).subtract(new Vec2(ox, oy));
			double idealOX = game.getWidth() / 2 - screenPos.x;
			double idealOY = game.getHeight() / 2 - screenPos.y;
			
			double diffX = (double) idealOX - ox;
			double diffY = (double) idealOY - oy;
			
			if(diffX != 0) ox += (int) (diffX * Math.exp(1 / diffX) / cameraInertia);
			if(diffY != 0) oy += (int) (diffY * Math.exp(1 / diffY) / cameraInertia);
			
			if(new Vec2(diffX, diffY).getMagnitude() <= 4) {
				ox = (int) idealOX;
				oy = (int) idealOY;
			}
		}
	}

}
