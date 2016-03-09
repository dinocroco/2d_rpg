import java.awt.event.KeyEvent;
import asciiPanel.AsciiPanel;
/**
 * Created by Ravana on 9.03.2016.
 */

public interface Screen {
    public void displayOutput(AsciiPanel terminal);

    public Screen respondToUserInput(KeyEvent key);
}
