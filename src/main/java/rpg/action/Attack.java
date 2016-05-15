package rpg.action;

import rpg.character.GameCharacter;
import rpg.character.Player;
import rpg.screen.Screen;
import rpg.world.Diff;
import rpg.world.World;

public class Attack extends GameAction{

    private final GameCharacter[] targets;
    private final int damage;
    private final Player player;

    public Attack(GameCharacter[] targets, Player attacker, int damage) {
        super(attacker.getID());
        this.targets = targets;
        this.damage = damage;
        this.player = attacker;
    }

    @Override
    public void executeAction(Screen screen, long tickspassed) {

        for (GameCharacter target : targets) {
            if (target!=null) {
                if(player.getLastAttackTime()+player.getAttackSpeed()<tickspassed) {
                    target.addHealth(-damage);
                    player.setLastAttackTime(tickspassed);
                    if(target.getHealth()<=0){
                        player.receiveKill(target);
                        if(player.leveled()) {
                            player.leveled(false);
                            screen.getWorld().addDiff(new Diff(player.toMessage() + " killed "
                                    + target.toMessage() + " and leveled up!",
                                    target.getX(), target.getY(), World.HEARINGRADIUS));
                        } else {
                            screen.getWorld().addDiff(new Diff(player.toMessage() + " killed " + target.toMessage(), target.getX(), target.getY(), 100));

                        }
                    } else {
                        screen.getWorld().addDiff(new Diff(player.toMessage() + " attacked "
                                + target.toMessage() + ", HP -> "
                                + target.getHealth(), target.getX(), target.getY(), World.HEARINGRADIUS));
                    }
                }

            }
        }
    }
}
