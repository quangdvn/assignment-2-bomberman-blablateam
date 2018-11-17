package uet.oop.bomberman.entities.character.enemy;

import uet.oop.bomberman.Board;
import uet.oop.bomberman.Game;
import uet.oop.bomberman.entities.character.enemy.ai.AILow;
import uet.oop.bomberman.graphics.Sprite;

public class Kondoria extends Enemy {

    /**
     * This one can deactive obstacles :((
     * Also reward point for destroy and Speed
     * @param x Location x
     * @param y Location y
     * @param board Place to draw
     */
    public Kondoria(int x, int y, Board board) {
        super(x, y, board, Sprite.kondoria_dead, Game.getBomberSpeed() / 2, 100);

        _sprite = Sprite.kondoria_left1;

        _ai = new AILow(this);
        _direction = _ai.calculateDirection();
    }
    @Override
    protected void chooseSprite() {
        switch(_direction) {
            case 0:
            case 1:
                if(_moving)
                    _sprite = Sprite.movingSprite(Sprite.kondoria_right1, Sprite.kondoria_right2, Sprite.kondoria_right3, _animate, 60);
                else
                    _sprite = Sprite.movingSprite(Sprite.kondoria_left1, Sprite.kondoria_right1, Sprite.kondoria_left2, _animate, 60);
                break;
            case 2:
            case 3:
                if(_moving)
                    _sprite = Sprite.movingSprite(Sprite.kondoria_left1, Sprite.kondoria_left2, Sprite.kondoria_left3, _animate, 60);
                else
                    _sprite = Sprite.movingSprite(Sprite.kondoria_left2, Sprite.kondoria_right2, Sprite.kondoria_left1, _animate, 60);
                break;
        }
    }
}
