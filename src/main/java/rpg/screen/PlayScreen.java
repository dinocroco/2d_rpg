package rpg.screen;

import asciiPanel.AsciiPanel;
import rpg.action.GameAction;
import rpg.character.Player;
import rpg.character.Unit;
import rpg.server.Server;
import rpg.world.AsciiSymbol;
import rpg.world.Diff;
import rpg.world.World;
import rpg.world.WorldBuilder;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class PlayScreen implements Screen {
    private World world;
    private final int screenWidth = 80;
    private final int screenHeight = 24;
    private int viewX = 0;
    private int viewY = 0;

    // these will not remain as constants
    final int width = 90;
    final int height = 31;



    public PlayScreen(Map<Integer,Player> players) {
        createWorld(players);
    }

    public void displayOutput(AsciiPanel terminal) {
        displayTiles(terminal,viewX,viewY);
    }

    public Screen respondToUserInput(KeyEvent key) {
        switch (key.getKeyCode()){
            case KeyEvent.VK_ESCAPE: return new LoseScreen(world.getPlayers());
            case KeyEvent.VK_ENTER: return new WinScreen(world.getPlayers());
            case KeyEvent.VK_LEFT: viewX = viewX<2-screenWidth ? viewX : viewX-1; break;
            case KeyEvent.VK_RIGHT: viewX = viewX>world.width()-2 ? viewX : viewX+1; break;
            case KeyEvent.VK_UP: viewY = viewY<2-screenHeight ? viewY : viewY-1; break;
            case KeyEvent.VK_DOWN: viewY = viewY>world.height()-2 ? viewY : viewY+1; break;
        }

        return this;
    }

    private void createWorld(Map<Integer,Player> players ){
        world = new WorldBuilder(width, height)
                .makeCaves()
                .build();
        for (Player player : players.values()) {
            Diff diff = world.playerStartingPoint();
            player.setX(diff.getX());
            player.setY(diff.getY());
        }
        world.setPlayers(players);
    }

    public synchronized void AddEvent(TreeSet<GameAction> events, GameAction action){
        events.add(action);
    }

    private void displayTiles(AsciiPanel terminal, int left, int top) {
        for (int x = 0; x < screenWidth; x++){
            for (int y = 0; y < screenHeight; y++){
                int wx = x + left;
                int wy = y + top;

                terminal.write(world.glyph(wx, wy), x, y, world.color(wx, wy));
            }
        }
    }

    public void displayPlayers(AsciiPanel terminal, Map<Integer,Player> players){
        for (Player player : players.values()) {
            int wx = player.getX()-viewX;
            int wy = player.getY()-viewY;
            if(wx>=screenWidth || wx<0 ||wy>=screenHeight || wy<0) continue;
            terminal.write(player.glyph,wx,wy,player.color);
        }
    }

    public void displayUnits(AsciiPanel terminal, List<Unit> units){
        for (Unit unit : units) {
            int wx = unit.getX()-viewX;
            int wy = unit.getY()-viewY;
            if(wx>=screenWidth || wx<0 ||wy>=screenHeight || wy<0) continue;
            terminal.write(unit.getGlyph(),wx,wy,unit.getColor());
        }
    }

    @Override
    public void sendOutput(Server server) {
        AsciiSymbol[][] symbols = new AsciiSymbol[width][height];
        for(int i=0;i<width;i++){
            for(int j=0;j<height;j++){
                symbols[i][j] = new AsciiSymbol(world.glyph(i,j),world.color(i,j));
            }
        }
        server.sendToAll(symbols);
    }

    @Override
    public void sendDiff(Server server, Diff diff) {
        //System.out.println("sending one diff to all");
        server.sendToAll(diff);
    }

    @Override
    public synchronized List<Diff> updateDiff() {
        // send world.getDiff
        List<Diff> diff = new ArrayList<>(world.getDiff());
        if(diff.isEmpty()){
            // nothing to send
            return null;
        }
        world.clearDiff();
        if(diff.size()>0){
            System.out.println("playscreen updatediff "+diff.size());
        }
        return diff;
    }

    public World getWorld() {
        return world;
    }
}
