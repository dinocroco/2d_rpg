package rpg.player;

import asciiPanel.AsciiPanel;

import java.awt.*;

public class Player {

    public int x;
    public int y;
    public final char glyph = (char)254;
    public final Color color = AsciiPanel.cyan;
    public final int connectionId;

    public Player(int id) {
        connectionId = id;
    }

}
