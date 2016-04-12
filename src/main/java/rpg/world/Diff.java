package rpg.world;

public class Diff{
    private Tile tile;
    private int x;
    private int y;

    public Diff(Tile tile, int x, int y) {
        this.tile = tile;
        this.x = x;
        this.y = y;
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
}
