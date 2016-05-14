package rpg.world;

import rpg.character.GameCharacter;
import rpg.character.Player;
import rpg.character.Unit;

import java.awt.*;
import java.util.*;
import java.util.List;

public class World {

    public static final long BACKTOACTIVE = 30;
    public static final int HEARINGRADIUS = 15;

    private Tile[][] tiles;
    private int width;
    private List<Diff> diff = new ArrayList<>();
    private Map<Integer,Player> players = new HashMap<>();
    private List<Unit> units = new ArrayList<>();
    private final int healingPointsAmount = 10;

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

    public synchronized void dig(int x, int y){

        if(tile(x,y) == Tile.WALL){
            tiles[x][y] = Tile.FLOOR;
            addDiff(new Diff(tile(x,y),x,y));
        }
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

    public synchronized void setTile(Tile tile, int x, int y){
        tiles[x][y] = tile;
    }

    public Diff startingPoint(){
        Random rand = new Random();
        for (int i = 0;i<1000;i++) {

            int x = rand.nextInt(width);
            int y = rand.nextInt(height);
            if (tile(x, y) == Tile.FLOOR) {

                for (GameCharacter player : players.values()) {
                    if (x >= player.getX() - 5 && x <= player.getX() + 5 && y <= player.getY() + 5 && y >= player.getY() - 5) {
                        x = -1;
                        y = -1;
                    }
                }
                for (Unit unit : units) {
                    if (x == unit.getX() && y == unit.getY()) {
                        x = -1;
                        y = -1;
                    }
                }
                if (x != -1 && y != -1) {
                    return new Diff(tile(x, y), x, y);
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
        if(player.getOldconnectionId()==player.getId()) {
            player.setConnected(false);
        }
        player.toUnchanged();
        addDiff(new Diff(player));
    }

    public List<Unit> getUnits() {
        return units;
    }

    public GameCharacter getGameCharacter(int x, int y){
        for (Player player : players.values()) {
            if(player.getX()==x && player.getY()==y) {
                return player;
            }
        }
        for (Unit unit : units) {
            if(unit.getX()==x && unit.getY()==y){
                return unit;
            }
        }
        return null;
    }

    public Player playerAdjacent(int x, int y){
        for (Player player:players.values()){
            if (Math.abs(player.getX() - x) + Math.abs(player.getY() - y) <= 1 ){
                return player;
            }
        }
        return null;
    }

    public void recoverPoints(long tickspassed) {
        if (tickspassed % 25 == 0) {
            for (Player player : players.values()) {
                if (player.getHealth() < player.getMaxhealth()) {
                    int healthToAdd = healingPointsAmount * player.getHealingSpeed();
                    player.addHealth(healthToAdd);
                    if (player.getHealth()==player.getMaxhealth()) {
                        addDiff(new Diff(player.toMessage() + " has healed to maximum health!", player.getX(), player.getY(), HEARINGRADIUS));
                    }

                }
            }
        }
        if (tickspassed % 50 == 0) {
            for (Player player : players.values()) {
                if (player.getFreezeAbility() < player.getMaxFreezeAbility()) {
                    player.addFreezeAbility(1);
                }
            }

        }
        if (tickspassed % 20 == 0) {
            for (Unit unit : units) {
                if (unit.getHealth() < unit.getMaxhealth()) {
                    unit.addHealth(healingPointsAmount);
                }
            }
        }

    }

    public synchronized void handleDeadPlayers(long tickspassed){

        for (Player player : players.values()) {
            if (player.isActive() && player.getHealth()<=0){
                addDiff(new Diff(player.toMessage()+" passed out.", player.getX(),player.getY(),HEARINGRADIUS));
                player.setActive(false);
                player.setX(-20); //inactive player not displayed on the screen and doesn't influence other player's actions
                player.setY(-20);
                player.setBackToActive(BACKTOACTIVE+tickspassed);
            }
            if (!player.isActive()){
                if (player.getBackToActive()<=tickspassed){
                    player.setActive(true);
                    player.addHealth(player.getMaxhealth()-player.getHealth());
                    Diff location = startingPoint();
                    player.setX(location.getX());
                    player.setY(location.getY());
                    addDiff(new Diff(player.toMessage()+" returned to life.", player.getX(),player.getY(),HEARINGRADIUS));


                }
            }
        }
    }

    public void moveUnits(long tickspassed){
        for (Unit unit : units){
            if(unit.frozen(tickspassed)){
                continue;
            }
            Player playerAdjacent = playerAdjacent(unit.getX(),unit.getY());
            if(playerAdjacent==null) {
                unit.moveUnit(this, tickspassed);
            } else {
                if(unit.getAttackCounter()+1==unit.getAttackSpeed()) {
                    playerAdjacent.addHealth(-unit.getDamage());
                    addDiff(new Diff(unit.getID() + " attacked " + playerAdjacent.getID() + ", health now: "
                            +playerAdjacent.getHealth(),playerAdjacent.getX(),playerAdjacent.getY(),HEARINGRADIUS));

                }
                unit.setAttackCounter((unit.getAttackCounter()+1)%unit.getAttackSpeed());
            }

        }
    }

    public double distanceBetween(GameCharacter a, GameCharacter b){
        return Math.sqrt((a.getX()-b.getX())*(a.getX()-b.getX())+(a.getY()-b.getY())*(a.getY()-b.getY()));
    }

    /**
     *
     * @return nearest Unit or null
     */
    public Unit getNearestUnit(int playerId){
        Double locMin = null;
        Unit nearestUnit = null;
        Player player = players.get(playerId);
        for (Unit unit : units) {
            double distance = distanceBetween(player,unit);
            if(locMin==null || locMin>distance){
                locMin = distance;
                nearestUnit = unit;
            }
        }
        return nearestUnit;
    }

    private List<Diff> findPath(GameCharacter unit, GameCharacter goal){
        // this should return path from unit to goal, or null if unreachable, if 20 steps arent enough then unreachable
        // when it is implemented, then public
        return null;
    }
}
