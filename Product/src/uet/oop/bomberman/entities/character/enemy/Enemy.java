package uet.oop.bomberman.entities.character.enemy;

import uet.oop.bomberman.Board;
import uet.oop.bomberman.Game;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.entities.Message;
import uet.oop.bomberman.entities.bomb.Flame;
import uet.oop.bomberman.entities.bomb.FlameSegment;
import uet.oop.bomberman.entities.character.Bomber;
import uet.oop.bomberman.entities.character.Character;
import uet.oop.bomberman.entities.character.enemy.ai.AI;
import uet.oop.bomberman.graphics.Screen;
import uet.oop.bomberman.graphics.Sprite;
import uet.oop.bomberman.level.Coordinates;

import java.awt.*;

public abstract class Enemy extends Character {

	protected int _points;

	protected double _speed;
	protected AI _ai;

	protected final double MAX_STEPS;
	protected final double rest;
	protected double _steps;

	protected int _finalAnimation = 30;
	protected Sprite _deadSprite;

	public Enemy(int x, int y, Board board, Sprite dead, double speed, int points) {
		super(x, y, board);

		_points = points;
		_speed = speed;

		MAX_STEPS = Game.TILES_SIZE / _speed;
		rest = (MAX_STEPS - (int) MAX_STEPS) / MAX_STEPS;
		_steps = MAX_STEPS;

		_timeAfter = 20;
		_deadSprite = dead;
	}

	@Override
	public void update() {
		animate();

		if(!_alive) {
			afterKill();
			return;
		}

		if(_alive)
			calculateMove();
	}

	@Override
	public void render(Screen screen) {

		if(_alive)
			chooseSprite();
		else {
			if(_timeAfter > 0) {
				_sprite = _deadSprite;
				_animate = 0;
			} else {
				_sprite = Sprite.movingSprite(Sprite.mob_dead1, Sprite.mob_dead2, Sprite.mob_dead3, _animate, 60);
			}
		}
		screen.renderEntity((int)_x, (int)_y - _sprite.SIZE, this);
	}

	@Override
	public void calculateMove() {

		if (_steps <= 0) {
			_direction = _ai.calculateDirection();
			_steps = MAX_STEPS;
		}

		if (_direction == -1) {
			_steps = 0;
			_moving = false;
		}

		int _xNow = 0,_yNow = 0;

		if(_direction == 0) _yNow--;
		else if(_direction == 2) _yNow++;
		else if(_direction == 3) _xNow--;
		else if(_direction == 1) _xNow++;

		if(canMove(_xNow, _yNow)) {
            _steps -= 1 + rest;
			move(_xNow * _speed, _yNow * _speed);
			_moving = true;

		} else {
            _steps = 0;
			_moving = false;
		}
	}

	@Override
	public void move(double xa, double ya) {
		if(!_alive) return;
		_y += ya;
		_x += xa;
	}

	@Override
	public boolean canMove(double x, double y) {
        double xTile = _x, yTile = _y;

        if(_direction == 0) {
            yTile = _y - 17 + _sprite.getSize();
            xTile = _x + _sprite.getSize() / 2;

        } else if(_direction == 1) {
            yTile = _y - 16 + _sprite.getSize() / 2;
            xTile = _x + 1;

        } else if(_direction == 2) {
            yTile = _y - 15;
            xTile = _x + _sprite.getSize() / 2;

        } else if(_direction == 3) {
            yTile = _y - 16 + _sprite.getSize() / 2;
            xTile = _x + _sprite.getSize() - 1;
        }

        xTile = Coordinates.pixelToTile(xTile);
        yTile = Coordinates.pixelToTile(yTile);

        Entity a = _board.getEntity(xTile + (int)x, yTile + (int)y, this);

        return a.collide(this);
	}

	@Override
	public boolean collide(Entity e) {
		if(e instanceof Flame) {
			kill();
		}
		if(e instanceof FlameSegment) {
			kill();
		}
		if(e instanceof Bomber) {
			((Bomber) e).kill();
		}
		return true;
	}

	@Override
	public void kill() {
		if(!_alive) return;
		_alive = false;

		_board.addPoints(_points);

		Message msg = new Message("+" + _points, getXMessage(), getYMessage(), 2, Color.white, 14);
		_board.addMessage(msg);
	}


	@Override
	protected void afterKill() {
		if(_timeAfter > 0) --_timeAfter;
		else {
			if(_finalAnimation > 0) --_finalAnimation;
			else
				remove();
		}
	}

	protected abstract void chooseSprite();
}
