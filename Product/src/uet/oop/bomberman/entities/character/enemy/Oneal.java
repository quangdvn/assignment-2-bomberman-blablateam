package uet.oop.bomberman.entities.character.enemy;


import uet.oop.bomberman.Board;
import uet.oop.bomberman.Game;
import uet.oop.bomberman.entities.character.enemy.ai.AIMedium;
import uet.oop.bomberman.graphics.Sprite;

public class Oneal extends Enemy {

	/**
	 * Still another normal Enemy
	 * Also reward point for destroy and Speed
	 * @param x Location x
	 * @param y Location y
	 * @param board Place to draw
	 */
	public Oneal(int x, int y, Board board) {
		super(x, y, board, Sprite.oneal_dead, Game.getBomberSpeed()  , 200);
		
		_sprite = Sprite.oneal_left1;
		
		_ai = new AIMedium(_board.getBomber(), this, _board, false);
		_direction  = _ai.calculateDirection();
}
	
	@Override
	protected void chooseSprite() {
		switch(_direction) {
			case 0:
			case 1:
				if(_moving)
					_sprite = Sprite.movingSprite(Sprite.oneal_right1, Sprite.oneal_right2, Sprite.oneal_right3, _animate, 60);
				else
					_sprite = Sprite.movingSprite(Sprite.oneal_left1, Sprite.oneal_right2, Sprite.oneal_left3, _animate, 60);
				break;
			case 2:
			case 3:
				if(_moving)
					_sprite = Sprite.movingSprite(Sprite.oneal_left1, Sprite.oneal_left2, Sprite.oneal_left3, _animate, 60);
				else
					_sprite = Sprite.movingSprite(Sprite.oneal_left2, Sprite.oneal_right3, Sprite.oneal_left1, _animate, 60);
				break;
		}
	}
}
