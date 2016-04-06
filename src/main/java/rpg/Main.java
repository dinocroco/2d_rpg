package rpg;

import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        System.out.println("this is main class");



        Application app = new Application();
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        app.setVisible(true);
    }
}
