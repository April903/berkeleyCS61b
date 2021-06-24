package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;
import java.awt.Font;
import java.awt.Color;

import java.io.*;
import java.util.*;

public class Game {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    public static final int RECTANGLELOWER = 2; // minimum length of the side of a rectangle room
    public static final int RECTANGLEUPPER = 6; // maximum length of the side of a rectangle room
    public static final int HEXAGONLOWER = 2; // minimum length of the side of a hexagon room
    public static final int HEXAGONUPPER = 4; // maximum length of the side of a hexagon room
    public static final int BOOMCOUNTDOWN = -20;
    public static final Font BIG = new Font("Monaco", Font.BOLD, 32);
    public static final Font SMALL = new Font("Monaco", Font.BOLD, 16);

    public static Set<Character> modeSet = new HashSet<>();

    private Random r;
    private long seed;
    private enum STATE {
        MENU, // main menu;
        PROMPT, // asking for seed and play mode when starting a new game;
        BETWEEN, // asking after winning a game;
        GAME, // playing mode
        READONLY, // shows the world without allowing the user to play and make modifications
        SAVED,
        OVER
    }

    private STATE state;
    private boolean gameOn = false;
    private Controller controller;
    private Player p;
    private LinkedList<Boom> boomList = new LinkedList<>();
    private LinkedList<Chaser> chaserList = new LinkedList<>();
    private TETile[][] world = new TETile[WIDTH][HEIGHT];
    private TETile[][] rooms = new TETile[WIDTH][HEIGHT];
    private TETile[][] hallways = new TETile[WIDTH][HEIGHT];
    private int[][] grass = new int[WIDTH][HEIGHT];

    private HashMap<String, Position> flower = new HashMap<>();
    private TETile lastStep = Tileset.FLOOR;
    private Position lastPos;
    private Position[] locations;
    private Position door;
    private ArrayList<int[]> wallList = new ArrayList<>();
    private boolean doorSet = false;
    private boolean playerSet = false;
    private boolean colliding = false;
    private String quit = "";
    private static final int[] dx = new int[] {0, 0, 1, -1, 1, -1, 1, -1};
    private static final int[] dy = new int[] {1, -1, 0, 0, 1, -1, -1, 1};

    /**
     * Method used for playing a fresh game. The game should start from the main menu.
     */
    public void playWithKeyboard() {
        StdDraw.setCanvasSize(WIDTH * 16, HEIGHT * 16);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.clear(new Color(0, 0, 0));
        StdDraw.enableDoubleBuffering();

        state = STATE.MENU;
        mouseRender();
        long s = 0;
        StdDraw.setFont(BIG);
        StdDraw.setPenColor(Color.white);
        StdDraw.enableDoubleBuffering();
        initialize(modeSet);
        while (true) {

            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }

            char i = StdDraw.nextKeyTyped();

            if (state == STATE.MENU) {
                if (!modeSet.contains(i)) {
                    StdDraw.setFont(SMALL);
                    StdDraw.text(40, 14, "Please enter a valid mode : N / L / Q");
                    StdDraw.show();
                }

                else if (modeSet.contains(i)) {

                    if (i == 'N' || i == 'n'){
                        s = 0;
                        state = STATE.PROMPT;
                        StdDraw.clear(Color.BLACK);
                        StdDraw.setFont(BIG);
                        StdDraw.text(40, 16, "Please now enter the seed : (any number within 64 digits long)");
                        StdDraw.setFont(SMALL);
                        StdDraw.text(40, 14, "Please enter 's' to start the game after entering the seed");
                        StdDraw.show();
                    }

                    else if (i == 'L' || i == 'l') {
                        controller = loadSavedWorld();
                        resumeGame();
                    }

                    else if (i == 'q' || i == 'Q') {
                        System.exit(0);
                    }
                }
            }

            else if (state == STATE.PROMPT) {

                if (Character.isDigit(i)) {
                    s = s * 10 + (i - '0');
                }

                else if (!Character.isDigit(i) && i != 's' && i != 'S') {
                    StdDraw.text(40, 12, "For seed we take NUMBERS only, Please try enter the seed again.");
                    StdDraw.show();
                    s = 0;
                }

                else if ((i == 's' || i == 'S')) {
                    StdDraw.text(40, 10, "Seed Confirmed: " + s);
                    StdDraw.show();
                    StdDraw.pause(2000);
                    startNewGame(s);
                }
            }

            else if (state == STATE.BETWEEN) {

                if (i == 'c' || i == 'C') {
                    state = STATE.GAME;
                    startNewGame(seed + 1);
                }

                else if (i == 'm' || i == 'M') {
                    state = STATE.MENU;
                    mouseRender();
                }

                else if (i == 'q' || i == 'Q') {
                    System.exit(0);
                }
            }

            else if (state == STATE.SAVED) {
                if (i == 'q' || i == 'Q') {
                    System.exit(0);
                }
            }

            else if (state == STATE.OVER) {
                if (i == 'm' || i == 'M') {
                    state = STATE.MENU;
                    mouseRender();
                }

                else if (i == 'q' || i == 'Q') {
                    System.exit(0);
                }
            }
        }
    }

    /**
     * Method used for autograding and testing the game code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The game should
     * behave exactly as if the user typed these characters into the game after playing
     * playWithKeyboard. If the string ends in ":q", the same world should be returned as if the
     * string did not end with q. For example "n123sss" and "n123sss:q" should return the same
     * world. However, the behavior is slightly different. After playing with "n123sss:q", the game
     * should save, and thus if we then called playWithInputString with the string "l", we'd expect
     * to get the exact same world back again, since this corresponds to loading the saved game.
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] playWithInputString(String input) {
        // TODO: Fill out this method to run the game using the input passed in,
        // and return a 2D tile representation of the world that would have been
        // drawn if the same inputs had been given to playWithKeyboard().
        state = STATE.READONLY;

        char[] inputArray = input.toCharArray();
        long s = findSeed(inputArray);
        if (inputArray[0] == 'N' || inputArray[0] == 'n') {
            startNewGame(s);
        }
        else if (inputArray[0] == 'L' || inputArray[0] == 'l') {
            resumeGame();
        }

        return world;
    }

    public void run() {
        long lastTime = System.nanoTime();
        final double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0.0;

        while (gameOn) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;

            if (delta >= 1) {

                if (boomList.size() == 0) {
                    world[p.getX()][p.getY()] = Tileset.PLAYER;
                    for(Chaser c : chaserList) {
                        world[c.getX()][c.getY()] = c.getCurrent();
                    }
                }

                //tick();
                bombTick();
                chaserTick();
                tick();
                delta--;
            }
            mouseRender();
        }
    }

    private Controller loadSavedWorld() {
        File f = new File("byog/Core/lastgame.ter");
        if (f.exists()) {
            try {
                FileInputStream fs = new FileInputStream(f);
                ObjectInputStream os = new ObjectInputStream(fs);
                return (Controller) os.readObject();
            } catch (FileNotFoundException e) {
                System.out.println("file not found");
                System.exit(0);
            } catch (IOException e) {
                System.out.println(e);
                System.exit(0);
            } catch (ClassNotFoundException e) {
                System.out.println("Class not found.");
                System.exit(0);
            }
        }
        return null;
    }

    private void bombTick() {
        Iterator<Boom> iter = boomList.iterator();
        Boom b;

        while (iter.hasNext()) {
            b = iter.next();
            if (b.getStage() >= 4) {
                iter.remove();
                continue;
            }
            if (b.update()) {
                world[b.getX()][b.getY()] = b.getCurrent();
                if (b.getX() == p.getX() && b.getY() == p.getY()) {
                    p.setHealth(p.getHealth() - 1);
                }

                Iterator<Chaser> iterC = chaserList.iterator();
                Chaser c;
                while (iterC.hasNext()) {
                    c = iterC.next();
                    if (b.getX() == c.getX() && b.getY() == c.getY()) {
                        c.setHealth(c.getHealth() - 1);
                        if (c.getHealth() == 0) {
                            iterC.remove();
                            p.setScore(p.getScore() + 30);
                        }
                    }
                }
            }
        }

    }

    private void chaserTick() {
        Iterator<Chaser> iter = chaserList.iterator();

        if (checkCollision() && !colliding) {
            colliding = true;
            p.setHealth(p.getHealth() - 0.5);
        }

        if (!checkCollision()) {
            colliding = false;
        }

        Chaser c;

        while (iter.hasNext()) {
            c = iter.next();

            if (c.getMode() == 1) {
                c.setTarget(p.getX(), p.getY());
            }
            else if (c.getMode() == 0) {
                c.setTarget(c.getInitialX(), c.getInitialY());
            }
            c.update();
        }
    }

    private boolean checkCollision() {
        Iterator<Chaser> iter = chaserList.iterator();
        Chaser c;
        while (iter.hasNext()) {
            c = iter.next();
            if (c.getX() == p.getX() && c.getY() == p.getY()) {
                return true;
            }
        }

        return false;
    }

    private void tick() {
        char c;
        if (atDoor(p) && p.getScore() >= 100) {
            world[door.getX()][door.getY()] = Tileset.UNLOCKED_DOOR;
            gameOn = false;
            state = STATE.BETWEEN;
            reset();
            return;
        }

        if (p.getHealth() <= 0) {
            gameOn = false;
            state = STATE.OVER;
            reset();
            return;
        }

        if (!StdDraw.hasNextKeyTyped()) {
            return;
        }

        if (StdDraw.hasNextKeyTyped()) {
            c = StdDraw.nextKeyTyped();

            int px = p.getX();
            int py = p.getY();
            int lx = lastPos.getX();
            int ly = lastPos.getY();
            if (c == 'w' || c == 'W') {
                if (quit.equals(":")) {
                    quit = "";
                }
                if (!world[px][py + 1].equals(Tileset.WALL) && !world[px][py + 1].equals(Tileset.GRASS)) {

                    if (isExplosion(world[lx][ly])) {
                        world[px][py] = Tileset.FLOOR;
                    }
                    else if (world[lx][ly].equals(Tileset.PLAYER)) {
                        if (!isChaser(lastStep) && !lastStep.equals(Tileset.COIN) && !lastStep.equals(Tileset.FLOWER)) {
                            world[px][py] = lastStep;
                        }
                        else {
                            world[px][py] = Tileset.FLOOR;

                            if (lastStep.equals(Tileset.FLOWER)) {
                                p.setScore(p.getScore() + 10);
                                flower.remove(px + "" + py);
                            }

                            if (lastStep.equals(Tileset.COIN)) {
                                p.setWealth(p.getWealth() + 1);
                            }

                        }
                    }
                    else {
                        world[px][py] = world[lx][ly];
                    }
                    lastPos.setX(px);
                    lastPos.setY(py + 1);
                    lastStep = world[px][py + 1];
                    p.setY(py + 1);
                    p.setxD(0);
                    p.setyD(1);
                }
            }

            else if (c == 's' || c == 'S') {
                if (quit.equals(":")) {
                    quit = "";
                }
                if (!world[px][py - 1].equals(Tileset.WALL) && !world[px][py - 1].equals(Tileset.GRASS)) {
                    if (isExplosion(world[lx][ly])) {
                        world[px][py] = Tileset.FLOOR;
                        p.setHealth(p.getHealth() - 1);
                    }

                    else if (world[lx][ly].equals(Tileset.PLAYER)) {
                        if (!isChaser(lastStep) && !lastStep.equals(Tileset.COIN) && !lastStep.equals(Tileset.FLOWER)) {
                            world[px][py] = lastStep;
                        }
                        else {
                            world[px][py] = Tileset.FLOOR;
                            if (lastStep.equals(Tileset.FLOWER)) {
                                p.setScore(p.getScore() + 10);
                                flower.remove(px + "" + py);
                            }

                            if (lastStep.equals(Tileset.COIN)) {
                                p.setWealth(p.getWealth() + 1);
                            }
                        }
                    }
                    else {
                        world[px][py] = world[lx][ly];
                    }
                    lastPos.setX(px);
                    lastPos.setY(py - 1);
                    lastStep = world[px][py - 1];
                    p.setY(p.getY() - 1);
                    p.setxD(0);
                    p.setyD(-1);
                }
            }

            else if (c == 'a' || c == 'A') {
                if (quit.equals(":")) {
                    quit = "";
                }
                if (!world[px - 1][py].equals(Tileset.WALL) && !world[px - 1][py].equals(Tileset.GRASS)) {
                    if (isExplosion(world[lx][ly])) {
                        world[px][py] = Tileset.FLOOR;
                        p.setHealth(p.getHealth() - 1);
                    }
                    else if (world[lx][ly].equals(Tileset.PLAYER)) {
                        if (!isChaser(lastStep) && !lastStep.equals(Tileset.COIN) && !lastStep.equals(Tileset.FLOWER)) {
                            world[px][py] = lastStep;
                        }
                        else {
                            world[px][py] = Tileset.FLOOR;
                            if (lastStep.equals(Tileset.FLOWER)) {
                                p.setScore(p.getScore() + 10);
                                flower.remove(px + "" + py);
                            }

                            if (lastStep.equals(Tileset.COIN)) {
                                p.setWealth(p.getWealth() + 1);
                            }
                        }
                    }
                    else {
                        world[px][py] = world[lx][ly];
                    }
                    lastPos.setX(px - 1);
                    lastPos.setY(py);
                    lastStep = world[px - 1][py];
                    p.setX(px - 1);
                    p.setxD(-1);
                    p.setyD(0);
                }
            }

            else if (c == 'd' || c == 'D') {
                if (quit.equals(":")) {
                    quit = "";
                }
                if (!world[px + 1][py].equals(Tileset.WALL) && !world[px + 1][py].equals(Tileset.GRASS)) {
                    if (isExplosion(world[lx][ly])) {
                        world[px][py] = Tileset.FLOOR;
                        p.setHealth(p.getHealth() - 1);
                    }
                    else if (world[lx][ly].equals(Tileset.PLAYER)) {
                        if (!isChaser(lastStep) && !lastStep.equals(Tileset.COIN) && !lastStep.equals(Tileset.FLOWER)) {
                            world[px][py] = lastStep;
                        }
                        else {
                            world[px][py] = Tileset.FLOOR;
                            if (lastStep.equals(Tileset.FLOWER)) {
                                p.setScore(p.getScore() + 10);
                                flower.remove(px + "" + py);
                            }

                            if (lastStep.equals(Tileset.COIN)) {
                                p.setWealth(p.getWealth() + 1);
                            }
                        }
                    }
                    else {
                        world[px][py] = world[lx][ly];
                    }
                    lastPos.setX(px + 1);
                    lastPos.setY(py);
                    lastStep = world[px + 1][py];
                    p.setX(px + 1);
                    p.setxD(1);
                    p.setyD(0);
                }
            }

            else if (c == 'b' || c == 'B') {
                if (quit.equals(":")) {
                    quit = "";
                }
                int nx = px + p.getxD();
                int ny = py + p.getyD();

                if (world[nx][ny].equals(Tileset.FLOOR)) {
                    world[nx][ny] = Tileset.BOMB;
                    boomList.add(new Boom(nx, ny, BOOMCOUNTDOWN));
                    Boom b;

                    for (int i = 1; i < 4; i++) {

                        if (nx - i >= 0 && !world[nx - i][ny].equals(Tileset.WALL) && !world[nx - i][ny].equals(Tileset.NOTHING) && !world[nx - i][ny].equals(Tileset.LOCKED_DOOR)) {
                            b = new Boom(nx - i, ny, BOOMCOUNTDOWN - i);
                            if (world[nx - i][ny].equals(Tileset.GRASS) && grass[nx - i][ny] == 1) {
                                b.setLastFrame(Tileset.COIN);
                            }
                            boomList.add(b);
                        }

                        if (nx + i < 80 && !world[nx + i][ny].equals(Tileset.WALL) && !world[nx + i][ny].equals(Tileset.NOTHING) && !world[nx + i][ny].equals(Tileset.LOCKED_DOOR)) {
                            b = new Boom(nx + i, ny, BOOMCOUNTDOWN - i);
                            if (world[nx + i][ny].equals(Tileset.GRASS) && grass[nx + i][ny] == 1) {
                                b.setLastFrame(Tileset.COIN);
                            }
                            boomList.add(b);
                        }

                        if (ny - i >= 0 && !world[nx][ny - i].equals(Tileset.WALL) && !world[nx][ny - i].equals(Tileset.NOTHING) && !world[nx][ny - i].equals(Tileset.LOCKED_DOOR)) {
                            b = new Boom(nx, ny - i, BOOMCOUNTDOWN - i);
                            if (world[nx][ny - i].equals(Tileset.GRASS) && grass[nx][ny - i] == 1) {
                                b.setLastFrame(Tileset.COIN);
                            }
                            boomList.add(b);
                        }

                        if (ny + i < 30 && !world[nx][ny + i].equals(Tileset.WALL) && !world[nx][ny + i].equals(Tileset.NOTHING) && !world[nx][ny + i].equals(Tileset.LOCKED_DOOR)) {
                            b = new Boom(nx, ny + i, BOOMCOUNTDOWN - i);
                            if (world[nx][ny + i].equals(Tileset.GRASS) && grass[nx][ny + i] == 1) {
                                b.setLastFrame(Tileset.COIN);
                            }
                            boomList.add(b);
                        }
                    }
                }
            }

            else if (c == 't' || c == 'T') {
                if (p.getWealth() >= 3) {
                    p.setWealth(p.getWealth() - 3);
                    p.setHealth(p.getHealth() + 1);
                }
            }

            else if (c == ':') {
                if (quit.equals("")) {
                    quit += c;
                }
            }

            else if (c == 'Q' || c == 'q') {
                if (quit.equals(":")) {
                    gameOn = false;
                    controller = new Controller(p, world, seed, boomList, chaserList, lastStep, lastPos, door, grass, flower, colliding);
                    saveWorld(controller);
                    state = STATE.SAVED;
                    return;
                }
                else {
                    gameOn = false;
                    state = STATE.MENU;
                    reset();
                    return;
                }
            }

            else {
                if (quit.equals(":")) {
                    quit = "";
                }
            }
            world[p.getX()][p.getY()] = Tileset.PLAYER;

            for (Position p : flower.values()) {

                if (!world[p.getX()][p.getY()].equals(Tileset.FLOWER)) {
                    world[p.getX()][p.getY()] = Tileset.FLOWER;
                }
            }
        }

    }

    private static void saveWorld(Controller c) {
        File f = new File("byog/Core/lastgame.ter");

        try {
            if (!f.exists()) {
                f.createNewFile();
            }

            FileOutputStream fs = new FileOutputStream(f);
            ObjectOutputStream os = new ObjectOutputStream(fs);
            os.writeObject(c);

        } catch (FileNotFoundException e) {
            System.out.println("File not found, baby");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(0);
        }
    }

    private boolean atDoor(Player p) {
        int x = p.getX();
        int y = p.getY();
        for (int i = 0; i < 4; i++) {
            if(world[x + dx[i]][y + dy[i]].equals(Tileset.LOCKED_DOOR)) {
                return true;
            }
        }
        return false;
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

    private void mouseRender() {
        if (gameOn) {
            ter.renderFrame(world);
            int mX = (int) StdDraw.mouseX();
            int mY = (int) StdDraw.mouseY();
            StdDraw.setPenColor(Color.gray);
            StdDraw.filledRectangle(65, 30.5, 5, 0.5);
            StdDraw.setPenColor(Color.green);
            StdDraw.filledRectangle(60 + (float) (p.getHealth() / 2), 30.5, (float) (p.getHealth() / 2), 0.5);
            StdDraw.setPenColor(Color.white);
            StdDraw.text(50, 30.5, "Coins: "  + p.getWealth());
            if (!(mX < 0 || mX >= 80 || mY <0 || mY >= 30)) {
                StdDraw.setPenColor(Color.white);
                StdDraw.setFont(new Font("Monaco", Font.BOLD, 14));
                StdDraw.textLeft(0.5, 30.5, world[mX][mY].description());
                StdDraw.textRight(79.5, 30.5, "Score: " + p.getScore());
            }
            StdDraw.show();
        }
        else if (state == STATE.BETWEEN){
            drawBetween();
        }

        else if (state == STATE.MENU) {
            drawMenu();
        }

        else if (state == STATE.SAVED) {
            drawSaved();
        }

        else if (state == STATE.OVER) {
            drawOver();
        }
    }

    private void drawOver() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.white);
        StdDraw.setFont(BIG);
        StdDraw.text(40, 20 ,"You lost :(");
        StdDraw.setFont(SMALL);
        StdDraw.text(40, 18 ,"'q' to quit, 'm' to main menu");
        StdDraw.show();
    }

    private void drawBetween() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.magenta);
        StdDraw.setFont(BIG);
        StdDraw.text(40, 20 ,"Congratulations! You won! Wanna continue darling?");
        StdDraw.setFont(SMALL);
        StdDraw.text(40, 18 ,"'c' to continue and start a new game, 'm' to go back to main menu");
        StdDraw.show();
    }

    private void drawMenu() {
        StdDraw.clear(Color.black);
        Font font = new Font("Monaco", Font.BOLD, 48);
        StdDraw.setFont(font);
        StdDraw.setPenColor(Color.white);
        StdDraw.text(40, 22, "CS61B: THE GAME");
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 32));
        StdDraw.text(40, 12, "New Game (N)");
        StdDraw.text(40, 9, "Load Game (L)");
        StdDraw.text(40, 6, "Quit (Q)");
        StdDraw.show();
    }

    private void drawPrompt() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.white);
        StdDraw.setFont(BIG);
        StdDraw.text(40, 16, "Please now enter the seed : (any number within 64 digits long)");
        StdDraw.setFont(SMALL);
        StdDraw.text(40, 14, "Please enter 's' to start the game after entering the seed");
        StdDraw.show();
    }

    private void drawSaved() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.white);
        StdDraw.setFont(BIG);
        StdDraw.text(40, 15, "Game saved! Press q to exit.");
        StdDraw.show();
    }

    private void startNewGame(long s) {
        //reset();
        state = STATE.GAME;
        gameOn = true;
        seed = s;
        r = new Random(seed);

        ter.initialize(WIDTH, HEIGHT + 1);
        initialize();
        int roomNum = 15 + RandomUtils.uniform(r, 10);
        generateLocations(world, roomNum);
        generatePath(world, locations);
        addWalls();
        addDoor();
        addPlayer();
        addChaser();
        addGrass();
        addFlowers();
        lastPos = new Position(p.getX(), p.getY());

        run();
    }

    private void reset() {
        r = null;
        boomList = new LinkedList<>();
        chaserList = new LinkedList<>();
        wallList = new ArrayList<>();
        flower = new HashMap<>();
        grass = new int[WIDTH][HEIGHT];
        p = null;
        lastPos = null;
        locations = null;
        doorSet = false;
        playerSet = false;
        quit = "";
    }

    private void initialize(Set<Character> mode) {
        modeSet.add('N');
        modeSet.add('n');
        modeSet.add('L');
        modeSet.add('l');
        modeSet.add('Q');
        modeSet.add('q');
    }

    private void initialize() {
        initialize(world);
        initialize(hallways);
        initialize(rooms);
    }

    private void resumeGame() {
        reset();
        ter.initialize(WIDTH, HEIGHT + 1);
        initialize();
        p = controller.getPlayer();
        world = controller.getWorld();
        seed = controller.getSeed();
        boomList.addAll(controller.getBoomList());

        chaserList.addAll(controller.getChaserList());
        lastStep = controller.getLastStep();
        lastPos = controller.getLastPos();
        door = controller.getDoor();
        gameOn = true;
        run();
    }

    private long findSeed(char[] input) {
        long num = 0;

        int i = 0;
        while (i < input.length) {
            if (Character.isDigit(input[i])) {
                num = num * 10 + (input[i] - '0');
            }

            else if (input[i] == 'S' || input[i] == 's') {
                break;
            }
            i++;
        }

        return num;
    }

    private void initialize(TETile[][] world) {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }

    private void generateLocations(TETile[][] world, int num) {

        locations = new Position[num];

        int i = 0;
        int x = 0;
        int y = 0;
        int roomType;

        int w;
        int h;

        int s;

        int xOffSet;
        int yOffSet;
        int nx;
        int ny;

        while (i < num) {

            x = 1 + r.nextInt(78);
            y = 1 + r.nextInt(28);

            while (rooms[x][y] == Tileset.FLOOR) {

                x = 1 + r.nextInt(78);
                y = 1 + r.nextInt(28);
            }

            roomType = r.nextInt(2);
            if (roomType == 0) {
                w = RandomUtils.uniform(r, RECTANGLELOWER, RECTANGLEUPPER + 1);
                h = RandomUtils.uniform(r, RECTANGLELOWER, RECTANGLEUPPER + 1);
                xOffSet = -r.nextInt(w);
                yOffSet = -r.nextInt(h);
                nx = x + xOffSet;
                ny = y + yOffSet;

                while (nx <= 0 || ny <= 0 || nx + w > 79 || ny + h > 29 || checkRecOverLap(nx, ny, w, h)) {

                    x = 1 + r.nextInt(78);
                    y = 1 + r.nextInt(28);
                    nx = x + xOffSet;
                    ny = y + yOffSet;
                }

                addRectangle(nx, ny, w, h, world);
                locations[i] = new Position(x, y);
                i++;
            }

            else if (roomType == 1) {

                s = RandomUtils.uniform(r, HEXAGONLOWER, HEXAGONUPPER + 1);
                xOffSet = -RandomUtils.uniform(r, s - 1, 2 * s - 1);
                yOffSet = -r.nextInt(2 * s);
                nx = x + xOffSet;
                ny = y + yOffSet;

                while (nx <= 0 || ny <= 0 || nx + 3 * s - 2 > 79 || ny + 2 * s > 29 || checkHexOverLap(nx, ny, s)) {

                    x = 1 + r.nextInt(78);
                    y = 1 + r.nextInt(28);
                    nx = x + xOffSet;
                    ny = y + yOffSet;
                }

                addHexagon(nx, ny, s, world);
                locations[i] = new Position(x, y);
                i++;

            }

        }
    }

    private void generatePath(TETile[][] world, Position[] locations) {
        int prev = 0;
        int next = 1;

        int xLarger;
        int xSmaller;
        int yOfXSmaller;
        int yOfXLarger;

        while (next < locations.length) {
            if (locations[prev].getX() > locations[next].getX()) {
                xLarger = locations[prev].getX();
                xSmaller = locations[next].getX();
                yOfXLarger = locations[prev].getY();
                yOfXSmaller = locations[next].getY();
            }
            else {
                xLarger = locations[next].getX();
                xSmaller = locations[prev].getX();
                yOfXLarger = locations[next].getY();
                yOfXSmaller = locations[prev].getY();
            }

            if (yOfXLarger == yOfXSmaller) {
                horizontalPath(world, xSmaller, xLarger, yOfXLarger);
            }

            else {
                int whichFirst = r.nextInt(2);
                if (whichFirst == 0) {
                    int turnH = RandomUtils.uniform(r, xSmaller, xLarger + 1);
                    horizontalPath(world, xSmaller, turnH, yOfXSmaller);
                    verticalPath(world, yOfXSmaller, yOfXLarger, turnH);
                    horizontalPath(world, turnH, xLarger, yOfXLarger);
                }

                else if (whichFirst == 1) {
                    int ySmaller;
                    int yLarger;
                    if (yOfXLarger > yOfXSmaller) {
                        yLarger = yOfXLarger;
                        ySmaller = yOfXSmaller;
                    }
                    else {
                        yLarger = yOfXSmaller;
                        ySmaller = yOfXLarger;
                    }
                    int turnV = RandomUtils.uniform(r, ySmaller, yLarger);
                    verticalPath(world, yOfXSmaller, turnV, xSmaller);
                    horizontalPath(world, xSmaller, xLarger, turnV);
                    verticalPath(world, turnV, yOfXLarger, xLarger);
                }
            }
            prev++;
            next++;
        }
    }


    private void generateHexagonLine(TETile[][] world, int xStart, int xEnd, int y) {
        for (int i = xStart; i < xEnd; i++) {
            world[i][y] = Tileset.FLOOR;
        }
    }

    private void horizontalPath(TETile[][] world, int xStart, int xEnd, int y) {
        if (xStart == xEnd) {
            return;
        }

        else if (xStart > xEnd) {
            for (int i = xStart; i > xEnd; i--) {
                if (world[i][y] == Tileset.NOTHING) {
                    world[i][y] = Tileset.FLOOR;
                    hallways[i][y] = Tileset.FLOOR;
                }
            }
        }

        else {
            for (int i = xStart; i < xEnd; i++) {
                if (world[i][y] == Tileset.NOTHING) {
                    world[i][y] = Tileset.FLOOR;
                    hallways[i][y] = Tileset.FLOOR;
                }
            }
        }

    }

    private void verticalPath(TETile[][] world, int yStart, int yEnd, int x) {

        if (yStart == yEnd) {
            return;
        }

        else if (yStart > yEnd) {
            for (int i = yStart; i > yEnd; i--) {
                if (world[x][i] == Tileset.NOTHING) {
                    world[x][i] = Tileset.FLOOR;
                    hallways[x][i] = Tileset.FLOOR;
                }
            }
        }

        else {
            for (int i = yStart; i < yEnd; i++) {
                if (world[x][i] == Tileset.NOTHING) {
                    world[x][i] = Tileset.FLOOR;
                    hallways[x][i] = Tileset.FLOOR;
                }
            }
        }
    }

    private boolean checkLine(int xStart, int xEnd, int y, TETile[][] world) {
        for (int i = xStart; i < xEnd; i++) {
            if (world[i][y] == Tileset.FLOOR) {
                return true;
            }
        }

        return false;
    }

    private boolean checkRecOverLap(int x, int y, int w, int h) {
        for (int i = y; i < y + h; i++) {
            if (checkLine(x, x + w, i, rooms)) {
                return true;
            }
        }

        return false;
    }

    private boolean checkHexOverLap(int x, int y, int s) {
        int rowNum;
        int start;
        int end;

        for (int i = y; i < y + 2 * s; i++) {
            rowNum = i - y;

            if (rowNum < s) {
                start = x + s - 1 - rowNum;
                end = x + s - 1 - rowNum + s + 2 * rowNum;
                if (checkLine(start, end, i, rooms)) {
                    return true;
                }
            }

            else {
                start = x + rowNum - s;
                end = x + rowNum - s + 3 * s - 2 - 2 *(rowNum - s);
                if (checkLine(start, end, i, rooms)) {
                    return true;
                }
            }
        }

        return false;
    }

    private void addRectangle(int x, int y, int w, int h, TETile[][] world) {

        for (int i = x; i < x + w; i++) {
            for (int j = y; j < y + h; j++) {
                world[i][j] = Tileset.FLOOR;
                rooms[i][j] = Tileset.FLOOR;
            }
        }


    }

    private void addHexagon(int x, int y, int s, TETile[][] world) {

        int rowNum;
        int start;
        int end;

        for (int i = y; i < y + 2 * s; i++) {
            rowNum = i - y;

            if (rowNum < s) {
                start = x + s - 1 - rowNum;
                end = x + s - 1 - rowNum + s + 2 * rowNum;
                generateHexagonLine(world, start, end, i);
                generateHexagonLine(rooms, start, end, i);
            }

            else {
                start = x + rowNum - s;
                end = x + rowNum - s + 3 * s - 2 - 2 *(rowNum - s);
                generateHexagonLine(world, start, end, i);
                generateHexagonLine(rooms, start, end, i);
            }
        }

    }

    private void addWalls() {
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                buildWall(world, i, j);
            }
        }
    }

    private void buildWall(TETile[][] world, int x, int y) {
        if (world[x][y] != Tileset.FLOOR) {
            return;
        }
        int nx;
        int ny;

        for (int k = 0; k < 8; k++) {
            nx = x + dx[k];
            ny = y + dy[k];

            if (world[nx][ny] == Tileset.NOTHING) {
                world[nx][ny] = Tileset.WALL;
                wallList.add(new int[] {nx, ny});
            }
        }
    }

    private void addDoor() {
        int idx = r.nextInt(wallList.size());

        int x = wallList.get(idx)[0];
        int y = wallList.get(idx)[1];

        while (!validDoor(world, x, y)) {
            idx = r.nextInt(wallList.size());
            x = wallList.get(idx)[0];
            y = wallList.get(idx)[1];
        }

        world[x][y] = Tileset.LOCKED_DOOR;
        door = new Position(x, y);
        doorSet = true;
    }

    private boolean validDoor(TETile[][] world, int x, int y) {
        int nx;
        int ny;
        boolean a = false;
        boolean b;

        int[] pos = new int[4];
        for (int i = 0; i < 4; i++) {
            nx = x + dx[i];
            ny = y + dy[i];

            if (nx < 0 || ny < 0 || nx >= 80 || ny >= 30) {
                continue;
            }

            else if (world[nx][ny] == Tileset.NOTHING) {
                int tx = nx;
                int ty = ny;
                while (tx > 0 && tx < WIDTH && ty > 0 && ty < HEIGHT) {
                    if (world[tx][ty] != Tileset.NOTHING) {
                        return false;
                    }

                    tx += dx[i];
                    ty += dy[i];

                }
                a = true;
            }

            else if (world[nx][ny] == Tileset.WALL) {
                pos[i] = 1;
            }
        }

        b = ((pos[0] == 1 && pos[1] == 1) || pos[2] == 1 && pos[3] == 1);

        return a && b;
    }

    private void addPlayer() {
        int x = 1 + r.nextInt(78);
        int y = 1 + r.nextInt(28);

        while (world[x][y] != Tileset.FLOOR) {
            x = 1 + r.nextInt(78);
            y = 1 + r.nextInt(28);
        }

        world[x][y] = Tileset.PLAYER;
        this.p = new Player(x, y);
        playerSet = true;
    }

    private void addChaser() {
        aa:
        for (int i = 79; i >= 0; i--) {
            for (int j = 29; j >= 0; j--) {
                if (world[i][j] == Tileset.FLOOR) {
                    world[i][j] = Tileset.CHASERLEFT;
                    chaserList.add(new Chaser(i, j, -1, 0, new Position(p.getX(), p.getY()), world));
                    break aa;
                }
            }
        }

        bb:
        for (int i = 0; i < 80; i++) {
            for (int j = 0; j < 30; j++) {
                if (world[i][j] == Tileset.FLOOR) {
                    world[i][j] = Tileset.CHASERLEFT;
                    chaserList.add(new Chaser(i, j, 1, 0, new Position(p.getX(), p.getY()), world));
                    break bb;
                }
            }
        }
    }

    private void addGrass() {
        int count = 10;
        int x;
        int y;
        int coin;
        while (count > 0) {
            x = 1 + r.nextInt(78);
            y = 1 + r.nextInt(28);

            while(world[x][y] != Tileset.FLOOR) {
                x = 1 + r.nextInt(78);
                y = 1 + r.nextInt(28);
            }

            world[x][y] = Tileset.GRASS;
            count--;
            coin = r.nextInt(10);
            if (coin < 5) {
                grass[x][y] = 1;
            }
        }
    }

    private void addFlowers() {
        for (Position p : locations) {
            world[p.getX()][p.getY()] = Tileset.FLOWER;
            flower.put(p.getX() + "" + p.getY(), p);
        }
    }

}
