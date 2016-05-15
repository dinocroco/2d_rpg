package rpg.character;

import asciiPanel.AsciiPanel;

import java.awt.*;
import java.io.Serializable;

public class Player implements Serializable, GameCharacter {
    private String name;
    private String password;
    private int x;
    private int y;
    public final char glyph = (char)254;
    public final Color color;
    private int connectionId;
    private int oldconnectionId;
    private boolean hasChanged;
    private boolean connected = true;
    private int health = 100;
    private int maxhealth = 100;
    private int damage = 10;
    private int deltaX;
    private int deltaY;
    private boolean active = true;
    private long backToActive;
    private int attackSpeed = 5;
    private long lastAttackTime = 0;
    private int freezeAbility = 5;
    private int maxFreezeAbility = 4;
    private double healingSpeed = 1;
    private boolean leveled = false;
    private int level = 1;
    private double xp = 0;

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

    public int getFreezeAbility() {
        return freezeAbility;
    }

    public void addFreezeAbility(int freezeAbility) {
        this.freezeAbility += freezeAbility;
    }

    public long getLastAttackTime() {
        return lastAttackTime;
    }

    public void setLastAttackTime(long lastAttackTime) {
        this.lastAttackTime = lastAttackTime;
    }

    public int getAttackSpeed() {
        return attackSpeed;
    }

    public void setAttackSpeed(int attackSpeed) {
        this.attackSpeed = attackSpeed;
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

    public int getMaxFreezeAbility() {
        return maxFreezeAbility;
    }

    public double getHealingSpeed() {
        return healingSpeed;
    }

    public void setHealingSpeed(int healingSpeed) {
        this.healingSpeed = healingSpeed;
        hasChanged = true;
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

    public String toMessage(){
        return name + "(" + getId() + ')';
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

    @Override
    public int getMaxhealth() {
        return maxhealth;
    }

    public void setMaxhealth(int maxhealth) {
        this.maxhealth = maxhealth;
        hasChanged = true;
    }

    public void setConnectionId(int connectionId) {
        // this may ONLY be called when player is not in any world or screen
        oldconnectionId = this.connectionId;
        this.connectionId = connectionId;
        hasChanged = true;
    }

    public int getOldconnectionId() {
        return oldconnectionId;
    }

    public void resetOldconnectionId() {
        this.oldconnectionId = connectionId;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
        hasChanged = true;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        hasChanged = true;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        hasChanged = true;
    }

    public void receiveKill(GameCharacter target){
        xp+=target.getLevel()*1000;
        if(xp>level*level*1000){
            levelUp();
        }
    }

    public void levelUp(){
        level++;
        damage++;
        maxFreezeAbility++;
        freezeAbility=maxFreezeAbility;
        healingSpeed+=0.1;
        maxhealth+=10;
        health=maxhealth;
        leveled(true);
    }

    public boolean leveled() {
        return leveled;
    }

    public void leveled(boolean leveled) {
        this.leveled = leveled;
    }

    public int getLevel() {
        return level;
    }
}
