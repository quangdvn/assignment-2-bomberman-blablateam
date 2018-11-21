package uet.oop.bomberman.entities.character.enemy.ai;

import uet.oop.bomberman.Board;
import uet.oop.bomberman.Game;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.entities.LayeredEntity;
import uet.oop.bomberman.entities.bomb.Bomb;
import uet.oop.bomberman.entities.bomb.Flame;
import uet.oop.bomberman.entities.bomb.FlameSegment;
import uet.oop.bomberman.entities.character.Bomber;
import uet.oop.bomberman.entities.character.enemy.Enemy;
import uet.oop.bomberman.entities.tile.Wall;
import uet.oop.bomberman.entities.tile.destroyable.Brick;

import java.util.*;

public class AIMedium extends AI {

    Bomber _bomber;
    Enemy _e;
    Board _board;
    int direction;

    final int dx[] = {0, 1, -1, 0};
    final int dy[] = {1, 0, 0, -1};

    int[] fValue, gValue;
    int[] cameFrom;
    boolean[] visited;
    List<Node> visitedNodes = new ArrayList<>();

    final int MAX_ARR = 13 * 31;

    public AIMedium(Bomber bomber, Enemy e, Board board) {
        _bomber = bomber;
        _e = e;
        _board = board;

        fValue = new int[MAX_ARR];
        gValue = new int[MAX_ARR];
        cameFrom = new int[MAX_ARR];
        visited = new boolean[MAX_ARR];

        for (int i = 0; i < MAX_ARR; ++i) {
            cameFrom[i] = -1;
            gValue[i] = fValue[i] = MAX_ARR;
            visited[i] = false;
        }
    }

    int position(int xPos, int yPos) {
        return yPos * 31 + xPos;
    }

    int heuristicValue(int xPos, int yPos) {
        return Math.abs(xPos - _bomber.getXTile()) + Math.abs(yPos - _bomber.getYTile());
    }

    int isBlocked(int xPos, int yPos) {
        if (xPos < 0 || yPos < 0) return 1;
        if (xPos >= 31 || yPos >= 13) return 1;

        Entity e = _board.getEntity(xPos, yPos, _bomber);

        if (e instanceof Wall) {
            return 1;
        }
        if (e instanceof Bomb) {
            return 2;
        }
        if (e instanceof Flame || e instanceof FlameSegment) {
            return 1;
        }
        if (e instanceof LayeredEntity) {
            LayeredEntity layerE = (LayeredEntity)e;

            if (layerE.getTopEntity() instanceof Brick) {
                return 1;
            }
        }
        return 0;
    }

    private class Node implements Comparable {
        public int xPos, yPos;

        Node(int xPos, int yPos) {
            this.xPos = xPos;
            this.yPos = yPos;
        }

        public int compareTo(Object o) {
            Node p = (Node)o;
            return fValue[position(xPos, yPos)] - fValue[position(p.xPos, p.yPos)];
        }
    }

    int AStar() {

        if (_bomber.getXTile() == _e.getXTile() && _bomber.getYTile() == _e.getYTile()) {
            return -1;
        }

        PriorityQueue <Node> pQueue = new PriorityQueue<>();
        int has_bomb = -1;

        int xSource = _e.getXTile();
        int ySource = _e.getYTile();
        Node source = new Node(xSource, ySource);

        pQueue.add(source);
        visitedNodes.add(source);
        gValue[position(xSource, ySource)] = 0;
        visited[position(xSource, ySource)] = true;

        while (!pQueue.isEmpty()) {
            Node current = pQueue.poll();

            if (current.xPos == _bomber.getXTile() && current.yPos == _bomber.getYTile()) {
                return tracePath(current.xPos, current.yPos);
            }

            for (int i = 0; i < 4; ++i) {
                int nextX = current.xPos + dx[i];
                int nextY = current.yPos + dy[i];
                int nextPos = position(nextX, nextY);
                int newGValue = gValue[position(current.xPos, current.yPos)] + 1;

                int blocked = isBlocked(nextX, nextY);
                if (blocked > 0) {
                    if (blocked == 2) has_bomb = nextPos;
                    continue;
                }
                if (visited[nextPos] && newGValue > gValue[nextPos]) {
                    continue;
                }

                cameFrom[nextPos] = position(current.xPos, current.yPos);
                gValue[nextPos] = newGValue;
                fValue[nextPos] = newGValue + heuristicValue(nextX, nextY);

                if (!visited[nextPos]) {
                    Node nextNode = new Node(nextX, nextY);
                    pQueue.add(nextNode);
                    visitedNodes.add(nextNode);
                    visited[nextPos] = true;
                }
            }
        }

        // easier AI
        /*
        int rand = random.nextInt(4);
        return position(xSource + dx[rand], ySource + dy[rand]);
        */

        // impossible AI
        return findPathIfBlocked(has_bomb);

    }

    int findPathIfBlocked(int has_bomb) {

        int[] adjNodes = new int[4];
        for (int i = 0; i < 4; ++i) {
            adjNodes[i] = position(_e.getXTile() + dx[i], _e.getYTile() + dy[i]);
        }

        for (int i = 0; i < 3; ++i) {
            for (int j = i + 1; j < 4; ++j) {
                if (fValue[adjNodes[i]] > fValue[adjNodes[j]]){
                    int temp = adjNodes[i];
                    adjNodes[i] = adjNodes[j];
                    adjNodes[j] = temp;
                }
            }
        }

        if (has_bomb == -1) {
            return adjNodes[0];
        }

        int xBomb = has_bomb % 31;
        int yBomb = has_bomb / 31;
        int nextPos = -2;

        for (int i = 0; i < 4; ++i) {
            int nextX = adjNodes[i] % 31;
            int nextY = adjNodes[i] / 31;

            if (nextX == xBomb && Math.abs(nextY - yBomb) <= Game.getBombRadius()) {
                continue;
            }
            if (nextY == yBomb && Math.abs(nextX - xBomb) <= Game.getBombRadius()) {
                continue;
            }
            nextPos = adjNodes[i];
            break;
        }

        return nextPos;
    }

    int tracePath(int xPos, int yPos) {
        int currentPos = position(xPos, yPos);
        while (cameFrom[cameFrom[currentPos]] != -1) {
            currentPos = cameFrom[currentPos];
        }
        return currentPos;
    }

    void reset() {
        for (Node current: visitedNodes) {
            int currentPos = position(current.xPos, current.yPos);
            fValue[currentPos] = MAX_ARR;
            gValue[currentPos] = MAX_ARR;
            cameFrom[currentPos] = -1;
            visited[currentPos] = false;
        }
        visitedNodes.clear();
    }

	@Override
	public int calculateDirection() {
        int nextPos = AStar();
        reset();

        if (nextPos == -1) {
            return direction;
        }
        if (nextPos == -2) {
            return -1;
        }

        int nextXPos = nextPos % 31, nextYPos = nextPos / 31;

        if (nextXPos > _e.getXTile()) {
            direction = 1;

        } else if (nextXPos < _e.getXTile()) {
            direction = 3;

        } else if (nextYPos < _e.getYTile()) {
            direction = 0;

        } else {
            direction = 2;
        }
        return direction;
	}

}
