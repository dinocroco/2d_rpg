package rpg.action;

import rpg.character.GameCharacter;
import rpg.character.Player;
import rpg.screen.Screen;
import rpg.world.Diff;

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
                if(player.getAttackCounter()+1==player.getAttackSpeed()) {
                    target.addHealth(-damage);
                    screen.getWorld().addDiff(new Diff(characterID + " attacked " + target.getID() + ", health now: "
                            + target.getHealth(), target.getX(), target.getY(), 15));
                }
                player.setAttackCounter((player.getAttackCounter()+1)%player.getAttackSpeed());

            }
        }
    }
}
