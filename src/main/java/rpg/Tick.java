package rpg;

public class Tick implements Runnable {

    private long lastMoment = System.nanoTime();
    private long tickLength = 200000000;
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

            while (unprocessed/tickLength >= 1) {
                tick();
                ticksPassed++;
                unprocessed -= tickLength;
                shouldRender = true;
            }

            if (shouldRender) {
                app.repaint();
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
        if(ticksPassed%120==0){
            app.addNewUnit(ticksPassed);
        }
        app.executeGameEvents();
    }

}


