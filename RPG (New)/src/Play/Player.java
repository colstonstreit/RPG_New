package Play;

import java.awt.Color;
import java.awt.Graphics;

import Engine.Game;
import Engine.Tools.Vec2;
import Engine.Tools.fRect;

public class Player extends Entity.Dynamic {

	public Player(Game game, double x, double y) {
		super(game, "Player");
		this.pos = new Vec2(x, y);
		solidVsStatic = true;
		solidVsDynamic = true;
		size = new Vec2(0.75, 0.75);
	}

	@Override
	public void tick(double deltaTime) {
		if (game.keyDown('s')) v.y = 0.1 * deltaTime;
		else if (game.keyDown('w')) v.y = -0.1 * deltaTime;
		else v.y = 0;

		if (game.keyDown('a')) v.x = -0.1 * deltaTime;
		else if (game.keyDown('d')) v.x = 0.1 * deltaTime;
		else v.x = 0;
		
		if(v.x != 0) {
			pos.x += v.x;
			if(solidVsStatic) {
				fRect hitbox = new fRect(pos.x, pos.y, size.x, size.y);
				for(Vec2 r : PlayState.map.getColliders()) {
					if(hitbox.intersects(new fRect(r.x, r.y, 1, 1))) {
						if(v.x > 0 && pos.x + size.x > r.x) pos.x = r.x - size.x; 
						else if(v.x < 0 && pos.x < r.x + 1) pos.x = r.x + 1;
					}
				}
			}
		}
		
		if(v.y != 0) {
			pos.y += v.y;
			if(solidVsStatic) {
				fRect hitbox = new fRect(pos.x, pos.y, size.x, size.y);
				for(Vec2 r : PlayState.map.getColliders()) {
					if(hitbox.intersects(new fRect(r.x, r.y, 1, 1))) {
						if(v.y > 0 && pos.y + size.y > r.y) pos.y = r.y - size.y; 
						else if(v.y < 0 && pos.y < r.y + 1) pos.y = r.y + 1;
					}
				}
			}
		}
	}

	@Override
	public void render(Graphics g, int ox, int oy) {
		Vec2 screenPos = getState().worldToScreen(pos);
		g.setColor(Color.white);
		Game.fillRect(g, screenPos.x, screenPos.y, size.x * Tile.GAME_SIZE, size.y * Tile.GAME_SIZE);
	}

	@Override
	public void onInteract(Entity e) {}

}
