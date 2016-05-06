package rpg.action;

import rpg.character.Player;
import rpg.screen.Screen;

public class Movement extends GameAction{
    public final int down;
    public final int right;

    public Movement(long characterID, int right, int down) {
        super(characterID);
        this.down = down;
        this.right = right;
    }

    @Override
    public void executeAction(Screen screen, long tickspassed) {
        if (characterID < 1000) {
            Player player = screen.getWorld().getPlayers().get((int) characterID);
            if(player==null){
                System.out.println("Movement-> player is null");
                return;
            }
            if(screen.getWorld().vacantXY(player.getX()+right,player.getY()+down)){
                player.addToXY(right, down);
            }
        }
    }
}
