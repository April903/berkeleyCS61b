package byog.Core;

import java.io.Serializable;

public class Player implements Serializable {
    private int x;
    private int y;
    private int xD;
    private int yD;
    private double health = 10.0;

    private int score;
    private int wealth;
    private int coins;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getxD() {
        return xD;
    }

    public int getyD() {
        return yD;
    }

    public void setxD(int xD) {
        this.xD = xD;
    }

    public void setyD(int yD) {
        this.yD = yD;
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getWealth() {
        return wealth;
    }

    public void setWealth(int wealth) {
        this.wealth = wealth;
    }

}
