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

    public List<GameAction> actions = new ArrayList<>();

    public List<GameAction> getActions(){
        List<GameAction> newActions = new ArrayList<>(actions);
        actions.clear();
        return newActions;
    }

    public void doAction(GameAction action){
        actions.add(action);
    }


}
