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
    private Map<Integer,Player> players = new HashMap<>();
    private List<Unit> units = new ArrayList<>();
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

    public synchronized List<Diff> getDiff(){
        return diff;
    }

    public synchronized void addDiff(Diff diff){
        this.diff.add(diff);
    }

    public synchronized void clearDiff(){
        diff.clear();
    }

    public Diff playerStartingPoint() {

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

    public Diff unitStartingPoint(){
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

    public boolean vacantXY(int x, int y){
        if (tile(x,y)==Tile.FLOOR){
            for (GameCharacter player: players.values()) {
                if (x==player.getX() && y==player.getY()){
                    return false;
                }
            }
            for (Unit unit:units) {
                if (x==unit.getX() && y==unit.getY()){
                    return false;
                }
            }
            return true;

        }
        return false;
    }

    public synchronized Map<Integer, Player> getPlayers() {
        return players;
    }

    public synchronized void setPlayers(Map<Integer, Player> players) {
        this.players = players;
    }

    public synchronized void removePlayer(int clientIndex){
        Player player = getPlayers().get(clientIndex);
        getPlayers().remove(clientIndex);
        player.setX(-1);
        player.setY(-1);
        player.toUnchanged();
        addDiff(new Diff(player));
    }

    public List<Unit> getUnits() {
        return units;
    }

    public void moveUnits(){
        for (Unit unit : units){
            unit.moveUnit(this);
        }
    }

    public double distanceBetween(GameCharacter a, GameCharacter b){
        return Math.sqrt((a.getX()-b.getX())*(a.getX()-b.getX())+(a.getY()-b.getY())*(a.getY()-b.getY()));
    }

    private List<Diff> findPath(GameCharacter unit, GameCharacter goal){
        // this should return path from unit to goal, or null if unreachable, if 20 steps arent enough then unreachable
        // when it is implemented, then public
        return null;
    }
}
