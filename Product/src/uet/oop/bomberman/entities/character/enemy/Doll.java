package uet.oop.bomberman.entities.character.enemy;

import uet.oop.bomberman.Board;
import uet.oop.bomberman.Game;
import uet.oop.bomberman.entities.Message;
import uet.oop.bomberman.entities.character.enemy.ai.AILow;
import uet.oop.bomberman.graphics.Sprite;

import java.awt.*;

public class Doll extends Enemy {
    /**
     * Still another normal Enemy
     * Also reward point for destroy and Speed
     * @param x Location x
     * @param y Location y
     * @param board Place to draw
     */
    protected int _lives = 2;
    protected int _timeImmortal = 0;
    protected boolean _immortal = false;

    public boolean isImmortal() { return _immortal; }

    public Doll(int x, int y, Board board) {
        super(x, y, board, Sprite.doll_dead, Game.getBomberSpeed() / 2, 100);

        _sprite = Sprite.doll_left1;

        _ai = new AILow();
        _direction = _ai.calculateDirection();
    }

    @Override
    public void update() {
        animate();

        if(!_alive) {
            afterKill();
            return;

        } else {
            if (_timeImmortal > 0) {
                --_timeImmortal;
            } else {
                _immortal = false;
            }
            calculateMove();
        }
    }

    @Override
    public void kill() {
        if(!_alive) return;

        if (_lives > 1) {
            --_lives;
            _timeImmortal = 180;
            _immortal = true;

        } else {
            _alive = false;
            _board.addPoints(_points);

            Message msg = new Message("+" + _points, getXMessage(), getYMessage(), 2, Color.white, 14);
            _board.addMessage(msg);
        }
    }

    @Override
    protected void chooseSprite() {
        switch(_direction) {
            case 0:
            case 1:
                if (_immortal) {
                    if(_moving)
                        _sprite = Sprite.movingSprite(Sprite.doll_right1, Sprite.empty, Sprite.doll_right2, Sprite.empty, Sprite.doll_right3, Sprite.empty, _animate, 60);
                    else
                        _sprite = Sprite.movingSprite(Sprite.doll_left2, Sprite.empty, Sprite.doll_right3, Sprite.empty, Sprite.doll_left1, Sprite.empty, _animate, 60);

                } else {
                    if (_moving)
                        _sprite = Sprite.movingSprite(Sprite.doll_right1, Sprite.doll_right2, Sprite.doll_right3, _animate, 60);
                    else
                        _sprite = Sprite.movingSprite(Sprite.doll_left2, Sprite.doll_right3, Sprite.doll_left1, _animate, 60);
                }
                break;
            case 2:
            case 3:
                if (_immortal) {
                    if(_moving)
                        _sprite = Sprite.movingSprite(Sprite.doll_left1, Sprite.empty, Sprite.doll_left2, Sprite.empty, Sprite.doll_left3, Sprite.empty, _animate, 60);
                    else
                        _sprite = Sprite.movingSprite(Sprite.doll_left1, Sprite.empty, Sprite.doll_right2, Sprite.empty, Sprite.doll_left3, Sprite.empty, _animate, 60);

                } else {
                    if (_moving)
                        _sprite = Sprite.movingSprite(Sprite.doll_left1, Sprite.doll_left2, Sprite.doll_left3, _animate, 60);
                    else
                        _sprite = Sprite.movingSprite(Sprite.doll_left1, Sprite.doll_right2, Sprite.doll_left3, _animate, 60);
                }
                break;
        }
    }
}

