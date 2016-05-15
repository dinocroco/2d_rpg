package rpg;

import asciiPanel.AsciiPanel;
import rpg.action.*;
import rpg.character.GameCharacter;
import rpg.character.Player;
import rpg.character.Unit;
import rpg.client.ClientData;
import rpg.client.KeyEventWrapper;
import rpg.client.PlayerData;
import rpg.screen.*;
import rpg.server.Server;
import rpg.world.Diff;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

public class Application extends JFrame implements KeyListener {

    public static final int PORT = 1336;

    private final int screenWidth = 80;
    private final int screenHeight = 30;
    private AsciiPanel terminal;
    private Screen screen;
    private Tick tick;
    private Server server;
    private boolean sentInitialView = false;
    private List<GameAction> gameActions = new ArrayList<>();
    private Map<String,Player> passwordPlayer = new HashMap<>();

    /**
     * Application constructor for Server, includes game time ticking.
     */

    public Application(){
        super();
        setResizable(false);
        terminal = new AsciiPanel(screenWidth,screenHeight);
        add(terminal);

        addKeyListener(this);

        pack();
        tick = new Tick(this);
        Thread ticking = new Thread(tick);
        ticking.start();
        server = startServer();
        screen = new PlayScreen(new HashMap<>());
        readPasswordPlayers();
        repaint();
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

    private void close(){
        if(passwordPlayer.size()>0){
            writePasswordPlayers();
        }
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        if(server!=null) {
            try {
                server.shutDown();
            } catch (IOException e){
                throw new RuntimeException(e); //no point in throwing IOE again
            }
        }
    }

    public void keyReleased(KeyEvent e) {

    }

    public synchronized void newConnection(PlayerData playerData){
        // search if that player exists already
        Player player;
        if(passwordPlayer.containsKey(playerData.playername+"/"+playerData.password)) {
            int id = -1;
            // kick previous one if any
            for (Player p : screen.getWorld().getPlayers().values()) {
                if((p.getName()+"/"+p.getPassword()).equals(playerData.playername+"/"+playerData.password)){
                    id = p.getId();
                    break;
                }
            }
            if(id>=0) {
                server.kick(id);
            }
            player = passwordPlayer.get(playerData.playername+"/"+playerData.password);
            player.setConnectionId(playerData.idCode);
            player.setConnected(true);
            player.setLastAttackTime(0);
            //System.out.println("player rejoined");
            // maybe make sure the location is free, but not that important
            // TODO it should use old ticks amount not just resetting it
        } else {
            Random rand = new Random();
            Color color = new Color(rand.nextInt(0xFFFFFF));
            player = new Player(playerData.idCode, color);

            if (screen.getClass() == PlayScreen.class) {
                Diff startingPoint = screen.getWorld().startingPoint();
                if (startingPoint != null) {
                    player.setX(startingPoint.getX());
                    player.setY(startingPoint.getY());
                }
            }
            player.setName(playerData.playername);
            player.setPassword(playerData.password);
            passwordPlayer.put(playerData.playername+"/"+playerData.password,player);
            // saves everyone who connects
        }
        if (screen.getClass() == PlayScreen.class) {
            screen.sendWorldTerrain(server);
        }

        if(screen.getWorld()!=null) {
            screen.getWorld().getPlayers().put(playerData.idCode, player);
        } else {
            screen.addPlayer(playerData.idCode, player);
        }
        System.out.println("client "+Integer.toString(playerData.idCode)+" connected");
    }

    public synchronized void onDisconnect(int clientIndex){
        screen.getWorld().removePlayer(clientIndex);
        System.out.println("Kicked client "+clientIndex);
    }

    public synchronized void executeKeyCode(ClientData clientdata){
        if(screen.getClass() == PlayScreen.class) {
            for (KeyEventWrapper e : clientdata.getKeyEvents()) {
                int i = e.getKeyCode();
                int id = clientdata.getId();
                Player player = screen.getWorld().getPlayers().get(id);
                if(!player.isActive()){
                    return;
                }
                if (i == KeyEvent.VK_SPACE){
                    GameCharacter[] targets = new GameCharacter[4];
                    targets[0] = screen.getWorld().getGameCharacter(player.getX(), player.getY()+1);
                    targets[1] = screen.getWorld().getGameCharacter(player.getX()+1, player.getY());
                    targets[2] = screen.getWorld().getGameCharacter(player.getX(), player.getY()-1);
                    targets[3] = screen.getWorld().getGameCharacter(player.getX()-1, player.getY());
                    addGameActions(new Attack(targets,player,player.getDamage()));
                }
                if (i == KeyEvent.VK_Z){
                    Unit nearestUnit = screen.getWorld().getNearestUnit(id);
                    if (nearestUnit != null && screen.getWorld().distanceBetween(nearestUnit,player) < 4){
                        addGameActions(new FreezeUnit(player,nearestUnit,15));
                    }
                }
                if (!e.isShiftDown()) {
                    if (i == KeyEvent.VK_RIGHT) {
                        addGameActions(new Movement(player, 1, 0, false));
                    }
                    if (i == KeyEvent.VK_LEFT) {
                        addGameActions(new Movement(player, -1, 0, false));
                    }
                    if (i == KeyEvent.VK_UP) {
                        addGameActions(new Movement(player, 0, -1, false));
                    }
                    if (i == KeyEvent.VK_DOWN) {
                        addGameActions(new Movement(player, 0, 1, false));
                    }
                } else {
                    if (i == KeyEvent.VK_RIGHT) {
                        addGameActions(new Movement(player, 1, 0, true));
                    }
                    if (i == KeyEvent.VK_LEFT) {
                        addGameActions(new Movement(player, -1, 0, true));
                    }
                    if (i == KeyEvent.VK_UP) {
                        addGameActions(new Movement(player, 0, -1, true));
                    }
                    if (i == KeyEvent.VK_DOWN) {
                        addGameActions(new Movement(player, 0, 1, true));
                    }

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
            if (count > 1){
                return;
            }
        }
        gameActions.add(actionToAdd);

    }

    public synchronized void executeGameEvents(long tickspassed){
        if(screen.getClass()==PlayScreen.class) {
            List<GameAction> toRemove = new ArrayList<>();
            List<Long> idCodes = new ArrayList<>();
            screen.getWorld().recoverPoints(tickspassed);
            for (GameAction gameaction : gameActions) {
                long id = gameaction.characterID;
                if (idCodes.contains(id)) {
                    continue;
                }
                if (screen.getWorld().getPlayers().get((int)id)!=null && screen.getWorld().getPlayers().get((int)id).isActive()){
                    gameaction.executeAction(screen,tickspassed);
                    screen.getWorld().handleDeadPlayers(tickspassed);
                    idCodes.add(id);
                }
                toRemove.add(gameaction);
            }
            for (GameAction gameAction : toRemove) {
                gameActions.remove(gameAction);
            }
            screen.getWorld().moveUnits(tickspassed);
            screen.getWorld().handleDeadPlayers(tickspassed);
            screen.getWorld().handleDeadUnits(tickspassed);
        }
        repaint();
    }

    @Override
    public void repaint(){
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
                    player.resetOldconnectionId();
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
        return new Server(PORT,this);
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
            if (screen.getWorld().getUnits().size() < 10 ) {
                Diff startingPoint = screen.getWorld().startingPoint();
                if (startingPoint != null) {
                    unit.setX(startingPoint.getX());
                    unit.setY(startingPoint.getY());
                }
                screen.sendWorldTerrain(server);
                screen.getWorld().getUnits().add(unit);
            }
        }
    }

    public void readPasswordPlayers(){
        try(ObjectInputStream dis = new ObjectInputStream(new FileInputStream("players.dat"))){
            while(true) {
                try {
                    Player player = (Player) dis.readObject();
                    passwordPlayer.put(player.getName()+"/"+player.getPassword(),player);
                } catch (EOFException e) {
                    // fail sai otsa
                    break;
                } catch (InvalidClassException e){
                    // old format file
                    dis.close();
                    Files.delete(Paths.get("players.dat"));
                    break;
                } catch (ClassNotFoundException e) {
                    // mingi suurem jama
                    throw new RuntimeException(e);
                }
            }

        } catch(IOException e) {
            e.printStackTrace();
            System.out.println("Reading passwords failed.");
            passwordPlayer = new HashMap<>();
        }
    }

    public void writePasswordPlayers(){
        try(ObjectOutputStream dos = new ObjectOutputStream(new FileOutputStream("players.dat"))){
            for (Player player : passwordPlayer.values()) {
                dos.writeObject(player);
            }
        } catch (IOException e){
            System.out.println("Saving passwords failed.");
        }
    }

    public void setDisconnectScreen(){
        screen = new DisconnectScreen();
    }
}
