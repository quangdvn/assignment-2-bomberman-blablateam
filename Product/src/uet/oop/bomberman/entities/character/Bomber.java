package uet.oop.bomberman.entities.character;

import uet.oop.bomberman.Board;
import uet.oop.bomberman.Game;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.entities.bomb.Bomb;
import uet.oop.bomberman.entities.bomb.Flame;
import uet.oop.bomberman.entities.bomb.FlameSegment;
import uet.oop.bomberman.entities.character.enemy.Enemy;
import uet.oop.bomberman.entities.tile.item.Item;
import uet.oop.bomberman.graphics.Screen;
import uet.oop.bomberman.graphics.Sprite;
import uet.oop.bomberman.input.Keyboard;
import uet.oop.bomberman.level.Coordinates;
import uet.oop.bomberman.sound.Sound;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Bomber extends Character {

    private List<Bomb> _bombs;
    protected Keyboard _input;
    public static List<Item> powerUp = new ArrayList<>();
    /**
     * nếu giá trị này < 0 thì cho phép đặt đối tượng Bomb tiếp theo,
     * cứ mỗi lần đặt 1 Bomb mới, giá trị này sẽ được reset về 0 và giảm dần trong mỗi lần update()
     */
    protected int _timeBetweenPutBombs = 0;
    protected int _timeImmortal = 0;
    protected boolean _immortal = false;

    public boolean isImmortal() {
        return _immortal;
    }

    public Bomber(int x, int y, Board board) {
        super(x, y, board);
        _bombs = _board.getBombs();
        _input = _board.getInput();
        _sprite = Sprite.player_right;
        _timeAfter = 25;
    }

    @Override
    public void update() {
        clearBombs();

        animate();

        if (!_alive) {
            afterKill();
            return;
        }
        if (_timeImmortal > 0) {
            --_timeImmortal;
        } else {
            _immortal = false;
        }

        if (_timeBetweenPutBombs < 0) _timeBetweenPutBombs = 0;
        else _timeBetweenPutBombs--;

        calculateMove();

        detectPlaceBomb();
    }

    @Override
    public void render(Screen screen) {
        calculateXOffset();

        if (_alive)
            chooseSprite();
        else
            _sprite = Sprite.movingSprite(Sprite.player_dead1, Sprite.player_dead2, Sprite.player_dead3, _animate,30);

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
            _timeBetweenPutBombs = 10;
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

    public void addPowerup(Item item) {
        if(item.isRemoved()) return;
        
        powerUp.add(item);
        item.setPower();
    }

    @Override
    public void kill() {
        if (!_alive) return;
        Game.setBomberLives(Game.getBomberLives() - 1);
        if (Game.getBomberLives() > 0) Sound.play(Sound.lifeLost);

        _alive = false;
        _animate = 0;
    }


    @Override
    protected void afterKill() {
        if (_timeAfter > 0) --_timeAfter;
        else {
            if (Game.getBomberLives() > 0) {
                _alive = true;
                _timeAfter = 25;
                _immortal = true;
                _timeImmortal = 240;

            } else {
                _board.endGame();
            }
        }
    }

    @Override
    protected void calculateMove() {
        double _speed = Game.getBomberSpeed();

        if (_input.up) {
            move(0, -_speed);
            _moving = true;

        } else if (_input.down) {
            move(0, _speed);
            _moving = true;

        } else if (_input.left) {
            move(-_speed, 0);
            _moving = true;

        } else if (_input.right) {
            move(_speed, 0);
            _moving = true;

        } else {
            _moving = false;
        }
    }

    @Override
    public boolean canMove(double x, double y) {
        int xTile = Coordinates.pixelToTile(x);
        int yTile = Coordinates.pixelToTile(y);

        Entity e = _board.getEntity(xTile, yTile, this);
        return e.collide(this);
    }

    public void fixPosition() {

        double xMid = _x + _sprite.getRealWidth() / 2;
        double yMid = _y - _sprite.getRealHeight() / 2;

        int xTile = Coordinates.pixelToTile(xMid);
        int yTile = Coordinates.pixelToTile(yMid);

        if (_direction != 0 && !canMove(xMid, yMid + Game.TILES_SIZE / 2)) {
            _y = Coordinates.tileToPixel(yTile) + Game.TILES_SIZE / 2 + _sprite.getRealHeight() / 2;
        }
        if (_direction != 1 && !canMove(xMid - Game.TILES_SIZE / 2, yMid)) {
            _x = Coordinates.tileToPixel(xTile) + Game.TILES_SIZE / 2 - _sprite.getRealWidth() / 2;
        }
        if (_direction != 2 && !canMove(xMid, yMid - Game.TILES_SIZE / 2)) {
            _y = Coordinates.tileToPixel(yTile) + Game.TILES_SIZE / 2 + _sprite.getRealHeight() / 2;
        }
        if (_direction != 3 && !canMove(xMid + Game.TILES_SIZE / 2, yMid)) {
            _x = Coordinates.tileToPixel(xTile) + Game.TILES_SIZE / 2 - _sprite.getRealWidth() / 2;
        }
    }

    @Override
    public void move(double dx, double dy) {

        if (dx > 0) _direction = 1;
        if (dx < 0) _direction = 3;
        if (dy > 0) _direction = 2;
        if (dy < 0) _direction = 0;

        if (canMove(_x + _sprite.getRealWidth() / 2 + dx, _y - _sprite.getRealHeight() / 2 + dy)) {
            _x += dx;
            _y += dy;
        }
        fixPosition();
    }

    @Override
    public boolean collide(Entity e) {
        if(e instanceof Flame) {
            if (_immortal) {
                return true;

            } else {
                kill();
            }
        }
        if(e instanceof FlameSegment) {
            if (_immortal) {
                return true;

            } else {
                kill();
            }
        }
        if(e instanceof Enemy) {
            if (_immortal) {
                return false;

            } else {
                kill();
            }
        }
        return true;
    }

    private void chooseSprite() {
        switch (_direction) {
            case 0:
                if (!_immortal) {
                    _sprite = Sprite.player_up;
                    if (_moving) {
                        _sprite = Sprite.movingSprite(Sprite.player_up_1, Sprite.player_up_2, _animate, 20);
                    }
                } else {
                    _sprite = Sprite.movingSprite(Sprite.player_up, Sprite.empty, _animate, 20);
                    if (_moving) {
                        _sprite = Sprite.movingSprite(Sprite.player_up_1, Sprite.empty, Sprite.player_up_2, Sprite.empty, _animate, 40);
                    }
                }
                break;
            case 1:
                if (!_immortal) {
                    _sprite = Sprite.player_right;
                    if (_moving) {
                        _sprite = Sprite.movingSprite(Sprite.player_right_1, Sprite.player_right_2, _animate, 20);
                    }
                } else {
                    _sprite = Sprite.movingSprite(Sprite.player_right, Sprite.empty, _animate, 20);
                    if (_moving) {
                        _sprite = Sprite.movingSprite(Sprite.player_right_1, Sprite.empty, Sprite.player_right_2, Sprite.empty, _animate, 40);
                    }
                }
                break;
            case 2:
                if (!_immortal) {
                    _sprite = Sprite.player_down;
                    if (_moving) {
                        _sprite = Sprite.movingSprite(Sprite.player_down_1, Sprite.player_down_2, _animate, 20);
                    }
                } else {
                    _sprite = Sprite.movingSprite(Sprite.player_down, Sprite.empty, _animate, 20);
                    if (_moving) {
                        _sprite = Sprite.movingSprite(Sprite.player_down_1, Sprite.empty, Sprite.player_down_2, Sprite.empty, _animate, 40);
                    }
                }
                break;
            case 3:
                if (!_immortal) {
                    _sprite = Sprite.player_left;
                    if (_moving) {
                        _sprite = Sprite.movingSprite(Sprite.player_left_1, Sprite.player_left_2, _animate, 20);
                    }
                } else {
                    _sprite = Sprite.movingSprite(Sprite.player_left, Sprite.empty, _animate, 20);
                    if (_moving) {
                        _sprite = Sprite.movingSprite(Sprite.player_left_1, Sprite.empty, Sprite.player_left_2, Sprite.empty, _animate, 40);
                    }
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
