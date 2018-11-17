package uet.oop.bomberman.entities.character.enemy.ai;

import uet.oop.bomberman.entities.character.enemy.Enemy;

public class AILow extends AI {

    Enemy _e;
    int _currentDirection = random.nextInt(4);
    private final int[] _direction = {0, 1, 2, 3};

    public AILow(Enemy _e) {
        this._e = _e;
    }

	@Override
	public int calculateDirection() {
        if (!_e.isMoving()) {
            int nextDirection = random.nextInt(3);
            int count = -1;

            for (int i = 0; i < 4; ++i) {
                if (_currentDirection != _direction[i]) ++count;
                if (count == nextDirection) {
                    _currentDirection = _direction[i];
                }
            }
        }
        return _currentDirection;
	}

}
