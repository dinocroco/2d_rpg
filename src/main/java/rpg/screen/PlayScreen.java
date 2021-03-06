package rpg.screen;

import asciiPanel.AsciiPanel;
import rpg.character.Player;
import rpg.character.Unit;
import rpg.server.Server;
import rpg.world.AsciiSymbol;
import rpg.world.Diff;
import rpg.world.World;
import rpg.world.WorldBuilder;

import java.awt.event.KeyEvent;
import java.util.*;

/**
 * Screen that is seen by gamemaster.
 */

public class PlayScreen implements Screen {
    private World world;
    private final int viewWidth = 80;
    private final int viewHeight = 24;
    private int viewX = 0;
    private int viewY = 0;

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
            case KeyEvent.VK_LEFT: viewX = viewX<2- viewWidth ? viewX : viewX-1; break;
            case KeyEvent.VK_RIGHT: viewX = viewX>world.width()-2 ? viewX : viewX+1; break;
            case KeyEvent.VK_UP: viewY = viewY<2- viewHeight ? viewY : viewY-1; break;
            case KeyEvent.VK_DOWN: viewY = viewY>world.height()-2 ? viewY : viewY+1; break;
        }

        return this;
    }

    /**
     * Creates game world and sends players their starting location.
     */

    private void createWorld(Map<Integer,Player> players ){
        world = new WorldBuilder(width, height)
                .makeCaves()
                .build();
        for (Player player : players.values()) {
            Diff diff = world.startingPoint();
            player.setX(diff.getX());
            player.setY(diff.getY());
        }
        world.setPlayers(players);
    }

    private void displayTiles(AsciiPanel terminal, int left, int top) {
        for (int x = 0; x < viewWidth; x++){
            for (int y = 0; y < viewHeight; y++){
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
            if(wx>= viewWidth || wx<0 ||wy>= viewHeight || wy<0) continue;
            terminal.write(player.glyph,wx,wy,player.color);
        }
    }

    public void displayUnits(AsciiPanel terminal, List<Unit> units){
        for (Unit unit : units) {
            int wx = unit.getX()-viewX;
            int wy = unit.getY()-viewY;
            if(wx>= viewWidth || wx<0 ||wy>= viewHeight || wy<0) continue;
            terminal.write(unit.getGlyph(),wx,wy,unit.getColor());
        }
    }

    @Override
    public void sendWorldTerrain(Server server) {
        AsciiSymbol[][] symbols = new AsciiSymbol[width][height];
        for(int i=0;i<width;i++){
            for(int j=0;j<height;j++){
                symbols[i][j] = new AsciiSymbol(world.glyph(i,j),world.color(i,j));
            }
        }
        server.sendToAll(symbols);
    }

    @Override
    public synchronized void sendDiff(Server server, Diff diff) {
        for (Player player : world.getPlayers().values()) {
            if(diff.getPlayer()!=null){
                // safer to just send players always
                server.sendToAll(diff);
                return;
            }
            if(diff.getUnit()!=null){
                // units move constantly, so they will get updated anyways
                if(diff.getUnit().getHealth()<=0 || Math.abs(diff.getUnit().getX()-player.getX())<40 && Math.abs(diff.getUnit().getY()-player.getY())<12){
                    server.sendToOne(player.getId(),diff);
                    continue;
                }
            }
            // tile has to be always sent
            if(diff.getTile()!=null){
                server.sendToAll(diff);
                return;
            }
            if(diff.getMessage()!=null){
                double distance = (player.getX() - diff.getX()) * (player.getX() - diff.getX()) + (player.getY() - diff.getY()) + (player.getY() - diff.getY());
                if(Math.sqrt(distance)<44+diff.getR()){
                    server.sendToOne(player.getId(),diff);
                    continue;
                }
            }
            assert(false);
        }
    }

    @Override
    public synchronized List<Diff> updateDiff() {
        List<Diff> diff = new ArrayList<>(world.getDiff());
        if(diff.isEmpty()){
            return null;
        }
        world.clearDiff();
        return diff;
    }

    public World getWorld() {
        return world;
    }
}
