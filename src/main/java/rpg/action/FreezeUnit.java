package rpg.action;

import rpg.Tick;
import rpg.character.Player;
import rpg.character.Unit;
import rpg.screen.Screen;
import rpg.world.Diff;


public class FreezeUnit extends GameAction{

    private final Unit unit;
    public final int time;
    private final Player player;

    public FreezeUnit(Player player, Unit unit, int time) {
        super(player.getID());
        this.unit = unit;
        this.time = time;
        this.player = player;
    }

    @Override
    public void executeAction(Screen screen, long tickspassed) {
        if (characterID < 1000) {
            unit.freeze(time, tickspassed);
            screen.getWorld().addDiff(new Diff(player.toMessage() + " froze " + unit.toMessage() + ", health: "
                    +unit.getHealth() + " for " + time* Tick.TICK_LENGTH/1000000000 + " seconds",unit.getX(),unit.getY(),15));
        }
    }
}

