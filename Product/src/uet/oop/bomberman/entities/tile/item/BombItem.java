package uet.oop.bomberman.entities.tile.item;

import uet.oop.bomberman.Game;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.entities.character.Bomber;
import uet.oop.bomberman.graphics.Sprite;

import static uet.oop.bomberman.sound.Sound.playPowerUp;

public class BombItem extends Item {

	public BombItem(int x, int y, Sprite sprite) {
		super(x, y, sprite);
	}

	@Override
	public void setPower() {
		this.setActive(true);
		Game.addBombRate(1);
	}

	@Override
	public boolean collide(Entity e) {
		if(e instanceof Bomber) {
			playPowerUp();
			((Bomber) e).addPowerup(this);
			remove();
			return true;
		}
		return false;
	}
}
