package action;

public class Movement extends GameAction{
    private int down;
    private int right;

    public Movement(int priority, int down, int right) {
        super(priority);
        this.down = down;
        this.right = right;
    }

    @Override
    public void run() {

    }
}
