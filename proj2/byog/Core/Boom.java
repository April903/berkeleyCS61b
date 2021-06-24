package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.io.Serializable;

public class Boom implements Serializable {

    private TETile[] frames = new TETile[4];
    private TETile current;
    private int x;
    private int y;
    private int idx;
    private int stage;

    public Boom (int x, int y, int stage) {
        frames[0] = Tileset.EXPLOSION0;
        frames[1] = Tileset.EXPLOSION1;
        frames[2] = Tileset.EXPLOSION2;
        frames[3] = Tileset.FLOOR;
        this.x = x;
        this.y = y;
        this.idx = 0;
        this.stage = stage;
    }

    public void setLastFrame(TETile frame) {
        frames[3] = frame;
    }

    public TETile[] getFrames() {
        return frames;
    }

    public TETile getCurrent() {
        return current;
    }

    public void setCurrent(TETile current) {
        this.current = current;
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public boolean update() {
        boolean u = false;
        idx++;
        if (idx >= 5) {
            stage++;
            if (stage >= 0 && stage < 4) {
                setCurrent(frames[stage]);
                u = true;
            }
            idx = 0;
        }

        return u;
    }
}
