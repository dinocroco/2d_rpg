package rpg.action;

import rpg.character.Player;
import rpg.character.Unit;


public class FreezeUnit extends GameAction{

    public final Unit unit;
    public final int time;

    public FreezeUnit(long characterID, Unit unit, int time) {
        super(characterID);
        this.unit = unit;
        this.time = time;
    }



}

