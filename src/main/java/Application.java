import asciiPanel.AsciiPanel;
import screen.Screen;
import screen.StartScreen;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;

public class Application extends JFrame implements KeyListener {
    private AsciiPanel terminal;
    private Screen screen;
    private Tick tick;

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

    @Override
    public void repaint(){
        terminal.clear();
        screen.displayOutput(terminal);
        super.repaint();
    }
}
