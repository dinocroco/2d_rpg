package rpg.screen;

import asciiPanel.AsciiPanel;

import java.awt.event.KeyEvent;

public class ClientScreen implements Screen {
    @Override
    public void displayOutput(AsciiPanel terminal) {

    }

    @Override
    public Screen respondToUserInput(KeyEvent key) {
        return null;
    }
}
