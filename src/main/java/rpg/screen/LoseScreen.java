package rpg.screen;

import asciiPanel.AsciiPanel;
import rpg.server.Server;

import java.awt.event.KeyEvent;

public class LoseScreen implements Screen {

    public void displayOutput(AsciiPanel terminal) {
        terminal.write("You lost.", 1, 1);
        terminal.writeCenter("-- press [enter] to restart --", 22);
    }

    public Screen respondToUserInput(KeyEvent key) {
        return key.getKeyCode() == KeyEvent.VK_ENTER ? new StartScreen() : this;
    }

    @Override
    public void sendOutput(Server server) {

    }
}
