package rpg.world;

import rpg.character.GameCharacter;
import rpg.character.Player;
import rpg.character.Unit;

import java.awt.*;
import java.util.*;
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

    public Diff playerStartingPoint(Map<Integer,Player> players) {

        int x;
        int y;
        List<Integer> randomWidth = new ArrayList<>(width);
        List<Integer> randomHeight = new ArrayList<>(height);
        for (int i = 0; i < width; i++) {
            randomWidth.add(i);
        }
        for (int i = 0; i < height; i++) {
            randomHeight.add(i);
        }
        Collections.shuffle(randomWidth);
        Collections.shuffle(randomHeight);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if(tile(randomWidth.get(i),randomHeight.get(j))==Tile.FLOOR ){

                    x=randomWidth.get(i);
                    y=randomHeight.get(j);
                    for (GameCharacter player: players.values()) {
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

    public Diff unitStartingPoint(Map<Integer,Player> players, List<Unit> units){
        int x;
        int y;
        List<Integer> randomWidth = new ArrayList<>(width);
        List<Integer> randomHeight = new ArrayList<>(height);
        for (int i = 0; i < width; i++) {
            randomWidth.add(i);
        }
        for (int i = 0; i < height; i++) {
            randomHeight.add(i);
        }
        Collections.shuffle(randomWidth);
        Collections.shuffle(randomHeight);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if(tile(randomWidth.get(i),randomHeight.get(j))==Tile.FLOOR ){

                    x=randomWidth.get(i);
                    y=randomHeight.get(j);
                    for (GameCharacter player: players.values()) {
                        if (x>=player.getX()-5 && x<=player.getX()+5 && y<=player.getY()+5 && y>=player.getY()-5){
                            x=-1;
                            y=-1;
                        }
                    }
                    for (Unit unit:units) {
                        if (x==unit.getX() && y==unit.getY()){
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
