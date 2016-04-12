package rpg.world;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class World {
    private Tile[][] tiles;
    private int width;
    private List<Diff> diff = new ArrayList<>();
    public int width() { return width; }

    private int height;
    public int height() { return height; }

    public World(Tile[][] tiles){
        this.tiles = tiles;
        this.width = tiles.length;
        this.height = tiles[0].length;
    }

    public Tile tile(int x, int y){
        if (x < 0 || x >= width || y < 0 || y >= height)
            return Tile.BOUNDS;
        else
            return tiles[x][y];
    }

    public char glyph(int x, int y){
        return tile(x, y).glyph();
    }

    public Color color(int x, int y){
        return tile(x, y).color();
    }

    public List<Diff> getDiff(){
        // when changing tiles, then add to diff
        return diff;
    }

    public void clearDiff(){
        diff.clear();
    }
}
