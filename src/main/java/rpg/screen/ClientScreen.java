package rpg.screen;

import asciiPanel.AsciiPanel;
import rpg.world.AsciiSymbol;
import rpg.server.Server;

import java.awt.*;
import java.awt.event.KeyEvent;

public class ClientScreen implements Screen {
    private int screenWidth = 80;
    private int screenHeight = 24;
    // from WorldBuilder
    private AsciiSymbol[][] view = startView(90,31);
    private int viewX = 0;
    private int viewY = 0;

    @Override
    public void displayOutput(AsciiPanel terminal) {
        displayTiles(terminal,viewX,viewY);
    }

    private AsciiSymbol[][] startView(int width, int height){

        AsciiSymbol[][] startview = new AsciiSymbol[width][height];

        for (int x = 0; x < width; x++){
            for (int y = 0; y < height; y++){
                startview[x][y]=new AsciiSymbol('.',Color.white);
            }
        }
        return startview;
    }

    @Override
    public Screen respondToUserInput(KeyEvent key) {
        return this;
    }

    private void displayTiles(AsciiPanel terminal, int left, int top) {
        for (int x = 0; x < screenWidth; x++){
            for (int y = 0; y < screenHeight; y++){
                int wx = x + left;
                int wy = y + top;
                AsciiSymbol sym = view[wx][wy];
                terminal.write(sym.getGlyph(),wx,wy,sym.getColor());
            }
        }
    }

    @Override
    public void sendOutput(Server server) {

    }

    @Override
    public void setView(AsciiSymbol[][] view) {
        this.view = view;
    }
}
