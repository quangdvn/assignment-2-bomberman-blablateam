package uet.oop.bomberman.level;

import uet.oop.bomberman.Board;
import uet.oop.bomberman.Game;
import uet.oop.bomberman.entities.LayeredEntity;
import uet.oop.bomberman.entities.character.Bomber;
import uet.oop.bomberman.entities.character.enemy.*;
import uet.oop.bomberman.entities.tile.Grass;
import uet.oop.bomberman.entities.tile.Portal;
import uet.oop.bomberman.entities.tile.Wall;
import uet.oop.bomberman.entities.tile.destroyable.Brick;
import uet.oop.bomberman.entities.tile.item.BombItem;
import uet.oop.bomberman.entities.tile.item.FlameItem;
import uet.oop.bomberman.entities.tile.item.SpeedItem;
import uet.oop.bomberman.exceptions.LoadLevelException;
import uet.oop.bomberman.graphics.Screen;
import uet.oop.bomberman.graphics.Sprite;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class FileLevelLoader extends LevelLoader {

	/**
	 * Ma trận chứa thông tin bản đồ, mỗi phần tử lưu giá trị kí tự đọc được
	 * từ ma trận bản đồ trong tệp cấu hình
	 */
	private static char[][] _map;

	public FileLevelLoader(Board board, double level) throws LoadLevelException {
		super(board, level);
	}

	@Override
	public void loadLevel(double level) throws LoadLevelException {

		try {
			URL absPath = FileLevelLoader.class.getResource("/levels/Level" + Double.toString(level) + ".txt");
			BufferedReader in = new BufferedReader( new InputStreamReader(absPath.openStream() ) );

			String data = in.readLine(); // Read every char in level file
			_level = Integer.parseInt(data.substring(0,1));	// Read the number of level
			_height = Integer.parseInt(data.substring(2,4));	// Read the height
			_width = Integer.parseInt(data.substring(5,7));	// Read the width

            _map = new char[_height][_width];
            for (int i = 0; i < _height; ++i) {
                data = in.readLine();
                for (int j = 0; j < _width; ++j) {
                    _map[i][j] = data.charAt(j);
                }
            }
			in.close();

		} catch (IOException e) {
			throw new LoadLevelException("Error loading level " + level, e);
		}
	}

	@Override
	public void createEntities() {

		for (int y = 0; y < _height ; y++ ) {
			for (int x = 0; x < _width ; x++) {

				int pos = x + y * _width;
				char printSprite = _map[y][x];

				switch (printSprite) {
					case '#':
						_board.addEntity(pos, new Wall(x,y,Sprite.wall));
						break;

					case '*':
						_board.addEntity(pos, new LayeredEntity(x, y, new Grass(x ,y, Sprite.grass), new Brick(x ,y, Sprite.brick)));
						break;

					case 'p':
						_board.addEntity(pos, new Grass(x, y, Sprite.grass));
						_board.addCharacter(new Bomber(Coordinates.tileToPixel(x), Coordinates.tileToPixel(y) + Game.TILES_SIZE, _board));
						Screen.setOffset(0, 0);
						break;

					case 'b':
						_board.addEntity(pos, new LayeredEntity(x, y, new Grass(x, y, Sprite.grass), new BombItem(x, y, Sprite.powerup_bombs), new Brick(x, y, Sprite.brick)));
						break;

					case 's':
						_board.addEntity(pos, new LayeredEntity(x, y, new Grass(x, y, Sprite.grass), new SpeedItem(x, y, Sprite.powerup_speed), new Brick(x, y, Sprite.brick)));
						break;

                    case 'f':
                        _board.addEntity(pos, new LayeredEntity(x, y, new Grass(x, y, Sprite.grass), new FlameItem(x, y, Sprite.powerup_flames), new Brick(x, y, Sprite.brick)));
                        break;

                    case 'x':
                        _board.addEntity(pos, new LayeredEntity(x, y, new Grass(x, y, Sprite.grass), new Portal(x, y, Sprite.portal, _board), new Brick(x, y, Sprite.brick)));
                        break;

					case '1':
						_board.addEntity(pos, new Grass(x, y, Sprite.grass));
						_board.addCharacter(new Balloon(Coordinates.tileToPixel(x), Coordinates.tileToPixel(y) + Game.TILES_SIZE, _board));
						break;

					case '2':
						_board.addEntity(pos, new Grass(x, y, Sprite.grass));
						_board.addCharacter(new Oneal(Coordinates.tileToPixel(x), Coordinates.tileToPixel(y) + Game.TILES_SIZE, _board));
						break;

					case '3':
						_board.addEntity(pos, new Grass(x, y, Sprite.grass));
						_board.addCharacter(new Doll(Coordinates.tileToPixel(x), Coordinates.tileToPixel(y) + Game.TILES_SIZE, _board));
						break;

					case '4':
						_board.addEntity(pos, new Grass(x, y, Sprite.grass));
						_board.addCharacter(new Minvo(Coordinates.tileToPixel(x), Coordinates.tileToPixel(y) + Game.TILES_SIZE, _board));
						break;

					case '5':
						_board.addEntity(pos, new Grass(x, y, Sprite.grass));
						_board.addCharacter(new Kondoria(Coordinates.tileToPixel(x), Coordinates.tileToPixel(y) + Game.TILES_SIZE, _board));
						break;

					case '6':
						_board.addEntity(pos, new Grass(x, y, Sprite.grass));
						_board.addCharacter(new Ovape(Coordinates.tileToPixel(x), Coordinates.tileToPixel(y) + Game.TILES_SIZE, _board));
						break;

					case '7':
						_board.addEntity(pos, new Grass(x, y, Sprite.grass));
						_board.addCharacter(new Pontan(Coordinates.tileToPixel(x), Coordinates.tileToPixel(y) + Game.TILES_SIZE, _board));
						break;

					case '8':
						_board.addEntity(pos, new Grass(x, y, Sprite.grass));
						_board.addCharacter(new Pass(Coordinates.tileToPixel(x), Coordinates.tileToPixel(y) + Game.TILES_SIZE, _board));
						break;

					default:
						_board.addEntity(pos, new Grass(x, y, Sprite.grass));
						break;
				}
			}
		}
	}

}
