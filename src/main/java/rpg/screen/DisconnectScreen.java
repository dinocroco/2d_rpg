package rpg.screen;


import asciiPanel.AsciiPanel;

import java.awt.event.KeyEvent;


public class DisconnectScreen implements Screen{

    @Override
    public void displayOutput(AsciiPanel terminal) {
        terminal.write("Disconnected from server", 1, 1);
        terminal.write("Press Enter to quit", 1, 5);
    }

    @Override
    public Screen respondToUserInput(KeyEvent key) {
        switch (key.getKeyCode()){
            case KeyEvent.VK_ENTER:
                return null;
        }
        return this;
    }
}
