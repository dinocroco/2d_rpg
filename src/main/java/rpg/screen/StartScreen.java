package rpg.screen;

import asciiPanel.AsciiPanel;
import rpg.character.Player;
import rpg.server.Server;

import java.awt.event.KeyEvent;
import java.util.Map;

public class StartScreen implements Screen{
    private Map<Integer,Player> players;

    public StartScreen(Map<Integer, Player> players) {
        this.players = players;
    }

    @Override
    public void displayOutput(AsciiPanel terminal) {
        // hetkel on 24*80, vajadusel saan uue jar faili teha teise suurusega
        terminal.write("rl tutorial", 1, 1);
        terminal.writeCenter("-- press [enter] to start --", 22);
    }

    @Override
    public Screen respondToUserInput(KeyEvent key) {
        return key.getKeyCode() == KeyEvent.VK_ENTER ? new PlayScreen(players) : this;
    }

    @Override
    public void sendOutput(Server server) {

    }

    @Override
    public synchronized void addPlayer(int clientIndex, Player player) {
        players.put(clientIndex,player);
    }
}
