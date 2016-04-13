package rpg.player;

import asciiPanel.AsciiPanel;

import java.awt.*;
import java.io.Serializable;

public class Player implements Serializable {

    private int x;
    private int y;
    public final char glyph = (char)254;
    public final Color color = AsciiPanel.cyan; // igal mängijal olgu erinev
    public final int connectionId;
    private boolean hasChanged;

    public Player() {
        connectionId = -1;
        System.out.println("parameetritete Player()");
    }

    public Player(int id) {
        connectionId = id;
        hasChanged = true;
    }

    public void setX(int x) {
        // checks need to happen before set
        this.x = x;
        hasChanged = true;
    }

    public void setY(int y) {
        // checks need to happen before set
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
