import asciiPanel.AsciiPanel;
import java.awt.event.KeyEvent;
/**
 * Created by Ravana on 9.03.2016.
 */
public class StartScreen implements Screen{
    @Override
    public void displayOutput(AsciiPanel terminal) {
        terminal.write("rl tutorial", 1, 1);
        terminal.writeCenter("-- press [enter] to start --", 22);
    }

    @Override
    public Screen respondToUserInput(KeyEvent key) {
        return this;
        //return key.getKeyCode() == KeyEvent.VK_ENTER ? new PlayScreen() : this;
    }
}
