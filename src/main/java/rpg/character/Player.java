package rpg.character;

import asciiPanel.AsciiPanel;

import java.awt.*;
import java.io.Serializable;

public class Player implements Serializable, GameCharacter {

    private int x;
    private int y;
    public final char glyph = (char)254;
    public final Color color;
    public final int connectionId;
    private boolean hasChanged;
    private int health = 100;
    private int maxhealth = 100;
    private int damage = 10;
    private int deltaX;
    private int deltaY;
    private boolean active = true;
    private long backToActive;

    // TODO eventually load character info from somewhere instead of creating new for each connect
    public Player(int id) {
        connectionId = id;
        color = AsciiPanel.cyan;
        hasChanged = true;
    }

    public Player(int id, Color color) {
        connectionId = id;
        this.color = color;
        hasChanged = true;
    }

    public int getDeltaX() {
        return deltaX;
    }

    public int getDeltaY() {
        return deltaY;
    }

    @Override
    public int getDamage() {
        return damage;
    }

    @Override
    public void addHealth(int value) {
        health += value;
        health = Math.min(health,maxhealth);
        hasChanged = true;
    }

    @Override
    public int getHealth() {
        return health;
    }

    @Override
    public long getID() {
        return (long) connectionId;
    }

    public int getId() {
        return connectionId;
    }

    @Override
    public void setX(int x) {
        this.x = x;
        hasChanged = true;
    }

    @Override
    public void setY(int y) {
        this.y = y;
        hasChanged = true;
    }

    public void setActive(boolean inGame) {
        this.active = inGame;
        hasChanged=true;
    }

    public boolean isActive(){
        return active;
    }

    @Override
    public boolean hasChanged() {
        return hasChanged;
    }

    @Override
    public void toUnchanged() {
        this.hasChanged = false;
    }

    @Override
    public String toString() {
        return "Player{" +
                "x=" + x +
                ", y=" + y +
                ", glyph=" + glyph +
                ", color=" + color +
                ", connectionId=" + connectionId +
                '}';
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    private void addToX(int x){
        hasChanged = true;
        this.x+=x;
    }

    private void addToY(int y){
        hasChanged = true;
        this.y+=y;
    }

    public void addToXY(int x, int y){
        hasChanged = true;
        deltaX = x;
        deltaY = y;
        addToX(x);
        addToY(y);
    }

    public long getBackToActive() {
        return backToActive;
    }

    public void setBackToActive(long backToActive) {
        this.backToActive = backToActive;
        hasChanged = true;
    }

    public int getMaxhealth() {
        return maxhealth;
    }

    public void setMaxhealth(int maxhealth) {
        this.maxhealth = maxhealth;
    }
}
