package rpg;

import asciiPanel.AsciiPanel;
import rpg.client.ClientData;
import rpg.player.Player;
import rpg.screen.ClientScreen;
import rpg.screen.PlayScreen;
import rpg.screen.Screen;
import rpg.screen.StartScreen;
import rpg.server.Server;
import rpg.world.Diff;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.util.*;
import java.util.List;

public class Application extends JFrame implements KeyListener {
    private AsciiPanel terminal;
    private Screen screen;
    private Tick tick;
    public Server server;
    private boolean sentInitialView = false;
    private Map<Integer,Player> players = new HashMap<>();

    Application(){
        super();
        terminal = new AsciiPanel();
        add(terminal);

        screen = new StartScreen();
        addKeyListener(this);

        repaint();
        pack();
        tick = new Tick(this);
        Thread ticking = new Thread(tick);
        ticking.start();
        server = startServer();
    }

    public Application(int type){
        super();
        terminal = new AsciiPanel();
        add(terminal);
        screen = new ClientScreen();
        addKeyListener(this);
        repaint();
        pack();
    }

    //@Override
    public void keyTyped(KeyEvent e) {

    }

    //@Override
    public void keyPressed(KeyEvent e) {
        if(e.isControlDown() && e.getKeyCode()==KeyEvent.VK_C){
            // ctrl+c for exit
            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        }
        screen = screen.respondToUserInput(e);
        // instead, ticking is responsible for redraw
        //repaint();
    }

    //@Override
    public void keyReleased(KeyEvent e) {

    }

    public void newConnection(int clientIndex){
        Random rand = new Random();
        Color color = new Color(rand.nextInt(0xFFFFFF));
        Player player = new Player(clientIndex,color);
        // TODO find some place for it, add color
        player.setX(1);
        player.setY(1);
        players.put(clientIndex,player);
        System.out.println("client "+Integer.toString(clientIndex)+" connected");
        if(screen.getClass() == PlayScreen.class){
            screen.sendOutput(server);
        }
    }

    public void executeKeyCode(ClientData clientdata){
        // TODO see peab vaid lisama tegevusi järjekorda, ning tick peaks kontrollima, millal järjekorrast järgmine võetakse
        System.out.println("keycodes received:" + Arrays.toString(clientdata.getKeycodes())+" client id:" + clientdata.getId());
        if(screen.getClass() == PlayScreen.class) {
            for (int i : clientdata.getKeycodes()) {
                // TODO enne liikumist peab kontrollima kas ka võib, pole takistust, maailma lõppu, muud sellist
                if (i == KeyEvent.VK_RIGHT) {
                    players.get(clientdata.getId()).setX(players.get(clientdata.getId()).getX()+1);
                }
                if (i == KeyEvent.VK_LEFT) {
                    players.get(clientdata.getId()).setX(players.get(clientdata.getId()).getX()-1);
                }
                if (i == KeyEvent.VK_UP) {
                    players.get(clientdata.getId()).setY(players.get(clientdata.getId()).getY()-1);
                }
                if (i == KeyEvent.VK_DOWN) {
                    players.get(clientdata.getId()).setY(players.get(clientdata.getId()).getY()+1);
                }
            }
        }
    }
    @Override
    public void repaint(){
        //rpg.server sends new data
        //screen.sendOutput(server);
        if(!sentInitialView && screen.getClass() == PlayScreen.class){
            screen.sendOutput(server);
            sentInitialView = true;
        }
        List<Diff> diff = screen.updateDiff(server);
        if(diff == null){
            diff = new ArrayList<>();
        }
        for (int key : players.keySet()) {
            Player player = players.get(key);
            if (player.hasChanged()) {
                System.out.println(player);
                diff.add(new Diff(player));
                player.toUnchanged();
            }
        }
        if(!diff.isEmpty()) {
            for (Diff d : diff) {
                screen.sendDiff(server, d);
            }
            diff.clear();
        }
        terminal.clear();
        screen.displayOutput(terminal);
        if(screen.getClass()==PlayScreen.class){
            ((PlayScreen)screen).displayPlayers(terminal,players);
        }
        super.repaint();
    }

    public Server startServer(){
        return new Server(1336,this);
    }

    public Screen getScreen() {
        //System.out.println("returning screen");
        return screen;
    }

    public AsciiPanel getTerminal() {
        return terminal;
    }

    public void resetView(){
        sentInitialView = false;
    }
}
