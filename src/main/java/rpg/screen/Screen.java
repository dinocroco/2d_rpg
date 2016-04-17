package rpg.screen;

import asciiPanel.AsciiPanel;
import rpg.character.Player;
import rpg.server.Server;
import rpg.world.AsciiSymbol;
import rpg.world.Diff;
import rpg.world.World;

import java.awt.event.KeyEvent;
import java.util.List;

public interface Screen {
    void displayOutput(AsciiPanel terminal);

    void sendOutput(Server server);

    default List<Diff> updateDiff(){return null;}

    Screen respondToUserInput(KeyEvent key);

    default void setView(AsciiSymbol[][] view){}

    default void sendDiff(Server server, Diff diff){}

    default World getWorld(){
        return null;
    }

    default void addPlayer(int clientIndex, Player player){}
}
