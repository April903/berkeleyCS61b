package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.io.Serializable;

public class Chaser implements Serializable {

    private int[] dx = new int[] {0, 0, 1, -1};
    private int[] dy = new int[] {1, -1, 0, 0};

    private int mode = 1;
    private int initialX;
    private int initialY;
    private int x;
    private int y;
    private int xD;
    private int yD;
    private int idx;
    private int change; // the timing for changing the target
    private int health = 1;
    private Player p;
    private Position target;
    private Position lastPos;
    private TETile lastStep = Tileset.FLOOR;
    private TETile current;
    private TETile[] frames = new TETile[4];
    private TETile[][] world;

    public Chaser (int x, int y, int xD, int yD, Position p, TETile[][] world) {
        initialX = x;
        initialY = y;
        this.x = x;
        this.y = y;
        this.xD = xD;
        this.yD = yD;
        this.target = p;
        this.world = world;
        frames[0] = Tileset.CHASERUP;
        frames[1] = Tileset.CHASERDOWN;
        frames[2] = Tileset.CHASERRIGHT;
        frames[3] = Tileset.CHASERLEFT;
        if (xD == 1) {
            current = frames[2];
        }

        else if (xD == -1) {
            current = frames[3];
        }

        else if (yD == 1) {
            current = frames[0];
        }
        else if (yD == -1) {
            current = frames[1];
        }
        lastPos = new Position(x, y);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int h) {
        health = h;
    }

    public int getInitialX() {
        return initialX;
    }

    public int getInitialY() {
        return initialY;
    }

    public int getMode() {
        return mode;
    }

    public void setTarget(int x, int y) {
        target.setX(x);
        target.setY(y);
    }

    public TETile getCurrent() {
        return current;
    }

    public void update() {
        idx++;
        change++;
        if (change >= 1000) {
            change = 0;
            if (mode == 1) {
                setMode(0);
            }
            else {
                setMode(1);
            }
        }

        if (idx >= 12) {
            idx = 0;
            move();
        }

    }

    public void move() {
        int nx;
        int ny;
        int ndx;
        int ndy;
        int xDiff;
        int yDiff;
        double currDist;
        double minDist = 110;
        int minI = -1;

        int minIdx = -1;
        int backIdx = -1;

        for (int i = 0; i < 4; i++) {
            nx = x + dx[i];
            ny = y + dy[i];

            if (world[nx][ny].equals(Tileset.WALL) || world[nx][ny].equals(Tileset.GRASS) || world[nx][ny].equals(Tileset.LOCKED_DOOR)) {
                continue;
            }
            if ((dx[i] != 0 && dx[i] == -xD)|| (dy[i] != 0 && dy[i] == -yD)) {
                backIdx = i;
                continue;
            }

            else {
                xDiff = nx - target.getX();
                yDiff = ny - target.getY();
                currDist = Math.sqrt(xDiff * xDiff + yDiff * yDiff);
                if (currDist < minDist) {
                    minDist = currDist;
                    minI = i;
                }
            }
        }

        if (minI != -1) {
            nx = x + dx[minI];
            ny = y + dy[minI];
            ndx = dx[minI];
            ndy = dy[minI];
            minIdx = minI;
        }
        else {
            nx = x + dx[backIdx];
            ny = y + dy[backIdx];
            ndx = dx[backIdx];
            ndy = dy[backIdx];
            minIdx = backIdx;
        }
        int lx = lastPos.getX();
        int ly = lastPos.getY();
        if (isExplosion(world[lx][ly])) {
            world[x][y] = Tileset.FLOOR;
        }
        else if (isChaser(world[lx][ly])) {
            if (lastStep != Tileset.PLAYER) {
                world[x][y] = lastStep;
            }
            else {
                world[x][y] = Tileset.FLOOR;
            }
        }
        else {
            world[x][y] = world[lx][ly];
        }

        lastPos.setX(nx);
        lastPos.setY(ny);
        lastStep = world[nx][ny];
        setX(nx);
        setY(ny);
        setxD(ndx);
        setyD(ndy);

        current = frames[minIdx];
        world[nx][ny] = frames[minIdx];

    }

    private boolean isExplosion(TETile t) {
        if (t.equals(Tileset.EXPLOSION0)) {
            return true;
        }

        if (t.equals(Tileset.EXPLOSION1)) {
            return true;
        }

        if (t.equals(Tileset.EXPLOSION2)) {
            return true;
        }

        return false;
    }

    private boolean isChaser(TETile t) {
        if (t.equals(Tileset.CHASERDOWN)) {
            return true;
        }

        if (t.equals(Tileset.CHASERUP)) {
            return true;
        }

        if (t.equals(Tileset.CHASERLEFT)) {
            return true;
        }

        if (t.equals(Tileset.CHASERRIGHT)) {
            return true;
        }

        return false;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setxD(int xD) {
        this.xD = xD;
    }

    public void setyD(int yD) {
        this.yD = yD;
    }

    public void setMode(int m) {
        mode = m;
    }
}
