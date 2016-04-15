package rpg.world;

import rpg.character.Player;
import rpg.character.Unit;

import java.io.Serializable;

public class Diff implements Serializable {
    private Tile tile;
    private int x;
    private int y;
    private Player player = null;
    private Unit unit = null;

    public Diff(Tile tile, int x, int y) {
        this.tile = tile;
        this.x = x;
        this.y = y;
    }

    public Diff(Player player) {
        // in client it would work by drawing only most recent case of character of this id
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
}
