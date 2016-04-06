import action.GameAction;
import player.Player;

import java.util.ArrayList;
import java.util.List;

public class Tick implements Runnable {

    long lastMoment = System.nanoTime();
    long tickLength = 500000000;
    boolean playing = true;
    double unprocessed = 0.0;
    Application app;
    List<List<GameAction>> gameActions = new ArrayList<>();
    List<Player> players;

    public Tick(Application app) {
        this.app = app;
        //initTick();
    }

    public void run(){
        while (playing) {
            long thisMoment = System.nanoTime();
            System.out.println("running run loop");
            System.out.println(thisMoment);
            //System.out.println(thisMoment-lastMoment);
            unprocessed += (thisMoment - lastMoment);
            lastMoment = thisMoment;
            boolean shouldRender = false;

            while (unprocessed/tickLength >= 1) {
                tick();
                unprocessed -= tickLength;
                shouldRender = true;
            }

            if (shouldRender) {
                app.repaint();
                System.out.println("rendering at "+System.currentTimeMillis());
            } else {
                try {
                    Thread.sleep((int)((tickLength-unprocessed)/1000000));
                } catch (InterruptedException e){
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void tick(){
        System.out.println("performing tick "+System.currentTimeMillis());
        if (gameActions.size() <= 0) return;
        for (GameAction gameAction : gameActions.get(0)) {
            gameAction.run();
        }
        gameActions.remove(0);
        if (gameActions.size() <= 0) {
            gameActions.add(new ArrayList<>());
        }
        for (Player player : players) {
            gameActions.add(player.getActions());

        }

    }

}


