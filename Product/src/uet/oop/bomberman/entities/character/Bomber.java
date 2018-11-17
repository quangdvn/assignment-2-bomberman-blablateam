package uet.oop.bomberman.entities.character;

import uet.oop.bomberman.Board;
import uet.oop.bomberman.Game;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.entities.bomb.Bomb;
import uet.oop.bomberman.entities.bomb.Flame;
import uet.oop.bomberman.entities.bomb.FlameSegment;
import uet.oop.bomberman.entities.character.enemy.Enemy;
import uet.oop.bomberman.entities.tile.destroyable.Brick;
import uet.oop.bomberman.graphics.Screen;
import uet.oop.bomberman.graphics.Sprite;
import uet.oop.bomberman.input.Keyboard;
import uet.oop.bomberman.level.Coordinates;

import java.util.Iterator;
import java.util.List;

public class Bomber extends Character {

    private List<Bomb> _bombs;
    protected Keyboard _input;

    /**
     * nếu giá trị này < 0 thì cho phép đặt đối tượng Bomb tiếp theo,
     * cứ mỗi lần đặt 1 Bomb mới, giá trị này sẽ được reset về 0 và giảm dần trong mỗi lần update()
     */
    protected int _timeBetweenPutBombs = 0;

    public Bomber(int x, int y, Board board) {
        super(x, y, board);
        _bombs = _board.getBombs();
        _input = _board.getInput();
        _sprite = Sprite.player_right;
    }

    @Override
    public void update() {
        clearBombs();
        if (!_alive) {
            afterKill();
            return;
        }

        if (_timeBetweenPutBombs < -7500) _timeBetweenPutBombs = 0;
        else _timeBetweenPutBombs--;

        animate();

        calculateMove();

        detectPlaceBomb();
    }

    @Override
    public void render(Screen screen) {
        calculateXOffset();

        if (_alive)
            chooseSprite();
        else
            _sprite = Sprite.player_dead1;

        screen.renderEntity((int) _x, (int) _y - _sprite.SIZE, this);
    }

    public void calculateXOffset() {
        int xScroll = Screen.calculateXOffset(_board, this);
        Screen.setOffset(xScroll, 0);
    }

    /**
     * Detect if a bomb can be placed
     */
    private void detectPlaceBomb() {
        /**
         * Game.getBombRate(): get the Total number of bomb can place one time
         * _timeBetweenPutBombs: return the time between 2 consecutive bombs
         * Bomb Rate must be -1 and reset _timeBetweenPutBombs after placing 1 bomb
         */
        if(_input.space && Game.getBombRate() > 0 && _timeBetweenPutBombs < 0) {
            int curX = Coordinates.pixelToTile(_x + _sprite.getSize() / 2);
            int curY = Coordinates.pixelToTile( (_y + _sprite.getSize() / 2) - _sprite.getSize() ); //subtract half player height and minus 1 y position
            placeBomb(curX,curY);
            Game.addBombRate(-1);
            _timeBetweenPutBombs = 30;
        }
    }

    /*
        Create a bomb at (x, y)
     */
    protected void placeBomb(int x, int y) {
        Bomb b = new Bomb(x, y, _board);
        _board.addBomb(b);
    }
    /*
        Reset a number of current bomb
     */
    private void clearBombs() {
        Iterator<Bomb> bs = _bombs.iterator();

        Bomb b;
        while (bs.hasNext()) {
            b = bs.next();
            if (b.isRemoved()) {
                bs.remove();
                Game.addBombRate(1);
            }
        }

    }

    @Override
    public void kill() {
        if (!_alive) return;
        _alive = false;
    }

    @Override
    protected void afterKill() {
        if (_timeAfter > 0) --_timeAfter;
        else {
            _board.endGame();
        }
    }

    @Override
    protected void calculateMove() {
        int _xNow = 0,_yNow = 0;

        if(_input.up) _yNow--;
        else if(_input.down) _yNow++;
        else if(_input.left) _xNow--;
        else if(_input.right) _xNow++;

        if(_xNow != 0 || _yNow != 0) {
            move(_xNow * Game.getBomberSpeed(), _yNow * Game.getBomberSpeed());
            _moving = true;
        } else {
            _moving = false;
        }
    }

    @Override
    public boolean canMove(double x, double y) {
        for (int dx = 0; dx < 2; ++dx) {
            for (int dy = 0; dy < 2; ++dy) {
                double _xNext = ((_x + x) + dx * 11) / Game.TILES_SIZE;
                double _yNext = ((_y + y) + dy * 12 - 14) / Game.TILES_SIZE;

                Entity a = _board.getEntity(_xNext, _yNext, this);

                if (!a.collide(this)) return false;
            }
        }
        return true;
    }

    @Override
    public void move(double _xNow, double _yNow) {
        if(_xNow > 0) _direction = 1;   // Go right
        if(_xNow < 0) _direction = 3;   // Go left
        if(_yNow > 0) _direction = 2;   // Go up
        if(_yNow < 0) _direction = 0;   // Go down

        if(canMove(0, _yNow)) _y += _yNow;
        if(canMove(_xNow, 0)) _x += _xNow;
    }

    @Override
    public boolean collide(Entity e) {
        if(e instanceof Flame) {
            kill();
        }
        if(e instanceof FlameSegment) {
            kill();
        }
        if(e instanceof Enemy) {
            kill();
        }
        return true;
    }

    private void chooseSprite() {
        switch (_direction) {
            case 0:
                _sprite = Sprite.player_up;
                if (_moving) {
                    _sprite = Sprite.movingSprite(Sprite.player_up_1, Sprite.player_up_2, _animate, 20);
                }
                break;
            case 1:
                _sprite = Sprite.player_right;
                if (_moving) {
                    _sprite = Sprite.movingSprite(Sprite.player_right_1, Sprite.player_right_2, _animate, 20);
                }
                break;
            case 2:
                _sprite = Sprite.player_down;
                if (_moving) {
                    _sprite = Sprite.movingSprite(Sprite.player_down_1, Sprite.player_down_2, _animate, 20);
                }
                break;
            case 3:
                _sprite = Sprite.player_left;
                if (_moving) {
                    _sprite = Sprite.movingSprite(Sprite.player_left_1, Sprite.player_left_2, _animate, 20);
                }
                break;
            default:
                _sprite = Sprite.player_right;
                if (_moving) {
                    _sprite = Sprite.movingSprite(Sprite.player_right_1, Sprite.player_right_2, _animate, 20);
                }
                break;
        }
    }
}
