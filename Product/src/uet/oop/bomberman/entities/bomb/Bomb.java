package uet.oop.bomberman.entities.bomb;

import uet.oop.bomberman.Board;
import uet.oop.bomberman.Game;
import uet.oop.bomberman.entities.AnimatedEntitiy;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.entities.character.Bomber;
import uet.oop.bomberman.entities.character.Character;
import uet.oop.bomberman.entities.character.enemy.Enemy;
import uet.oop.bomberman.graphics.Screen;
import uet.oop.bomberman.graphics.Sprite;
import uet.oop.bomberman.level.Coordinates;

import static uet.oop.bomberman.sound.Sound.playExplosion;

public class Bomb extends AnimatedEntitiy {

	protected double _timeToExplode = 120; //2 seconds
	public int _timeAfter = 20;	// time to disappear explosion

	protected Board _board;
	protected Flame[] _flames = null;
	protected boolean _exploded = false;
	protected boolean _allowedToPassThrough = true;

	public Bomb(int x, int y, Board board) {
		_x = x;
		_y = y;
		_board = board;
		_sprite = Sprite.bomb;
	}

	@Override
	public void update() {
		if(_timeToExplode > 0)
			_timeToExplode--;
		else {
			if(!_exploded)
				explode();
			else
				updateFlames();

			if(_timeAfter > 0)
				_timeAfter--;
			else
				remove();
		}
		animate();
	}

	@Override
	public void render(Screen screen) {
		if(_exploded) {
			_sprite =  Sprite.bomb_exploded2;
			renderFlames(screen);
		} else
			_sprite = Sprite.movingSprite(Sprite.bomb, Sprite.bomb_1, Sprite.bomb_2, _animate, 60);

		int xt = (int)_x << 4;
		int yt = (int)_y << 4;

		screen.renderEntity(xt, yt , this);
	}

	public void renderFlames(Screen screen) {
		for (Flame flame : _flames) {
			flame.render(screen);
		}
	}

	public void updateFlames() {
		for (Flame flame : _flames) {
			flame.update();
		}
	}

	/**
	 * Handle the explosion
	 */
	protected void explode() {
		playExplosion();
		_exploded = true;
		_allowedToPassThrough = true;
		Character a = _board.getCharacterAt(_x, _y);
			if(a != null)  {
				a.kill();
			}
		_flames = new Flame[4];
			for (int i = 0; i < _flames.length; i++) {
			_flames[i] = new Flame((int)_x, (int)_y, i, Game.getBombRadius(), _board);
		}
	}

	public void multiExplode() {
		_timeToExplode = 0;
	}

	public FlameSegment flameAt(int x, int y) {
		if(!_exploded) return null;

		for (Flame flame : _flames) {
			if (flame == null) return null;
			FlameSegment e = flame.flameSegmentAt(x, y);
			if (e != null) return e;
		}
		return null;
	}

	@Override
	public boolean collide(Entity e) {
		if(e instanceof Bomber ) {
			double diffX = e.getX() - Coordinates.tileToPixel(getX());
			double diffY = e.getY() - Coordinates.tileToPixel(getY());

			if(!(diffX >= -10 && diffX < 16 && diffY >= 1 && diffY <= 28)) { // See if the bomber has already moved
				_allowedToPassThrough = false;								 // out of the bomb
			}
			return _allowedToPassThrough;
		}
		if(e instanceof Enemy) {
			return false;
		}
		if(e instanceof Flame || e instanceof Bomb) {
			multiExplode();
		}
		return true;
	}
}
