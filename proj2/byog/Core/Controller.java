package byog.Core;

import byog.TileEngine.TETile;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.ArrayList;

public class Controller implements Serializable {

    private Player p;
    private TETile[][] world;
    private long seed;
    private LinkedList<Boom> boomList;
    private LinkedList<Chaser> chaserList;
    private TETile lastStep;
    private Position lastPos;
    private Position door;
    private int[][] grass;
    private HashMap<String, Position> flower;
    private boolean colliding;


    public Controller (Player p, TETile[][] world, long seed, LinkedList<Boom> boomList, LinkedList<Chaser> chaserList, TETile lastStep, Position lastPos, Position door, int[][] grass, HashMap<String, Position> flower, boolean colliding) {
        this.p = p;
        this.world = world;
        this.seed = seed;
        this.boomList = boomList;
        this.chaserList = chaserList;
        this.lastStep = lastStep;
        this.lastPos = lastPos;
        this.door = door;
        this.grass = grass;
        this.flower = flower;
        this.colliding = colliding;
    }

    public Player getPlayer() {
        return p;
    }

    public TETile[][] getWorld() {
        return world;
    }

    public long getSeed() {
        return seed;
    }

    public LinkedList<Boom> getBoomList() {
        return boomList;
    }

    public LinkedList<Chaser> getChaserList() {
        return chaserList;
    }

    public Position getDoor() {
        return door;
    }

    public Position getLastPos() {
        return lastPos;
    }

    public TETile getLastStep() {
        return lastStep;
    }
}
