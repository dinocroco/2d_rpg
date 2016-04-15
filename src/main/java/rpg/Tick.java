package rpg;

import rpg.action.GameAction;

import java.util.TreeSet;

public class Tick implements Runnable {

    private long lastMoment = System.nanoTime();
    private long tickLength = 500000000;
    private boolean playing = true;
    private double unprocessed = 0.0;
    private Application app;
    private TreeSet<GameAction> gameActions = new TreeSet<>();
    private long ticksPassed = 0;

    public Tick(Application app) {
        this.app = app;
        //initTick();
    }

    public void run(){
        while (playing) {
            long thisMoment = System.nanoTime();
            //System.out.println("running run loop");
            //System.out.println(thisMoment);
            //System.out.println(thisMoment-lastMoment);
            unprocessed += (thisMoment - lastMoment);
            lastMoment = thisMoment;
            boolean shouldRender = false;

            while (unprocessed/tickLength >= 1) {
                tick();
                ticksPassed++;
                unprocessed -= tickLength;
                shouldRender = true;
            }

            if (shouldRender) {
                app.repaint();
                //System.out.println("rendering at "+System.currentTimeMillis());
                // TODO do something about sending
                //app.getScreen().sendOutput(app.server);

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
        //System.out.println("performing tick "+System.currentTimeMillis());

        if(ticksPassed%12==0){
            app.addNewUnit(ticksPassed);
        }
    }

}


