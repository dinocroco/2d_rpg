package rpg.screen;

import asciiPanel.AsciiPanel;
import rpg.character.Player;
import rpg.character.Unit;
import rpg.client.KeyEventWrapper;
import rpg.world.AsciiSymbol;
import rpg.world.Diff;
import rpg.world.Tile;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.List;

public class ClientScreen implements Screen {
    private final int viewWidth = 80;
    private final int viewHeight = 24;
    private final int totalWidth = 80;
    private final int totalHeight = 30;
    // from WorldBuilder
    private AsciiSymbol[][] view = startView(90,31);
    private int viewX = 0;
    private int viewY = 0;
    private int playerId;
    private Map<Integer, Player> players = new HashMap<>();
    private List<Unit> units = new ArrayList<>();
    private List<KeyEvent> keyEvents = new ArrayList<>();
    private Map<Integer, Integer> keymap = new HashMap<>();
    private LinkedList<String> messages = new LinkedList<>();

    public KeyEventWrapper[] getKeyEvents() {
        KeyEvent[] keyeventsArray= new KeyEvent[10];
        int index = 0;
        for (int i = 0; i < keyeventsArray.length; i++, index++) {
            if(i< keyEvents.size() && keyEvents.get(i)!=null){
                keyeventsArray[index]= keyEvents.get(i);
                keyEvents.remove(i);
                i--;
            }
        }
        int count = 0;
        for (KeyEvent i : keyeventsArray) {
            if(i == null) continue;
            count++;
        }
        if(count==0){
            return new KeyEventWrapper[0];
        }
        KeyEventWrapper[] notNullKeyEvents = new KeyEventWrapper[count];
        index = 0;
        for(KeyEvent i: keyeventsArray){
            if(i==null) continue;
            notNullKeyEvents[index] = new KeyEventWrapper(i.getKeyCode(),i.isShiftDown(),i.isAltDown(),i.isControlDown());
            index++;
        }
        return notNullKeyEvents;
    }

    @Override
    public void displayOutput(AsciiPanel terminal) {
        displayTiles(terminal,viewX,viewY);
        displayUnits(terminal, viewX, viewY);
        displayPlayers(terminal,viewX,viewY);
        displayMessages(terminal);
    }

    private void displayMessages(AsciiPanel terminal) {
        //for (int i = Math.min(totalHeight - viewHeight-1,messages.size()-1); i >= 0; i--) {
        for (int i = 0; i < Math.min(totalHeight - viewHeight,messages.size()); i++) {
            while (messages.size()<totalHeight-viewHeight){
                messages.add("");
            }
            String line = messages.get(totalHeight-viewHeight-i-1);
            terminal.clear(' ', 0, viewHeight+i, totalWidth, 1);
            if(line.length()==totalWidth) {
                terminal.write(line.substring(0,totalWidth-1), 0, viewHeight + i);
                // küllap on nii vaja sellepärast et stringi lõpus mingi jupp ütleb et see on lõpp
                char c = line.charAt(totalWidth-1);
                terminal.write(c,totalWidth-1,viewHeight+i);
            } else {
                terminal.write(line, 0, viewHeight + i);
            }
        }
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
/*        if(keymap.containsKey(key.getKeyCode())){
            keyEvents.add(keymap.get(key.getKeyCode())); //for user-configured keymap
        } else {*/

        keyEvents.add(key);

        if (key.getKeyCode() == KeyEvent.VK_ENTER) {
            addMessage("Enter pressed at " + System.currentTimeMillis() + " and then some longlonglonglonglonglonglonglonglonglonglonglonglonglong");
        }
        return this;
    }

    @Override
    public void addLocatedMessage(String message, int x, int y, int radius){
        if(players.get(playerId)==null){
            return;
        }
        if((players.get(playerId).getX()-x)*(players.get(playerId).getX()-x)+
                (players.get(playerId).getY()-y)*(players.get(playerId).getY()-y) < radius*radius){
            addMessage(message);
        }
    }

    @Override
    public void addMessage(String message){
        String s;
        List<String> tmpMsg = new ArrayList<>();
        do {
            s = message.substring(0,Math.min(totalWidth,message.length()));
            if(s.length()==0) break;
            tmpMsg.add(s);
            message = message.replaceFirst(".{0,"+totalWidth+"}","");
        } while(s.length()>0);
        tmpMsg.forEach(msg -> messages.add(0,msg));
    }

    private void displayTiles(AsciiPanel terminal, int left, int top) {
        for (int x = 0; x < viewWidth; x++){
            for (int y = 0; y < viewHeight; y++){
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
            if (player.getHealth() <= 0) {
                continue;
            }
            int wx = player.getX() - left;
            int wy = player.getY() - top;
            if (wx >= viewWidth || wx < 0 || wy >= viewHeight || wy < 0) continue;
            terminal.write(player.glyph, wx, wy, player.color);
        }
    }

    private void displayUnits(AsciiPanel terminal, int left, int top) {
        for (Unit unit : units ){
            int wx = unit.getX()-left;
            int wy = unit.getY()-top;
            if(wx>= viewWidth || wx<0 ||wy>= viewHeight || wy<0) continue;
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
        if(diff.getMessage()!=null){
            addLocatedMessage(diff.getMessage(),diff.getX(),diff.getY(),diff.getR());
        }
        if(diff.getTile()!=null){
            updateTile(diff.getTile(),diff.getX(),diff.getY());
        }
    }

    private void updateTile(Tile tile, int x, int y){
        view[x][y] = new AsciiSymbol(tile.glyph(),tile.color());
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
        if(diffPlayer.getOldconnectionId()!=diffPlayer.getId()){
            System.out.println("old and new differ "+diffPlayer.getOldconnectionId()+" "+diffPlayer.getId());
            players.remove(diffPlayer.getOldconnectionId());
            System.out.println("old and new differ "+diffPlayer.getOldconnectionId()+" "+diffPlayer.getId());
            if(diffPlayer.getOldconnectionId()==playerId){
                System.out.println("im "+playerId);
                playerId = diffPlayer.getId();
                System.out.println("im "+playerId);
            }
        }
        if (!diffPlayer.isConnected()) {
            players.remove(diffPlayer.getId());
            return;
        }
        players.put(diffPlayer.getId(), diffPlayer);

        if(players.containsKey(playerId)) {

            viewX = players.get(playerId).getX() - viewWidth / 2;
            viewY = players.get(playerId).getY() - viewHeight / 2;
        } else {
            System.out.println("I am not found in players list! "+playerId);
        }
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
}
