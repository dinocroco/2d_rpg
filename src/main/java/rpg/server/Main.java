package rpg.server;

import rpg.Application;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        Application app = new Application();
        app.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //app.setVisible(false); use false to hide it
        app.setVisible(true);

    }
}
