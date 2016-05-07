package rpg.client;

import java.io.Serializable;

public class ClientData implements Serializable {

    private final int id;
    private KeyEventWrapper[] keyEvents = new KeyEventWrapper[10];

    public ClientData(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public KeyEventWrapper[] getKeyEvents() {
        return keyEvents;
    }

    public void addKeyEvents(KeyEventWrapper[] keyevent) {
        this.keyEvents = keyevent;
    }

}