package rpg.screen;

import asciiPanel.AsciiPanel;
import rpg.character.Player;
import rpg.server.Server;

import java.awt.event.KeyEvent;
import java.util.Map;

public class WinScreen implements Screen {
    private Map<Integer,Player> players;

    public WinScreen(Map<Integer, Player> players) {
        this.players = players;
    }

    public void displayOutput(AsciiPanel terminal) {
        terminal.write("You won.", 1, 1);
        terminal.writeCenter("-- press [enter] to restart --", 22);
    }

    public Screen respondToUserInput(KeyEvent key) {
        return key.getKeyCode() == KeyEvent.VK_ENTER ? new StartScreen(players) : this;
    }

    @Override
    public void sendOutput(Server server) {

    }

    @Override
    public synchronized void addPlayer(int clientIndex, Player player) {
        players.put(clientIndex,player);
    }
}

