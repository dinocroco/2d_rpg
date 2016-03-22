package player;

import action.GameAction;
import screen.Screen;
import screen.StartScreen;

import java.util.ArrayList;
import java.util.List;

public class Player {

    public int x;
    public int y;

    List<GameAction> actions = new ArrayList<>();

    public void doAction(GameAction action){
        actions.add(action);
    }


}
