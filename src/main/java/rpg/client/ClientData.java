package rpg.client;


import java.io.Serializable;

public class ClientData implements Serializable {

    private final int id;
    private int[] keycodes = new int[10];

    public ClientData(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int[] getKeycodes() {
        return keycodes;
    }

    public void addKeycodes(int[] keycodes) {
        this.keycodes = keycodes;
    }

}
