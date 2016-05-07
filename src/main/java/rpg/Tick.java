package rpg;

public class Tick implements Runnable {

    public static final long TICK_LENGTH = 200000000;

    private long lastMoment = System.nanoTime();
    private boolean playing = true;
    private double unprocessed = 0.0;
    private Application app;
    private long ticksPassed = 0;

    public Tick(Application app) {
        this.app = app;
    }

    public void run(){
        while (playing) {
            long thisMoment = System.nanoTime();
            unprocessed += (thisMoment - lastMoment);
            lastMoment = thisMoment;
            boolean shouldRender = false;

            while (unprocessed/ TICK_LENGTH >= 1) {
                tick();
                ticksPassed++;
                unprocessed -= TICK_LENGTH;
                shouldRender = true;
            }

            if (shouldRender) {
                app.repaint();
            } else {
                try {
                    Thread.sleep((int)((TICK_LENGTH -unprocessed)/1000000));
                } catch (InterruptedException e){
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void tick(){
        if(ticksPassed%120==0){
            app.addNewUnit(ticksPassed);
        }
        app.executeGameEvents(ticksPassed);
    }

}


