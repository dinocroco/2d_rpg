import asciiPanel.AsciiPanel;
import javax.swing.JFrame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;

import screen.*;

public class Application extends JFrame implements KeyListener {
    private AsciiPanel terminal;
    private Screen screen;

    Application(){
        super();
        terminal = new AsciiPanel();
        add(terminal);
        pack();
        screen = new StartScreen();
        addKeyListener(this);
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println(System.nanoTime());
        if(e.isControlDown() && e.getKeyCode()==KeyEvent.VK_C){
            // ctrl+c for exit
            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        }
        screen = screen.respondToUserInput(e);
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void repaint(){
        terminal.clear();
        screen.displayOutput(terminal);
        super.repaint();
    }
}
