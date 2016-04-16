package rpg.action;

public class Movement extends GameAction{
    public final int down;
    public final int right;

    public Movement(long characterID, int right, int down) {
        super(characterID);
        this.down = down;
        this.right = right;
    }
}
