package rpg.action;

import java.io.Serializable;

public abstract class GameAction implements Comparable<GameAction>, Serializable {

    private int priority = 0;
    public final long characterID;


    public GameAction(long characterID) {
        this.characterID = characterID;
    }


    @Override
    public int compareTo(GameAction o) {
        if (priority > o.priority) {
            return 1;
        } else if (priority < o.priority){
            return -1;
        }
        return 0;
    }
}
