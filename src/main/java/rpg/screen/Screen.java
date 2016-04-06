package rpg.screen;

import asciiPanel.AsciiPanel;
import rpg.server.Server;
import rpg.world.AsciiSymbol;

import java.awt.event.KeyEvent;

public interface Screen {
    void displayOutput(AsciiPanel terminal);

    void sendOutput(Server server);

    Screen respondToUserInput(KeyEvent key);

    default void setView(AsciiSymbol[][] view){
        return;
    }

}
