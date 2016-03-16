/**
 * Created by mrrobot on 16.03.16.
 */
public class Tick {

    long lastMoment = System.nanoTime();
    long tickLength = 500000000;
    boolean playing = true;
    double unprocessed = 0.0;

    public Tick() {

        while (playing) {
            long thisMoment = System.nanoTime();
            //System.out.println(thisMoment-lastMoment);
            unprocessed += (thisMoment - lastMoment);
            lastMoment = thisMoment;
            boolean shouldRender = false;

            while (unprocessed/tickLength >= 1) {
                //tick();
                unprocessed -= tickLength;
                shouldRender = true;
            }

            if (shouldRender) {
                //application.repaint();
                System.out.println("rendering at "+System.currentTimeMillis());
            }
        }
    }

}


