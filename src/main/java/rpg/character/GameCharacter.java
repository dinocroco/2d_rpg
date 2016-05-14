package rpg.character;

public interface GameCharacter {

    boolean hasChanged();

    void toUnchanged();

    int getX();

    int getY();

    void setX(int x);

    void setY(int y);

    long getID();

    void addHealth(int value);

    int getHealth();

    int getDamage();

    String getName();

    String toMessage();
}
