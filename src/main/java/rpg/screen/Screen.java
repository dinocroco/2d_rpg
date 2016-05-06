package rpg.screen;

import asciiPanel.AsciiPanel;
import rpg.character.Player;
import rpg.character.Unit;
import rpg.server.Server;
import rpg.world.AsciiSymbol;
import rpg.world.Diff;
import rpg.world.World;

import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Map;

public interface Screen {
    void displayOutput(AsciiPanel terminal);

    default void sendWorldTerrain(Server server){}

    default List<Diff> updateDiff(){return null;}

    Screen respondToUserInput(KeyEvent key);

    default void setView(AsciiSymbol[][] view){}

    default void sendDiff(Server server, Diff diff){}

    default World getWorld(){
        return null;
    }

    default void addPlayer(int clientIndex, Player player){}

    default int[] getKeycodes(){
        return null;
    }

    default void parseDiff(Diff diff){
    }

    default void setPlayerId(int playerId){}

    default void displayUnits(AsciiPanel terminal, List<Unit> units){
    }

    default void displayPlayers(AsciiPanel terminal, Map<Integer,Player> players){}

}
