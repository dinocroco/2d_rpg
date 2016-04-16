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
    private int health;
    private List<Color> colors = new ArrayList<>();
    public final long idCode;

    public Unit(long tickNumber){
        Random rand = new Random();
        this.color = new Color(rand.nextInt(0xFFFFFF));
        this.idCode=tickNumber+1000;//>1000 so it won't match player id
        hasChanged = true;
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


    public void moveUnit(World world){
        Random rand = new Random();
        int randomInt = rand.nextInt(10);
        if(randomInt == 1) {
            if (world.vacantXY(x, y - 1)) {
                y--;
            } else if (world.vacantXY(x, y + 1)) {
                y++;
            }
            hasChanged = true;
        }
    }

}
