package rpg.character;

import asciiPanel.AsciiPanel;

import java.awt.*;
import java.io.Serializable;

public class Player implements Serializable, GameCharacter {

    private int x;
    private int y;
    public final char glyph = (char)254;
    public final Color color;
    public final int connectionId;
    private boolean hasChanged;

    // eventually load character info from somewhere instead of creating new for each connect
    public Player(int id) {
        connectionId = id;
        color = AsciiPanel.cyan;
        hasChanged = true;
    }

    public Player(int id, Color color) {
        connectionId = id;
        this.color = color;
        hasChanged = true;
    }

    @Override
    public void setX(int x) {
        // checks need to happen before set - character class wont know anything about world
        this.x = x;
        hasChanged = true;
    }

    @Override
    public void setY(int y) {
        // checks need to happen before set - character class wont know anything about world
        this.y = y;
        hasChanged = true;
    }

    @Override
    public boolean hasChanged() {
        return hasChanged;
    }

    @Override
    public void toUnchanged() {
        this.hasChanged = false;
    }

    @Override
    public String toString() {
        return "Player{" +
                "x=" + x +
                ", y=" + y +
                ", glyph=" + glyph +
                ", color=" + color +
                ", connectionId=" + connectionId +
                '}';
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }
}
