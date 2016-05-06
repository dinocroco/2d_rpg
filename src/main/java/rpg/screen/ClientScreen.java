package rpg.screen;

import asciiPanel.AsciiPanel;
import rpg.character.Player;
import rpg.character.Unit;
import rpg.world.AsciiSymbol;
import rpg.world.Diff;
import rpg.world.Tile;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientScreen implements Screen {
    private final int screenWidth = 80;
    private final int screenHeight = 24;
    // from WorldBuilder
    private AsciiSymbol[][] view = startView(90,31);
    private int viewX = 0;
    private int viewY = 0;
    private int playerId;
    private Map<Integer, Player> players = new HashMap<>();
    private List<Unit> units = new ArrayList<>();
    private List<Integer> keycodes = new ArrayList<>();
    private Map<Integer, Integer> keymap = new HashMap<>();

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
        displayTiles(terminal,viewX,viewY);
        displayUnits(terminal, viewX, viewY);
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
        if(keymap.containsKey(key.getKeyCode())){
            keycodes.add(keymap.get(key.getKeyCode())); //for user-configured keymap
        } else {
            keycodes.add(key.getKeyCode());
        }
        return this;
    }

    private void displayTiles(AsciiPanel terminal, int left, int top) {
        for (int x = 0; x < screenWidth; x++){
            for (int y = 0; y < screenHeight; y++){
                int wx = x + left;
                int wy = y + top;
                AsciiSymbol sym = new AsciiSymbol(Tile.BOUNDS.glyph(),Tile.BOUNDS.color());
                if(view.length>wx && wx>=0 && view[wx].length>wy && wy>=0) {
                    sym = view[wx][wy];
                }
                terminal.write(sym.getGlyph(),x,y,sym.getColor());
            }
        }
    }

    private void displayPlayers(AsciiPanel terminal, int left, int top) {
        // TODO display only works after first movement after join
        for (Player player : players.values()) {
            int wx = player.getX()-left;
            int wy = player.getY()-top;
            if(wx>=screenWidth || wx<0 ||wy>=screenHeight || wy<0) continue;
            terminal.write(player.glyph,wx,wy,player.color);
        }
    }

    private void displayUnits(AsciiPanel terminal, int left, int top) {
        for (Unit unit : units ){
            int wx = unit.getX()-left;
            int wy = unit.getY()-top;
            if(wx>=screenWidth || wx<0 ||wy>=screenHeight || wy<0) continue;
            terminal.write(unit.getGlyph(),wx,wy,unit.getColor());
        }
    }

    @Override
    public void setView(AsciiSymbol[][] view) {
        this.view = view;
    }

    public void parseDiff(Diff diff){
        if(diff.getPlayer()!=null){
            updatePlayerMap(diff);
        }
        if(diff.getUnit()!=null){
            updateUnitList(diff);
        }
    }

    private void updateUnitList(Diff diff) {
        Unit diffUnit = diff.getUnit();
        boolean foundUnit = false;
        for (int i = 0; i < units.size(); i++) {
            Unit unit= units.get(i);
            if (unit.idCode == diffUnit.idCode) {
                units.remove(i);
                units.add(diffUnit);
                foundUnit = true;
                break;
            }
        }
        if(!foundUnit) {
            units.add(diffUnit);

        }
    }

    private void updatePlayerMap(Diff diff) {
        Player diffPlayer = diff.getPlayer();
        if (diffPlayer.getX() == -1 && diffPlayer.getY() == -1) {
            players.remove(diffPlayer.getId());
            return;
        }

        players.put(diffPlayer.getId(), diffPlayer);

        if(players.containsKey(playerId)) {

            viewX = players.get(playerId).getX() - screenWidth / 2;
            viewY = players.get(playerId).getY() - screenHeight / 2;
        }
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
}
