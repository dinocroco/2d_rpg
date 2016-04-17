package rpg.screen;

import asciiPanel.AsciiPanel;
import rpg.character.Player;

import java.awt.event.KeyEvent;
import java.util.Map;

public class LoseScreen implements Screen {
    private Map<Integer,Player> players;

    public LoseScreen(Map<Integer, Player> players) {
        this.players = players;
    }

    public void displayOutput(AsciiPanel terminal) {
        terminal.write("You lost.", 1, 1);
        terminal.writeCenter("-- press [enter] to restart --", 22);
    }

    public Screen respondToUserInput(KeyEvent key) {
        return key.getKeyCode() == KeyEvent.VK_ENTER ? new StartScreen(players) : this;
    }

    @Override
    public synchronized void addPlayer(int clientIndex, Player player) {
        players.put(clientIndex,player);
    }
}
