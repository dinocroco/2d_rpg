package rpg.action;

import rpg.Tick;
import rpg.character.Player;
import rpg.character.Unit;
import rpg.screen.Screen;
import rpg.world.Diff;


public class FreezeUnit extends GameAction{

    public final Unit unit;
    public final int time;

    public FreezeUnit(long characterID, Unit unit, int time) {
        super(characterID);
        this.unit = unit;
        this.time = time;
    }

    @Override
    public void executeAction(Screen screen, long tickspassed) {
        if (characterID < 1000) {
            unit.freeze(time, tickspassed);
            screen.getWorld().addDiff(new Diff(characterID + " froze " + unit.getID() + ", health: "
                    +unit.getHealth() + " for " + time* Tick.TICK_LENGTH/1000000000 + " seconds",unit.getX(),unit.getY(),15));
        }
    }
}

