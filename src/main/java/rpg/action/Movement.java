package rpg.action;

import rpg.character.Player;
import rpg.screen.Screen;

public class Movement extends GameAction{
    public final int down;
    public final int right;
    private final Player player;
    private boolean dig = false;

    public Movement(Player player, int right, int down, boolean dig) {
        super(player.getID());
        this.down = down;
        this.right = right;
        this.player = player;
        this.dig = dig;
    }

    @Override
    public void executeAction(Screen screen, long tickspassed) {
        if (characterID < 1000) {
            if(screen.getWorld().vacantXY(player.getX()+right,player.getY()+down)){
                player.addToXY(right, down);
            } else if (dig) {
                screen.getWorld().dig(player.getX()+right,player.getY()+down);
            }
        }
    }
}
