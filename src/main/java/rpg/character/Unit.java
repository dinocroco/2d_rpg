package rpg.character;


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
        this.idCode=tickNumber;
        hasChanged = true;
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
}
