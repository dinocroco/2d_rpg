package rpg.screen;

import asciiPanel.AsciiPanel;
import rpg.world.AsciiSymbol;
import rpg.server.Server;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class ClientScreen implements Screen {
    private int screenWidth = 80;
    private int screenHeight = 24;
    // from WorldBuilder
    private AsciiSymbol[][] view = startView(90,31);
    private int viewX = 0;
    private int viewY = 0;
    private int[] keycodesarray= new int[10];

    private List<Integer> keycodes = new ArrayList<>();

    public int[] getKeycodes() {
        int index = 0;
        for (int i = 0; i < keycodesarray.length; i++, index++) {
            if(i< keycodes.size() && keycodes.get(i)!=null){
                keycodesarray[index]=keycodes.get(i);
                keycodes.remove(i);
                i--;
            }
        }
        for (int i : keycodesarray) {
            System.out.println(i);
        }
        return keycodesarray;
    }

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
        System.out.println("clientscreen responding to user input"+key.getKeyCode());
        keycodes.add(key.getKeyCode());
        System.out.println("keycode added to list");
        System.out.println(keycodes);
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
