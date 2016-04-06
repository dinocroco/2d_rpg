package rpg.action;

public abstract class GameAction implements Comparable<GameAction>  {

    private final int priority;

    public abstract void run();

    public GameAction(int priority) {
        this.priority = priority;
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
