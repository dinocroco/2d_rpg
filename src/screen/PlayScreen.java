package screen;

import java.awt.event.KeyEvent;
import java.util.List;

import action.GameAction;
import action.Movement;
import asciiPanel.AsciiPanel;
import world.World;
import world.WorldBuilder;

public class PlayScreen implements Screen {
    private World world;
    private int screenWidth = 80;
    private int screenHeight = 24;
    private int viewX = 0;
    private int viewY = 0;

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
        world = new WorldBuilder(90, 31)
                .makeCaves()
                .build();
    }

    public void AddEvent(List<List<GameAction>> events, GameAction action){
        events.get(0).add(action);
    }

    private void displayTiles(AsciiPanel terminal, int left, int top) {
        for (int x = 0; x < screenWidth; x++){
            for (int y = 0; y < screenHeight; y++){
                int wx = x + left;
                int wy = y + top;

                terminal.write(world.glyph(wx, wy), x, y, world.color(wx, wy));
            }
        }
        //terminal.write(player.glyph(), player.x - left, player.y - top, player.color());
    }
}
