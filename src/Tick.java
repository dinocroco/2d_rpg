/**
 * Created by mrrobot on 16.03.16.
 */
public class Tick {

    long lastMoment = System.nanoTime();
    long tickLength = 500000000L;
    boolean playing = true;
    double unprocessed = 0;

    public Tick() {

        while (playing) {
            long thisMoment = System.nanoTime();
            unprocessed += (thisMoment - lastMoment) / tickLength;
            lastMoment = thisMoment;
            boolean shouldRender = true;

            while (unprocessed >= 1) {
                //tick();
                unprocessed -= 1;
                shouldRender = true;
            }

            if (shouldRender) {
                //application.repaint();
            }
        }
    }

}


