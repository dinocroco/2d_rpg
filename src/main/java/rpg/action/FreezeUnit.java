package rpg.action;

import rpg.Tick;
import rpg.character.Player;
import rpg.character.Unit;
import rpg.screen.Screen;
import rpg.world.Diff;


public class FreezeUnit extends GameAction{

    public final Unit unit;
    public final int time;
    public final Player player;

    public FreezeUnit(Player player, Unit unit, int time) {
        super(player.getID());
        this.unit = unit;
        this.time = time;
        this.player = player;

    }

    @Override
    public void executeAction(Screen screen, long tickspassed) {

        if (player.getFreezeAbility()>0) {
            unit.freeze(time, tickspassed);
            screen.getWorld().addDiff(new Diff(characterID + " froze " + unit.getID() + ", health: "
                    + unit.getHealth() + " for " + time * Tick.TICK_LENGTH / 1000000000 + " seconds", unit.getX(), unit.getY(), 15));

            player.addFreezeAbility(-1);
        }
    }
}

