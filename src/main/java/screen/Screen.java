package screen;

import java.awt.event.KeyEvent;
import asciiPanel.AsciiPanel;

public interface Screen {
    void displayOutput(AsciiPanel terminal);

    Screen respondToUserInput(KeyEvent key);

}
