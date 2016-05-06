package rpg.action;

import rpg.character.GameCharacter;
import rpg.screen.Screen;

public class Attack extends GameAction{

    private final GameCharacter[] targets;
    private final int damage;

    public Attack(GameCharacter[] targets, GameCharacter attacker, int damage) {
        super(attacker.getID());
        this.targets = targets;
        this.damage = damage;
    }

    @Override
    public void executeAction(Screen screen, long tickspassed) {

        for (GameCharacter target : targets) {
            if (target!=null) {
                target.addHealth(-damage);
            }
        }
    }
}