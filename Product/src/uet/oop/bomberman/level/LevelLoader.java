package uet.oop.bomberman.level;

import uet.oop.bomberman.Board;
import uet.oop.bomberman.exceptions.LoadLevelException;

/**
 * Load và lưu trữ thông tin bản đồ các màn chơi
 */
public abstract class LevelLoader {

	protected int _width = 20, _height = 20; // default values just for testing
	protected int _level;
	protected String[] _lineTiles; // brick to build, I guess :))
	protected Board _board;		   // current screen to add sprites in, I guess :))

	public LevelLoader(Board board, double level) throws LoadLevelException {
		_board = board;
		loadLevel(level);
	}

	public abstract void loadLevel(double level) throws LoadLevelException;

	public abstract void createEntities();

	public int getWidth() {
		return _width;
	}

	public int getHeight() {
		return _height;
	}

	public int getLevel() {
		return _level;
	}

}
