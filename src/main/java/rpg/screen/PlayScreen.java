package rpg.screen;

import asciiPanel.AsciiPanel;
import rpg.action.GameAction;
import rpg.player.Player;
import rpg.server.Server;
import rpg.world.AsciiSymbol;
import rpg.world.World;
import rpg.world.WorldBuilder;

import java.awt.event.KeyEvent;
import java.util.TreeSet;

public class PlayScreen implements Screen {
    private World world;
    Player player;
    private int screenWidth = 80;
    private int screenHeight = 24;
    private int viewX = 0;
    private int viewY = 0;

    // these will not remain as constants
    final int width = 90;
    final int height = 31;

    public PlayScreen() {
        createWorld();
    }

    public void displayOutput(AsciiPanel terminal) {
        displayTiles(terminal,viewX,viewY);
    }

    public Screen respondToUserInput(KeyEvent key) {
        switch (key.getKeyCode()){
            case KeyEvent.VK_ESCAPE: return new LoseScreen();
            case KeyEvent.VK_ENTER: return new WinScreen();
            case KeyEvent.VK_LEFT: viewX = viewX<2-screenWidth ? viewX : viewX-1; break;
            case KeyEvent.VK_RIGHT: viewX = viewX>world.width()-2 ? viewX : viewX+1; break;
            case KeyEvent.VK_UP: viewY = viewY<2-screenHeight ? viewY : viewY-1; break;
            case KeyEvent.VK_DOWN: viewY = viewY>world.height()-2 ? viewY : viewY+1; break;
        }

        return this;
    }

    private void createWorld(){
        world = new WorldBuilder(width, height)
                .makeCaves()
                .build();
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
        //terminal.write(rpg.player.glyph(), rpg.player.x - left, rpg.player.y - top, rpg.player.color());
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
}
