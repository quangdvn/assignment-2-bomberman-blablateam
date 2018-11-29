package uet.oop.bomberman.entities.character.enemy;

import uet.oop.bomberman.Board;
import uet.oop.bomberman.Game;
import uet.oop.bomberman.entities.character.enemy.Enemy;
import uet.oop.bomberman.entities.character.enemy.ai.AIMedium;
import uet.oop.bomberman.graphics.Sprite;


public class Pontan extends Enemy {

    Board _board;

    public Pontan (int x, int y, Board board) {
        super(x, y, board, Sprite.pontan_dead, Game.getBomberSpeed(), 500);
        _board = board;
        _sprite = Sprite.pontan_left1;

        _ai = new AIMedium(_board.getBomber(), this, _board, true);
        _direction  = _ai.calculateDirection();

    }

    @Override
    protected void chooseSprite() {
        switch(_direction) {
            case 0:
            case 1:
                if(_moving)
                    _sprite = Sprite.movingSprite(Sprite.pontan_right1, Sprite.pontan_right2, Sprite.pontan_right3, _animate, 60);
                else
                    _sprite = Sprite.pontan_left1;
                break;
            case 2:
            case 3:
                if(_moving)
                    _sprite = Sprite.movingSprite(Sprite.pontan_left1, Sprite.pontan_left2, Sprite.pontan_left3, _animate, 60);
                else
                    _sprite = Sprite.pontan_left1;
                break;
        }
    }
}
