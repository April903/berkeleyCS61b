package byog.TileEngine;

import java.awt.Color;

/**
 * Contains constant tile objects, to avoid having to remake the same tiles in different parts of
 * the code.
 *
 * You are free to (and encouraged to) create and add your own tiles to this file. This file will
 * be turned in with the rest of your code.
 *
 * Ex:
 *      world[x][y] = Tileset.FLOOR;
 *
 * The style checker may crash when you try to style check this file due to use of unicode
 * characters. This is OK.
 */

public class Tileset {
    public static final TETile PLAYER = new TETile('@', new Color(234, 0, 191), new Color(160, 160, 160), "player", 14);
    public static final TETile WALL = new TETile('#', new Color(64, 64, 64), new Color(153, 51, 255),
            "wall", 14);
    public static final TETile FLOOR = new TETile('·', new Color(178, 102, 255), new Color(160, 160, 160),
            "floor", 14);
    public static final TETile NOTHING = new TETile(' ', Color.black, Color.black, "nothing", 14);
    public static final TETile GRASS = new TETile('▒', Color.green, new Color(160, 160, 160), "grass", 14);
    public static final TETile WATER = new TETile('≈', Color.blue, Color.black, "water", 14);
    public static final TETile FLOWER = new TETile('❀', Color.magenta, new Color(160, 160, 160), "flower" ,14);
    public static final TETile LOCKED_DOOR = new TETile('█', Color.orange, Color.black,
            "locked door", 14);
    public static final TETile UNLOCKED_DOOR = new TETile('▢', Color.orange, Color.black,
            "unlocked door", 14);
    public static final TETile SAND = new TETile('▒', Color.yellow, Color.black, "sand", 14);
    public static final TETile MOUNTAIN = new TETile('▲', Color.gray, Color.black, "mountain", 14);
    public static final TETile TREE = new TETile('♠', Color.green, Color.black, "tree", 14);

    public static final TETile BOMB = new TETile('o', Color.black, new Color(160, 160, 160), "bomb", 14);
    public static final TETile EXPLOSION0 = new TETile('✸', Color.yellow, new Color(160, 160, 160), "Exploding", 12);
    public static final TETile EXPLOSION1 = new TETile('✹', Color.yellow, new Color(160, 160, 160), "Exploding", 14);
    public static final TETile EXPLOSION2 = new TETile('✺', Color.yellow, new Color(160, 160, 160), "Exploding", 16);
    public static final TETile CHASERUP = new TETile('^', Color.yellow, new Color(160, 160, 160), "Chaser", 16);
    public static final TETile CHASERDOWN = new TETile('v', Color.yellow, new Color(160, 160, 160), "Chaser", 16);
    public static final TETile CHASERLEFT = new TETile('<', Color.yellow, new Color(160, 160, 160), "Chaser", 16);
    public static final TETile CHASERRIGHT = new TETile('>', Color.yellow, new Color(160, 160, 160), "Chaser", 16);

    public static final TETile COIN = new TETile('●', Color.yellow, new Color(160, 160, 160), "Coin", 16);
}


