package rpg.action;

import rpg.character.GameCharacter;

public class Movement extends GameAction{
    public final int down;
    public final int right;

    public Movement(long characterID, int down, int right) {
        super(characterID);
        this.down = down;
        this.right = right;
    }



}
