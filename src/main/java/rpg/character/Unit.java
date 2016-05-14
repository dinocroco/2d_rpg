package rpg.character;


import rpg.world.World;

import java.awt.*;
import java.io.Serializable;
import java.util.*;
import java.util.List;

public class Unit implements GameCharacter, Serializable {

    private int x;
    private int y;
    private char glyph = '?';
    private Color color;
    private boolean hasChanged = false;
    private int health = 100;
    private int damage = 10;
    private List<Color> colors = new ArrayList<>();
    public final long idCode;
    private long freezeEnd = 0;

    public Unit(long tickNumber){
        Random rand = new Random();
        this.color = new Color(rand.nextInt(0xFFFFFF));
        this.idCode=tickNumber+1000;//>1000 so it won't match player id
        hasChanged = true;
    }

    @Override
    public int getDamage() {
        return damage;
    }

    @Override
    public void addHealth(int value) {
        health += value;
        health = Math.min(health,100);
        hasChanged = true;
    }


    @Override
    public int getHealth() {
        return health;
    }

    @Override
    public long getID() {
        return idCode;
    }

    @Override
    public boolean hasChanged() {
        return hasChanged;
    }

    @Override
    public void toUnchanged() {
        hasChanged = false;

    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public void setX(int x) {
        this.x=x;
        hasChanged = true;
    }

    @Override
    public void setY(int y) {
        this.y=y;
        hasChanged = true;

    }

    public char getGlyph() {
        return glyph;
    }

    public Color getColor() {
        return color;
    }

    public void moveUnitRandomWay(World world){
        Random rand = new Random();
        int randomInt = rand.nextInt(10);
        if(randomInt == 1) {
            if (world.vacantXY(x, y - 1)) {
                y--;
            }
        } else if (randomInt == 4){
            if (world.vacantXY(x, y + 1)) {
                y++;
            }
        } else if (randomInt == 2) {
            if (world.vacantXY(x + 1, y)) {
                x++;
            }
        } else if (randomInt == 8) {
            if (world.vacantXY(x-1,y)){
                x--;
            }
        }
        hasChanged = true;
    }



    public void goToPlayer(Player player, World world){

        if(Math.abs(player.getX()-x)>Math.abs(player.getY()-y)) {

            if (player.getX() - x >= 0 && world.vacantXY(x + 1, y)) {
                x++;
            } else if (player.getX() - x <= 0 && world.vacantXY(x - 1, y)) {
                x--;
            } else {
                moveUnitRandomWay(world);
            }
        } else {
            if (player.getY() - y >= 0 && world.vacantXY(x, y + 1)) {
                y++;
            } else if (player.getY() - y <= 0 && world.vacantXY(x, y - 1)) {
                y--;
            } else {
                moveUnitRandomWay(world);
            }
        }
    }

    public Player playerNearby(int x, int y, World world){
        // return the one that is nearest
        for (Player player:world.getPlayers().values()){
            if (Math.abs(player.getX() - x) < 10 && Math.abs(player.getY() - y) <10){
                return player;
            }
        }
        return null;
    }

    public void moveUnit(World world, long tickspassed){

        if (freezeEnd > tickspassed) {
            return;
        }
        Player player = playerNearby(x, y, world);
        if (player != null) {
            goToPlayer(player, world);
        } else {
            moveUnitRandomWay(world);
        }

        hasChanged = true;
    }

    public boolean frozen(long tickspassed){
        return freezeEnd > tickspassed;
    }

    public void freeze(int time, long tickspassed){
        freezeEnd = tickspassed + time;
        hasChanged = true;
    }

}
