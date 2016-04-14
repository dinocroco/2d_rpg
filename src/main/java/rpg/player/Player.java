package rpg.player;

import asciiPanel.AsciiPanel;

import java.awt.*;
import java.io.Serializable;

public class Player implements Serializable {

    private int x;
    private int y;
    public final char glyph = (char)254;
    public final Color color;
    public final int connectionId;
    private boolean hasChanged;

    // eventually load player info from somewhere instead of creating new for each connect
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

    public void setX(int x) {
        // checks need to happen before set - player class wont know anything about world
        this.x = x;
        hasChanged = true;
    }

    public void setY(int y) {
        // checks need to happen before set - player class wont know anything about world
        this.y = y;
        hasChanged = true;
    }

    public boolean hasChanged() {
        return hasChanged;
    }

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

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
