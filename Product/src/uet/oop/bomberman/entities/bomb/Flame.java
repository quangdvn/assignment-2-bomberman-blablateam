package uet.oop.bomberman.entities.bomb;

import uet.oop.bomberman.Board;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.entities.LayeredEntity;
import uet.oop.bomberman.entities.character.Character;
import uet.oop.bomberman.entities.character.Bomber;
import uet.oop.bomberman.entities.character.enemy.Doll;
import uet.oop.bomberman.entities.character.enemy.Enemy;
import uet.oop.bomberman.entities.tile.destroyable.Brick;
import uet.oop.bomberman.graphics.Screen;

public class Flame extends Entity {

	protected Board _board;
	protected int _direction;
	private int _radius;
	protected int xOrigin, yOrigin;
	protected FlameSegment[] _flameSegments = new FlameSegment[0];

	/**
	 *
	 * @param x hoành độ bắt đầu của Flame
	 * @param y tung độ bắt đầu của Flame
	 * @param direction là hướng của Flame
	 * @param radius độ dài cực đại của Flame
	 */
	public Flame(int x, int y, int direction, int radius, Board board) {
		xOrigin = x;
		yOrigin = y;
		_x = x;
		_y = y;
		_direction = direction;
		_radius = radius;
		_board = board;
		createFlameSegments();
	}

	/**
	 * Create FlameSegment
	 */
	private void createFlameSegments() {
		/**
		 * Counting the length of Flame, as the number of flameSegment
		 */
		_flameSegments = new FlameSegment[calculatePermittedDistance()];

		/**
		 * last is the final segment
		 */
		boolean last;
		int x = (int)_x;
		int y = (int)_y;
		for (int i = 0; i < _flameSegments.length; i++) {
			last = (i == _flameSegments.length - 1);

			switch (_direction) {
				case 0:
					y--;
					break;
				case 1:
					x++;
					break;
				case 2:
					y++;
					break;
				case 3:
					x--;
					break;
			}
			_flameSegments[i] = new FlameSegment(x, y, _direction, last);
		}

	}

	/**
	 * Counting the radius of Flame, if it is Brick/Wall, the flame will be cut off
	 * @return the radius of Flame
	 */
	private int calculatePermittedDistance() {
		int radius = 0;
		int x = (int)_x;
		int y = (int)_y;
		while(radius < _radius) {
			if(_direction == 0) y--;
			if(_direction == 1) x++;
			if(_direction == 2) y++;
			if(_direction == 3) x--;

			Entity a = _board.getEntity(x, y, null);

			if(!a.collide(this)) {
				break;
			}
			++radius;
		}
		return radius;
	}
	
	public FlameSegment flameSegmentAt(int x, int y) {
		for (FlameSegment flameSegment : _flameSegments) {
			if (flameSegment.getX() == x && flameSegment.getY() == y)
				return flameSegment;
		}
		return null;
	}

	@Override
	public void update() {
		for (FlameSegment flameSegment: _flameSegments) {
			flameSegment.update();
		}
	}
	
	@Override
	public void render(Screen screen) {
		for (FlameSegment flameSegment : _flameSegments) {
			flameSegment.render(screen);
		}
	}

	@Override
	public boolean collide(Entity e) {
		if (e instanceof Bomber && !((Bomber)e).isImmortal()) {
			((Bomber)e).kill();
		}
		if (e instanceof Enemy) {
			if (!(e instanceof Doll)) {
				((Enemy)e).kill();

			} else if (!((Doll)e).isImmortal()) {
				((Doll)e).kill();
			}
		}
		return true;
	}
}
