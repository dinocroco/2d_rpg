package rpg;

import asciiPanel.AsciiPanel;
import rpg.action.FreezeUnit;
import rpg.action.GameAction;
import rpg.action.Movement;
import rpg.character.Player;
import rpg.character.Unit;
import rpg.client.ClientData;
import rpg.screen.*;
import rpg.server.Server;
import rpg.world.Diff;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Application extends JFrame implements KeyListener {
    private final int screenWidth = 80;
    private final int screenHeight = 24;
    private AsciiPanel terminal;
    private Screen screen;
    private Tick tick;
    private Server server;
    private boolean sentInitialView = false;
    private List<GameAction> gameActions = new ArrayList<>();

    /**
     * Application constructor for Server, includes game time ticking.
     */

    Application(){
        super();
        setResizable(false);
        terminal = new AsciiPanel(screenWidth,screenHeight);
        add(terminal);

        screen = new StartScreen(new HashMap<>());
        addKeyListener(this);

        repaint();
        pack();
        tick = new Tick(this);
        Thread ticking = new Thread(tick);
        ticking.start();
        server = startServer();
    }

    /**
     * Application constructor for Client
     */

    public Application(int type){
        super();
        setResizable(false);
        terminal = new AsciiPanel(screenWidth,screenHeight);
        add(terminal);
        screen = new ClientScreen();
        addKeyListener(this);
        repaint();
        pack();
    }

    public void keyTyped(KeyEvent e) {

    }

    public void keyPressed(KeyEvent e) {
        if(e.isControlDown() && e.getKeyCode()==KeyEvent.VK_C){
            close();
        }
        screen = screen.respondToUserInput(e);
        if(screen == null){
            close();
        }
    }

    private void close() {
        if(server!=null) {
            server.shutDown();
        }
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    public void keyReleased(KeyEvent e) {

    }

    public synchronized void newConnection(int clientIndex){
        Random rand = new Random();
        Color color = new Color(rand.nextInt(0xFFFFFF));
        Player player = new Player(clientIndex,color);
        if(screen.getClass() == PlayScreen.class){
            Diff startingPoint = screen.getWorld().playerStartingPoint();
            if (startingPoint!=null) {
                player.setX(startingPoint.getX());
                player.setY(startingPoint.getY());
            }

            screen.sendWorldTerrain(server);
        }

        if(screen.getWorld()!=null) {
            screen.getWorld().getPlayers().put(clientIndex, player);
        } else {
            screen.addPlayer(clientIndex, player);
        }
        System.out.println("client "+Integer.toString(clientIndex)+" connected");
    }

    public synchronized void onDisconnect(int clientIndex){
        screen.getWorld().removePlayer(clientIndex);
        System.out.println("Kicked client "+clientIndex);
    }

    public synchronized void executeKeyCode(ClientData clientdata){
        if(screen.getClass() == PlayScreen.class) {
            for (int i : clientdata.getKeycodes()) {
                int id = clientdata.getId();
                if (i == KeyEvent.VK_Z){
                    Player player = screen.getWorld().getPlayers().get(id);
                    Unit nearestUnit = screen.getWorld().getNearestUnit(id);
                    if (nearestUnit != null && screen.getWorld().distanceBetween(nearestUnit,player) < 4){
                        addGameActions(new FreezeUnit(id,nearestUnit,15));
                    }
                }
                if (i == KeyEvent.VK_RIGHT) {
                    addGameActions(new Movement(id,1,0));
                }
                if (i == KeyEvent.VK_LEFT) {
                    addGameActions(new Movement(id,-1,0));
                }
                if (i == KeyEvent.VK_UP) {
                    addGameActions(new Movement(id,0,-1));
                }
                if (i == KeyEvent.VK_DOWN) {
                    addGameActions(new Movement(id,0,1));
                }
                if (i == KeyEvent.VK_ESCAPE) {
                    for (int j = 0; j < gameActions.size(); j++) {
                        if (gameActions.get(j).characterID == clientdata.getId()){
                            gameActions.remove(j);
                            j--;
                        }

                    }
                }
            }
        }
    }

    public synchronized void addGameActions(GameAction actionToAdd){
        int count = 0;
        for (GameAction gameaction : gameActions) {
            if (gameaction.characterID == actionToAdd.characterID){
                count++;
            }
            if (count > 3){
                return;
            }
        }
        gameActions.add(actionToAdd);

    }

    public synchronized void executeGameEvents(long tickspassed){
        if(screen.getClass()==PlayScreen.class) {
            List<GameAction> toRemove = new ArrayList<>();
            List<Long> idCodes = new ArrayList<>();
            for (GameAction gameaction : gameActions) {
                long id = gameaction.characterID;
                if (idCodes.contains(id)) {
                    continue;
                }
                if (gameaction instanceof Movement) {
                    Movement moveaction = (Movement) gameaction;
                    if (gameaction.characterID < 1000) {
                        Player player = screen.getWorld().getPlayers().get((int) gameaction.characterID);
                        if(screen.getWorld().vacantXY(player.getX()+moveaction.right,player.getY()+moveaction.down)){
                            player.addToXY(moveaction.right, moveaction.down);
                        }
                        idCodes.add(id);
                        toRemove.add(gameaction);
                    }
                }
                if (gameaction instanceof FreezeUnit){
                    FreezeUnit freezeunit = (FreezeUnit) gameaction;
                    if (gameaction.characterID < 1000) {
                        freezeunit.unit.freeze(freezeunit.time, tickspassed);
                        idCodes.add(id);
                        toRemove.add(gameaction);
                    }

                }

            }
            for (GameAction gameAction : toRemove) {
                gameActions.remove(gameAction);
            }

            screen.getWorld().moveUnits(tickspassed);
        }
        repaint();
    }

    @Override
    public void repaint(){
        terminal.clear();
        if(screen.getClass() == PlayScreen.class) {
            if (!sentInitialView) {
                screen.sendWorldTerrain(server);
                sentInitialView = true;
            }
            List<Diff> diff = screen.updateDiff();
            if (diff == null) {
                diff = new ArrayList<>();
            }
            for (int key : screen.getWorld().getPlayers().keySet()) {
                Player player = screen.getWorld().getPlayers().get(key);
                if (player.hasChanged()) {
                    diff.add(new Diff(player));
                    player.toUnchanged();
                }
            }
            for (Unit unit : screen.getWorld().getUnits()) {
                if (unit.hasChanged()) {
                    diff.add(new Diff(unit));
                    unit.toUnchanged();
                }
            }
            if (!diff.isEmpty()) {
                for (Diff d : diff) {
                    screen.sendDiff(server, d);
                }
                diff.clear();
            }
        }

        screen.displayOutput(terminal);
        if (screen.getClass() == PlayScreen.class) {
            screen.displayUnits(terminal, screen.getWorld().getUnits());
            screen.displayPlayers(terminal, screen.getWorld().getPlayers());
        }
        super.repaint();
    }

    public Server startServer(){
        return new Server(1336,this);
    }

    public Screen getScreen() {
        return screen;
    }

    public AsciiPanel getTerminal() {
        return terminal;
    }

    public synchronized void addNewUnit(long tickNr){
        Unit unit = new Unit(tickNr);
        if(screen.getClass() == PlayScreen.class) {
            if (screen.getWorld().getUnits().size() < 3 ) {
                Diff startingPoint = screen.getWorld().unitStartingPoint();
                if (startingPoint != null) {
                    unit.setX(startingPoint.getX());
                    unit.setY(startingPoint.getY());
                }
                screen.sendWorldTerrain(server);
                screen.getWorld().getUnits().add(unit);
            }
        }

    }

    public void setDisconnectScreen(){
        screen = new DisconnectScreen();
    }
}
