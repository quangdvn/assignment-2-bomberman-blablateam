package uet.oop.bomberman.entities.tile.item;

import uet.oop.bomberman.Game;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.entities.character.Bomber;
import uet.oop.bomberman.graphics.Sprite;
import uet.oop.bomberman.sound.Sound;

public class FlameItem extends Item {

	public FlameItem(int x, int y, Sprite sprite) {
		super(x, y, sprite);
	}

	@Override
	public void setPower() {
		this.setActive(true);
		Game.addBombRadius(1);
	}

	@Override
	public boolean collide(Entity e) {
		if(e instanceof Bomber) {
			Sound.play(Sound.powerUp);
			((Bomber) e).addPowerup(this);
			remove();
			return true;
		}
		return false;
	}

}
