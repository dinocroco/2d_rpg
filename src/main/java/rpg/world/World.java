package rpg.world;

import rpg.player.Player;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.IntStream;

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

    public Diff startingPoint(Map<Integer,Player> players) {

        int x=-1;
        int y=-1;
        //int[] randomWidth = IntStream.range(1, width).toArray();
        //int[] randomHeight = IntStream.range(1, height).toArray(); // 0,1,2,3...
        List<Integer> randomWidth = new ArrayList<>(width);
        List<Integer> randomHeight = new ArrayList<>(height);
        for (int i = 0; i < width; i++) {
            randomWidth.add(i);
        }
        for (int i = 0; i < height; i++) {
            randomHeight.add(i);
        }
/*        List randomWList=Arrays.asList(randomWidth);
        List randomHList=Arrays.asList(randomHeight);*/
        Collections.shuffle(randomWidth);
        Collections.shuffle(randomHeight);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if(tile(randomWidth.get(i),randomHeight.get(j))==Tile.FLOOR ){

                    x=randomWidth.get(i);
                    y=randomHeight.get(j);
                    for (Player player: players.values()) {
                        if (x==player.getX() && y==player.getY()){
                            x=-1;
                            y=-1;
                        }
                    }
                    if (x!=-1&&y!=-1){
                        return new Diff(tile(x,y),x,y);
                    }
                }
            }

        }
        return null;
    }
}
