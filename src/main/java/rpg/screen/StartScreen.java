package rpg.screen;

import asciiPanel.AsciiPanel;
import rpg.server.Server;

import java.awt.event.KeyEvent;

public class StartScreen implements Screen{
    @Override
    public void displayOutput(AsciiPanel terminal) {
        // hetkel on 24*80, vajadusel saan uue jar faili teha teise suurusega
        terminal.write("rl tutorial", 1, 1);
        terminal.writeCenter("-- press [enter] to start --", 22);
    }

    @Override
    public Screen respondToUserInput(KeyEvent key) {
        return key.getKeyCode() == KeyEvent.VK_ENTER ? new PlayScreen() : this;
    }

    @Override
    public void sendOutput(Server server) {

    }
}
