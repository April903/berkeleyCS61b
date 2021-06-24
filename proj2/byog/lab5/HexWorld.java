package byog.lab5;
import org.junit.Test;
import static org.junit.Assert.*;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {

    private int WIDTH;
    private int HEIGHT;
    private int UNITSIZE;
    private TETile[][] world;
    private final int[] cols = new int[] {3, 4, 5, 4, 3};
    private static final long SEED = 2873123;
    private static final Random RANDOM = new Random(SEED);

    public HexWorld(int s) {
        WIDTH = 3 * s - 2 + 4 * (s + s - 1);
        HEIGHT = 10 * s;
        UNITSIZE = s;

        world = new TETile[WIDTH][HEIGHT];

        for (int x = 0; x < HEIGHT; x += 1) {
            for (int y = 0; y < WIDTH; y += 1) {
                world[y][x] = Tileset.NOTHING;
            }
        }
        generateHexagons(s);
    }

    /**
     * generates Hexagons of specified size in a large enough world;
     * the size (number of hexagons) of the hexagon columns is 3, 4, 5, 4, 3
     * @param s
     */
    public void generateHexagons(int s) {
        int num = 0;
        int colNum = 0;
        int curr = cols[colNum];

        int x = 0 + (5 - curr) * UNITSIZE;
        int y = 0;

        while (colNum < 5) {
            while (num < curr) {
                addHexagon(UNITSIZE, x, y, randomTile());
                num++;
                x += 2 * UNITSIZE;
            }
            colNum++;

            if (colNum < 5) {
                curr = cols[colNum];
                num = 0;
                x = 0 + (5 - curr) * UNITSIZE;
                y += 2 * s - 1;
            }
        }
    }

    public TETile randomTile() {
        int tileNum = RANDOM.nextInt(6);
        switch (tileNum) {
            case 0: return Tileset.MOUNTAIN;
            case 1: return Tileset.FLOWER;
            case 2: return Tileset.GRASS;
            case 3: return Tileset.WATER;
            case 4: return Tileset.SAND;
            case 5: return Tileset.TREE;
            default: return Tileset.NOTHING;
        }
    }

    public void addHexagon(int s, int x, int y, TETile t) {
        for (int i = x; i < x + 2 * s; i++) {
            generateLine(s, x, i, y, i - x, t);
        }
    }

    public void generateLine(int s, int firstRow, int row, int start, int rowNum, TETile t) {
        if (rowNum < s) {
            for (int i = start + (s - 1 - rowNum); i < start + (s - 1 - rowNum) + s + 2 * rowNum; i++) {
                world[i][row] = TETile.colorVariant(t, 30, 30, 30, RANDOM);
            }
        }

        else {
            for (int i = start + rowNum - s; i < start + rowNum - s + 3 * s - 2 - 2 * (rowNum - s); i++) {
                int same = firstRow * 2 + 2 * s - 1 - row;
                world[i][row] = world[i][same];
            }
        }

    }

    public void show() {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH + 4, HEIGHT + 4, 2, 2);
        ter.renderFrame(world);
    }

    public static void main(String[] args) {
        HexWorld hw = new HexWorld(4);
        hw.show();
    }
}
