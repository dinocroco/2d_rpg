package rpg.world;

import rpg.character.Player;
import rpg.character.Unit;

import java.io.Serializable;

/**
 * Class responsible for including differences in gamestate
 */

public class Diff implements Serializable {
    private Tile tile;
    private int x;
    private int y;
    private int r;
    private String message;
    private Player player = null;
    private Unit unit = null;

    public Diff(Tile tile, int x, int y) {
        this.tile = tile;
        this.x = x;
        this.y = y;
    }

    public Diff(String message, int x, int y, int r){
        this.message = message;
        this.x = x;
        this.y = y;
        this.r = r;
    }

    public Diff(Player player) {
        this.player = player;
    }

    public Diff(Unit unit){
        this.unit = unit;
    }

    public Tile getTile() {
        return tile;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Player getPlayer() {
        return player;
    }

    public Unit getUnit() {
        return unit;
    }

    public String getMessage() {
        return message;
    }

    public int getR() {
        return r;
    }
}
