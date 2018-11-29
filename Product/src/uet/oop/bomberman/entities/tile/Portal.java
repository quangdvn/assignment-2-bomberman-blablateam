package uet.oop.bomberman.entities.tile;

import uet.oop.bomberman.Board;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.entities.character.Bomber;
import uet.oop.bomberman.graphics.Sprite;
import uet.oop.bomberman.sound.Sound;

public class Portal extends Tile {

	Board _board;

	public Portal(int x, int y, Sprite sprite, Board board) {
		super(x, y, sprite);
		_board = board;
	}
	
	@Override
	public boolean collide(Entity e) {
		if (e instanceof Bomber  && _board.detectNoEnemies()) {
			Sound.playStageComplete();
			_board.nextLevel();
		}
		return true;
	}

}
