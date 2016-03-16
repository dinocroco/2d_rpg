package action;

/**
 * Created by Ravana on 16.03.2016.
 */
public class Movement implements GameAction {
    private int down;
    private int right;

    public Movement(int down, int right) {
        this.down = down;
        this.right = right;
    }

    @Override
    public void run() {

    }
}
