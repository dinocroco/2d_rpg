package rpg.client;

import java.awt.*;
import java.io.Serializable;

public class KeyEventWrapper implements Serializable {

    private int keycode;
    private boolean shiftModifier;
    private boolean ctrlModifier;
    private boolean altModifier;

    public KeyEventWrapper(int keycode, boolean shiftModifier, boolean altModifier, boolean ctrlModifier) {
        this.keycode = keycode;
        this.shiftModifier = shiftModifier;
        this.altModifier = altModifier;
        this.ctrlModifier = ctrlModifier;
    }

    public int getKeyCode() {
        return keycode;
    }

    public boolean isShiftDown() {
        return shiftModifier;
    }

    public boolean isControlDown() {
        return ctrlModifier;
    }

    public boolean isAltDown() {
        return altModifier;
    }

}
