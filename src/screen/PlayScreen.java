package screen;

import java.awt.event.KeyEvent;
import java.util.List;

import action.GameAction;
import action.Movement;
import asciiPanel.AsciiPanel;

public class PlayScreen implements Screen {

    public void displayOutput(AsciiPanel terminal) {
        terminal.write("You are having fun.", 1, 1);
        terminal.writeCenter("-- press [escape] to lose or [enter] to win --", 22);
    }

    public Screen respondToUserInput(KeyEvent key) {
        switch (key.getKeyCode()){
            case KeyEvent.VK_ESCAPE: return new LoseScreen();
            case KeyEvent.VK_ENTER: return new WinScreen();
            case KeyEvent.VK_LEFT:
                Movement movement = new Movement(0,-1);
                //AddEvent();
        }

        return this;
    }

    public void AddEvent(List<List<GameAction>> events, GameAction action){
        events.get(0).add(action);
    }
}
