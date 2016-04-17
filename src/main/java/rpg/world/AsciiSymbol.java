package rpg.world;


import java.awt.*;
import java.io.Serializable;

/**
 * Wrapper class for describing Tile
 */

public class AsciiSymbol implements Serializable {
    private char glyph;
    private Color color;

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

}
