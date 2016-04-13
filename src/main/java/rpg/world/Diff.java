package rpg.world;

import rpg.player.Player;

import java.io.Serializable;

public class Diff implements Serializable {
    private Tile tile;
    private int x;
    private int y;
    private Player player;

    public Diff(Tile tile, int x, int y) {
        this.tile = tile;
        this.x = x;
        this.y = y;
    }

    public Diff(Player player) {
        // in client it would work by drawing only most recent case of player of this id
        this.player = player;
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
}
