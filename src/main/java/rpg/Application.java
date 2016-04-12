package rpg;

import asciiPanel.AsciiPanel;
import rpg.screen.ClientScreen;
import rpg.screen.PlayScreen;
import rpg.screen.Screen;
import rpg.screen.StartScreen;
import rpg.server.Server;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.util.Arrays;

public class Application extends JFrame implements KeyListener {
    private AsciiPanel terminal;
    private Screen screen;
    private Tick tick;
    public Server server;
    private boolean sentInitialView = false;

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

    public void newConnection(){
        if(screen.getClass() == PlayScreen.class){

            screen.sendOutput(server);
        }
    }

    public void executeKeyCode(int[] keycodes){
        System.out.println("keycodes received:" + Arrays.toString(keycodes));

    }
    @Override
    public void repaint(){
        //rpg.server sends new data
        //screen.sendOutput(server);
        if(!sentInitialView && screen.getClass() == PlayScreen.class){
            screen.sendOutput(server);
            sentInitialView = true;
        }
        screen.sendDiff(server);
        terminal.clear();
        screen.displayOutput(terminal);
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
