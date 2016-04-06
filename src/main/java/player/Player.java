package player;

import action.GameAction;
import asciiPanel.AsciiPanel;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Player {

    public int x;
    public int y;
    public final char glyph = (char)254;
    public final Color color = AsciiPanel.cyan;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
    }

}
