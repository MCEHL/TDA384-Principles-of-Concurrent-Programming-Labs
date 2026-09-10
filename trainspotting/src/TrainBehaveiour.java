import TSim.*;

import java.awt.*;
import java.util.concurrent.Semaphore;

public class TrainBehaveiour implements Runnable {
    private int trainId = -1;
    private int speed = 0;
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


    public TrainBehaveiour(int trainId, int speed,
                           Semaphore semA, Semaphore semB, Semaphore semC) {
        this.trainId = trainId;
        this.speed = speed;
        this.semA = semA; // Junction A
        this.semB = semB; // Junction B
        this.semC = semC; // Junction C
        this.tsim = TSimInterface.getInstance();
    }

    @Override
    public void run(){
        while(true){
            try{
                sensorEventHandler();

            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    private void onStation() throws InterruptedException, CommandException{
        tsim.setSpeed(this.trainId, 0); // stop at station
        Thread.sleep(1000 + (20 * Math.abs(this.speed))); // wait for passengers
        tsim.setSpeed(this.trainId, this.speed); // leave station
    }

    private void onIntersection(Semaphore sem) throws InterruptedException, CommandException {
        if(inCritical){
            sem.release();
        }
        else{
            tsim.setSpeed(this.trainId, 0); // stop at junction
            sem.acquire(); // check if train can cross
            inCritical = !inCritical;
            tsim.setSpeed(this.trainId, this.speed); // when allowed to cross, start driving
        }
    }

    private void doSwitch(Point sensor) throws CommandException {
        /*
        - switch b north (1) starting position is left
        sensor_B_north_upper (7) -> switch b north (1), right
        sensor_B_north_lower (8) -> switch b north (1), left

        - switch c south (4) starting position is left
        sensor_C_south_upper (13) -> switch c south (4), left
        sensor_C_south_lower (14) -> switch c south (4), right
         */

        if (sensor.equals(SENSOR_B_NORTH_UPPER)) {
            //sensor_B_north_upper (7) -> switch b north (1), right

            // in critical -> kommer utifrån, behöver veta var det andra tåget är
            // !in critical -> behöver switcha till höger för att inte deraila

            if(inCritical){
                //TODO
            }
            else{
                tsim.setSwitch(SWITCH_B_NORTH.x, SWITCH_B_NORTH.y, TSimInterface.SWITCH_LEFT);
            }

        } else if (sensor.equals(SENSOR_B_NORTH_LOWER)) {
            //sensor_B_north_lower (8) -> switch b north (1), left

            // in critical -> kommer utifrån, behöver veta var det andra tåget är
            // !in critical -> behöver switcha till vänster för att inte deraila
            if(inCritical){
                //TODO
            }
            else{
                tsim.setSwitch(SWITCH_B_NORTH.x, SWITCH_B_NORTH.y, TSimInterface.SWITCH_RIGHT);
            }

        } else if (sensor.equals(SENSOR_C_SOUTH_UPPER)) {
            //sensor_C_south_upper (13) -> switch c south (4), left

            // in critical -> kommer utifrån, behöver veta var det andra tåget är
            // !in critical -> behöver switcha till vänster för att inte deraila
            if(inCritical){
                //TODO
            }
            else{
                tsim.setSwitch(SWITCH_C_SOUTH.x, SWITCH_C_SOUTH.y, TSimInterface.SWITCH_LEFT);
            }

        } else if (sensor.equals(SENSOR_C_SOUTH_LOWER)) {
            //sensor_C_south_lower (14) -> switch c south (4), right

            // in critical -> kommer utifrån, behöver veta var det andra tåget är
            // !in critical -> behöver switcha till höger för att inte deraila
            if(inCritical){
                //TODO
            }
            else{
                tsim.setSwitch(SWITCH_C_SOUTH.x, SWITCH_C_SOUTH.y, TSimInterface.SWITCH_RIGHT);
            }

        } else {
            //DONT KNOW!
        }



    }

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
                doSwitch(SENSOR_B_NORTH_UPPER);
                onIntersection(this.semB);

            } else if (coords.equals(SENSOR_B_NORTH_LOWER)) {
                doSwitch(SENSOR_B_NORTH_LOWER);
                onIntersection(this.semB);

            } else if (coords.equals(SENSOR_B_SOUTH_UPPER)) {
                doSwitch(SENSOR_B_SOUTH_UPPER);
                onIntersection(this.semB);

            } else if (coords.equals(SENSOR_B_SOUTH_LOWER)) {
                doSwitch(SENSOR_B_SOUTH_LOWER);
                onIntersection(this.semB);

        // Critical section C (junction bottom left)
            } else if (coords.equals(SENSOR_C_NORTH_UPPER)) {
                doSwitch(SENSOR_C_NORTH_UPPER);
                onIntersection(this.semC);

            } else if (coords.equals(SENSOR_C_NORTH_LOWER)) {
                doSwitch(SENSOR_C_NORTH_LOWER);
                onIntersection(this.semC);

            } else if (coords.equals(SENSOR_C_SOUTH_UPPER)) {
                doSwitch(SENSOR_C_SOUTH_UPPER);
                onIntersection(this.semC);

            } else if (coords.equals(SENSOR_C_SOUTH_LOWER)) {
                doSwitch(SENSOR_C_SOUTH_LOWER);
                onIntersection(this.semC);

            } else {
                System.err.println("Unhandled sensor at " + coords + " for train " + trainId);
            }

        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
