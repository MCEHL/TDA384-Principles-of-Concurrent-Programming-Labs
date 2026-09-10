import TSim.*;

import java.awt.*;
import java.util.concurrent.Semaphore;

public class TrainBehaveiour implements Runnable {
    private int trainId = -1;
    private int speed = 0;
    private int[] position = {0,0};
    private TSimInterface tsim;
    private Semaphore semA;
    private Semaphore semB;
    private Semaphore semC;

    private boolean inCritical = false;

    // Switches
    private final Point SWITCH_B_NORTH = new Point(17, 7);
    private final Point SWITCH_B_SOUTH = new Point(15, 9);
    private final Point SWITCH_C_NORTH = new Point(4, 9);
    private final Point SWITCH_C_SOUTH = new Point(3, 11);

    // Station sensors
    private final Point SENSOR_STATION_N_UPPER = new Point(17, 3);
    private final Point SENSOR_STATION_N_LOWER = new Point(17, 5);
    private final Point SENSOR_STATION_S_UPPER = new Point(17, 11);
    private final Point SENSOR_STATION_S_LOWER = new Point(17, 13);

    // Critical section A (junction near top)
    private final Point SENSOR_A_NORTH = new Point(8, 6);
    private final Point SENSOR_A_EAST = new Point(9, 7);
    private final Point SENSOR_A_SOUTH = new Point(9, 8);
    private final Point SENSOR_A_WEST = new Point(7, 7);


    // Critical section B (junction middle right)
    private final Point SENSOR_B_NORTH_UPPER = new Point(16, 7);
    private final Point SENSOR_B_NORTH_LOWER = new Point(16, 8);
    private final Point SENSOR_B_SOUTH_UPPER = new Point(14, 9);
    private final Point SENSOR_B_SOUTH_LOWER = new Point(14, 10);

    // Critical section C (junction bottom left)
    private final Point SENSOR_C_NORTH_UPPER = new Point(5, 9);
    private final Point SENSOR_C_NORTH_LOWER = new Point(5, 10);
    private final Point SENSOR_C_SOUTH_UPPER = new Point(4, 11);
    private final Point SENSOR_C_SOUTH_LOWER = new Point(3, 12);


    public TrainBehaveiour(int trainId, int speed, int[] startingPos,
                           Semaphore semA, Semaphore semB, Semaphore semC) {
        this.trainId = trainId;
        this.position = startingPos;
        this.speed = speed;
        this.semA = semA;
        this.semB = semB;
        this.semC = semC;
        this.tsim = TSimInterface.getInstance();
    }

    @Override
    public void run(){
        while(true){
            sensorEventHandler();
        }
    }

    private void onStation() throws InterruptedException, CommandException{
        tsim.setSpeed(this.trainId, 0);

        Thread.sleep(1000 + (20 * Math.abs(this.speed)));

        tsim.setSpeed(this.trainId, this.speed);
    }

    private void onIntersection(Semaphore sem) throws InterruptedException, CommandException {
        tsim.setSpeed(this.trainId, 0);
        boolean permit;
        do{
            permit = sem.tryAcquire(); //TODO Check if correct
        }while(!permit);
        tsim.setSpeed(this.trainId, this.speed);
    }

    private void doSwitch(Point sw){
        //TODO
    }

    /*
    får event:
         case coord1:

            stanna tåget
            försök aquire semaphore
                om success:
                    drive
                om fail:
                    sov tills vaken


            break;


         case coord 2:
            // do stuff för annan semaphore
         etc
     */
    private void sensorEventHandler() throws CommandException, InterruptedException {
        SensorEvent se;
        try {
            se = tsim.getSensor(this.trainId);

            if (se.getStatus() == SensorEvent.INACTIVE) {return;}

            Point coords = new Point(se.getXpos(), se.getYpos());

        // Station sensors
            if (coords.equals(SENSOR_STATION_N_UPPER)) {
                onStation();

            } else if (coords.equals(SENSOR_STATION_N_LOWER)) {
                onStation();

            } else if (coords.equals(SENSOR_STATION_S_UPPER)) {
                onStation();

            } else if (coords.equals(SENSOR_STATION_S_LOWER)) {
                onStation();

        // Critical section A (junction near top)
            } else if (coords.equals(SENSOR_A_NORTH)) {
                onIntersection(this.semA);

            } else if (coords.equals(SENSOR_A_SOUTH)) {
                onIntersection(this.semA);


            } else if (coords.equals(SENSOR_A_WEST)) {
                onIntersection(this.semA);

            } else if (coords.equals(SENSOR_A_EAST)) {
                onIntersection(this.semA);

        // Critical section B (junction middle right)
            } else if (coords.equals(SENSOR_B_NORTH_UPPER)) {
                onIntersection(this.semB);
                doSwitch(SWITCH_B_NORTH);
            } else if (coords.equals(SENSOR_B_NORTH_LOWER)) {
                onIntersection(this.semB);
                doSwitch(SWITCH_B_NORTH);
                //TODO FIX SWITCHING
            } else if (coords.equals(SENSOR_B_SOUTH_UPPER)) {
                onIntersection(this.semB);
                doSwitch(SWITCH_B_SOUTH);
            } else if (coords.equals(SENSOR_B_SOUTH_LOWER)) {
                onIntersection(this.semB);
                doSwitch(SWITCH_B_SOUTH);

        // Critical section C (junction bottom left)
            } else if (coords.equals(SENSOR_C_NORTH_UPPER)) {
                onIntersection(this.semC);
                doSwitch(SWITCH_C_NORTH);
            } else if (coords.equals(SENSOR_C_NORTH_LOWER)) {
                onIntersection(this.semC);
                doSwitch(SWITCH_C_NORTH);
            } else if (coords.equals(SENSOR_C_SOUTH_UPPER)) {
                onIntersection(this.semC);
                doSwitch(SWITCH_C_SOUTH);
            } else if (coords.equals(SENSOR_C_SOUTH_LOWER)) {
                onIntersection(this.semC);
                doSwitch(SWITCH_C_SOUTH);
            } else {
                System.err.println("Unhandled sensor at " + coords + " for train " + trainId);
            }
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
