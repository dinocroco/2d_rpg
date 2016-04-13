package rpg.screen;

import asciiPanel.AsciiPanel;
import rpg.player.Player;
import rpg.server.Server;
import rpg.world.AsciiSymbol;
import rpg.world.Diff;

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
    private List<Player> players = new ArrayList<>();

    private List<Integer> keycodes = new ArrayList<>();

    public int[] getKeycodes() {
        int[] keycodesarray= new int[10];
        int index = 0;
        for (int i = 0; i < keycodesarray.length; i++, index++) {
            if(i< keycodes.size() && keycodes.get(i)!=null){
                keycodesarray[index]=keycodes.get(i);
                keycodes.remove(i);
                i--;
            }
        }
        int count = 0;
        for (int i : keycodesarray) {
            if(i == 0) continue;
            count++;
        }
        if(count==0){
            return new int[0];
        }
        int[] notNullKeycodes = new int[count];
        index = 0;
        for(int i: keycodesarray){
            if(i==0) continue;
            notNullKeycodes[index] = i;
            index++;
        }
        return notNullKeycodes;
    }

    @Override
    public void displayOutput(AsciiPanel terminal) {
        //System.out.println("displayOutput redraw");
        displayTiles(terminal,viewX,viewY);
        displayPlayers(terminal,viewX,viewY);
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
        keycodes.add(key.getKeyCode());
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

    private void displayPlayers(AsciiPanel terminal, int viewX, int viewY) {
        //System.out.println("displaying players");
        //System.out.println(players.size());
        for (Player player : players) {
            //System.out.println("displaying player");
            // TODO check if player is on seen screen
            int wx = viewX + player.getX();
            int wy = viewY + player.getY();
            //System.out.println(player.glyph);
            terminal.write(player.glyph,wx,wy,player.color);
        }
    }

    @Override
    public void sendOutput(Server server) {


    }

    @Override
    public void setView(AsciiSymbol[][] view) {
        this.view = view;
    }

    public void parseDiff(Diff diff){
        if(diff.getPlayer()!=null){
            System.out.println("parsing found player");
            Player diffPlayer = diff.getPlayer();
            // find player with same id and replace it
            for (int i = 0; i < players.size(); i++) {
                Player player = players.get(i);
                if (player.connectionId == diffPlayer.connectionId) {
                    //System.out.println("replacing old player");
                    //System.out.println(players.get(i).getX());
                    players.remove(i);
                    players.add(diffPlayer);
                    //System.out.println(players.get(players.size()-1).getX());
                    return;
                }
            }
            // so player with this id not found
            players.add(diffPlayer);
        }
    }
}
