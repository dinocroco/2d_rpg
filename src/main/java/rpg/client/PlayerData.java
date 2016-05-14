package rpg.client;

import java.io.Serializable;

public class PlayerData implements Serializable{

    public final int idCode;
    public final String playername;
    public final String password;

    public PlayerData(int idCode, String playername, String password) {
        this.idCode = idCode;
        this.playername = playername;
        this.password = password;
    }
}
