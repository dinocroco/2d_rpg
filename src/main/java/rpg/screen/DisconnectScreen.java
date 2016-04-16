package rpg.screen;


import asciiPanel.AsciiPanel;
import rpg.server.Server;
import java.awt.event.KeyEvent;


public class DisconnectScreen implements Screen{

    @Override
    public void displayOutput(AsciiPanel terminal) {
        terminal.write("Disconnected from server", 1, 1);

    }

    @Override
    public void sendOutput(Server server) {

    }

    @Override
    public Screen respondToUserInput(KeyEvent key) {
        return this;
    }
}
