package rpg.world;

import java.awt.*;
import java.io.Serializable;

/**
 * Created by Ravana on 6.04.2016.
 */
public class AsciiSymbol implements Serializable {
    private char glyph;
    private Color color;

    public AsciiSymbol() {
    }

    public AsciiSymbol(char glyph, Color color) {
        this.glyph = glyph;
        this.color = color;
    }

    public char getGlyph() {
        return glyph;
    }

    public Color getColor() {
        return color;
    }

    public void setGlyph(char glyph) {
        this.glyph = glyph;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
