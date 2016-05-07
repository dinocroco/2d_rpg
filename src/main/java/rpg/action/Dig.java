package rpg.action;

import rpg.character.Player;
import rpg.screen.Screen;

public class Dig extends GameAction{

    private final int x;
    private final int y;
    private final Player player;

    public Dig(Player player) {
        super(player.getID());
        this.x = player.getX() + player.getDeltaX();
        this.y = player.getY() + player.getDeltaY();
        this.player = player;
    }

    @Override
    public void executeAction(Screen screen, long tickspassed) {

        screen.getWorld().dig(x,y);

    }
}
