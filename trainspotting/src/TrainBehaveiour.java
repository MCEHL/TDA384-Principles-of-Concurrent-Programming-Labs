import TSim.*;

import java.awt.*;
import java.util.concurrent.Semaphore;

//TODO Change the execution order to make the switching happend after the semaphore lock

public class TrainBehaveiour implements Runnable {
    private int trainId = -1;
    private int speed = 0;
    private TSimInterface tsim;
    private Semaphore semA;
    private Semaphore semB;
    private Semaphore semC;
    private Semaphore semN;
    private Semaphore semS;

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
                           Semaphore semA, Semaphore semB, Semaphore semC, Semaphore semN, Semaphore semS) {
        this.trainId = trainId;
        this.speed = speed;
        this.semA = semA; // Junction A
        this.semB = semB; // Junction B
        this.semC = semC; // Junction C
        this.semN = semN;
        this.semS = semS;
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

        if (sensor.equals(SENSOR_B_NORTH_UPPER)) { // 7 on map

            if (inCritical) {
                // TODO släpp semaforen när tåget lämnar default track, hanteras i sensorEventHandler?
                // TODO var är tåget om den har triggat 7 och inCritical == true ?

            } else {
                //flip switch_b_north (1) to right
                tsim.setSwitch(SWITCH_B_NORTH.x, SWITCH_B_NORTH.y, TSimInterface.SWITCH_RIGHT);

                //flip switch_b_south (2) to left
                tsim.setSwitch(SWITCH_B_SOUTH.x, SWITCH_B_SOUTH.y, TSimInterface.SWITCH_LEFT);

            }

        } else if (sensor.equals(SENSOR_B_NORTH_LOWER)) { //8 on map

            if (inCritical) {
                // TODO var är tåget om den har triggat 8 och inCritical == true ?
            } else {
                //flip switch_b_north (1) to left
                tsim.setSwitch(SWITCH_B_NORTH.x, SWITCH_B_NORTH.y, TSimInterface.SWITCH_LEFT);

                //flip switch_b_south (2) to left
                tsim.setSwitch(SWITCH_B_SOUTH.x, SWITCH_B_SOUTH.y, TSimInterface.SWITCH_LEFT);
            }

        } else if (sensor.equals(SENSOR_B_SOUTH_UPPER)) { //9 on map
            // TODO Verify semaphore stuff is correct

            if (inCritical) {
                //flip switch_b_south (2) to right
                tsim.setSwitch(SWITCH_B_SOUTH.x, SWITCH_B_SOUTH.y, TSimInterface.SWITCH_RIGHT);

                // is default track available?
                if(this.semN.tryAcquire(1)){
                    // if yes, direct train to default track
                    tsim.setSwitch(SWITCH_B_NORTH.x, SWITCH_B_NORTH.y, TSimInterface.SWITCH_RIGHT);

                } else {
                    //if no, direct train to other track
                    tsim.setSwitch(SWITCH_B_NORTH.x, SWITCH_B_NORTH.y, TSimInterface.SWITCH_LEFT);
                }

            } else {
                //den kan inte trigga 9 om den inte är i critical?
            }


        } else if (sensor.equals(SENSOR_B_SOUTH_LOWER)) { //10 on map
            // TODO Verify semaphore stuff is correct

            if (inCritical) {
                //flip switch_b_south (2) to left
                tsim.setSwitch(SWITCH_B_SOUTH.x, SWITCH_B_SOUTH.y, TSimInterface.SWITCH_LEFT);

                // is default track available?
                if(this.semN.tryAcquire(1)){
                    // if yes, direct train to default track
                    tsim.setSwitch(SWITCH_B_NORTH.x, SWITCH_B_NORTH.y, TSimInterface.SWITCH_RIGHT);

                } else {
                    //if no, direct train to other track
                    tsim.setSwitch(SWITCH_B_NORTH.x, SWITCH_B_NORTH.y, TSimInterface.SWITCH_LEFT);
                }

            } else {
                // TODO var är tåget om den har triggat 10 och inCritical == false ?
            }

        } else if (sensor.equals(SENSOR_C_NORTH_UPPER)) { //11 on map
            // TODO Verify semaphore stuff is correct

            if (inCritical) {
                // flip switch_c_north (3) to left
                tsim.setSwitch(SWITCH_C_NORTH.x, SWITCH_C_NORTH.y, TSimInterface.SWITCH_LEFT);

                // is default track available?
                if(this.semS.tryAcquire(1)){
                    // if yes, direct train to default track
                    tsim.setSwitch(SWITCH_C_SOUTH.x, SWITCH_C_SOUTH.y, TSimInterface.SWITCH_RIGHT);

                } else {
                    //if no, direct train to other track
                    tsim.setSwitch(SWITCH_C_SOUTH.x, SWITCH_C_SOUTH.y, TSimInterface.SWITCH_LEFT);
                }

            } else {
                // TODO var är tåget om den har triggat 11 och inCritical == false ?
            }

        } else if (sensor.equals(SENSOR_C_NORTH_LOWER)) { //12 on map
            // TODO Verify semaphore stuff is correct

            if (inCritical) {
                // flip switch_c_north (3) to right
                tsim.setSwitch(SWITCH_C_NORTH.x, SWITCH_C_NORTH.y, TSimInterface.SWITCH_RIGHT);

                // is default track available?
                if(this.semS.tryAcquire(1)){
                    // if yes, direct train to default track
                    tsim.setSwitch(SWITCH_C_SOUTH.x, SWITCH_C_SOUTH.y, TSimInterface.SWITCH_RIGHT);

                } else {
                    //if no, direct train to other track
                    tsim.setSwitch(SWITCH_C_SOUTH.x, SWITCH_C_SOUTH.y, TSimInterface.SWITCH_LEFT);
                }

            } else {
                // TODO var är tåget om den har triggat 12 och inCritical == false ?
            }

        } else if (sensor.equals(SENSOR_C_SOUTH_UPPER)) { //13 on map

            if (inCritical) {
                // TODO var är tåget om den har triggat 13 och inCritical == true ?
            } else {
                //flip switch_c_south (4) to left
                tsim.setSwitch(SWITCH_C_SOUTH.x, SWITCH_C_SOUTH.y, TSimInterface.SWITCH_LEFT);

                //flip switch_c_north (3) to left
                tsim.setSwitch(SWITCH_C_NORTH.x, SWITCH_C_NORTH.y, TSimInterface.SWITCH_LEFT);
            }

        } else if (sensor.equals(SENSOR_C_SOUTH_LOWER)) { //14 on map

            if (inCritical) {
                // TODO släpp semaforen när tåget lämnar default track, hanteras i sensorEventHandler?
                // TODO var är tåget om den har triggat 14 och inCritical == true ?
            } else {
                //flip switch_c_south (4) to right
                tsim.setSwitch(SWITCH_C_SOUTH.x, SWITCH_C_SOUTH.y, TSimInterface.SWITCH_RIGHT);

                //flip switch_c_north (3) to left
                tsim.setSwitch(SWITCH_C_NORTH.x, SWITCH_C_NORTH.y, TSimInterface.SWITCH_LEFT);
            }

        } else {
            // TODO Add stuff here maybe, dunno yet?
        }

    }

    private void sensorEventHandler() throws CommandException, InterruptedException {
        SensorEvent se;
        try {
            se = tsim.getSensor(this.trainId);

            if (se.getStatus() == SensorEvent.INACTIVE) {return;}

            Point coords = new Point(se.getXpos(), se.getYpos());

        // Station sensors
            if (coords.equals(SENSOR_STATION_N_UPPER)) { // 1 on map
                onStation();

            } else if (coords.equals(SENSOR_STATION_N_LOWER)) { // 2 on map
                onStation();

            } else if (coords.equals(SENSOR_STATION_S_UPPER)) { // 15 on map
                onStation();

            } else if (coords.equals(SENSOR_STATION_S_LOWER)) { // 16 on map
                onStation();

        // Critical section A (junction near top)
            } else if (coords.equals(SENSOR_A_NORTH)) { // 3 on map
                onIntersection(this.semA);

            } else if (coords.equals(SENSOR_A_EAST)) { // 4 on map
                onIntersection(this.semA);

            } else if (coords.equals(SENSOR_A_SOUTH)) { // 5 on map
                onIntersection(this.semA);

            } else if (coords.equals(SENSOR_A_WEST)) { // 6 on map
                onIntersection(this.semA);

        // Critical section B (junction middle right)
            }  else if (coords.equals(SENSOR_B_NORTH_UPPER)) { // 7 on map
                // TODO släpp semaforen när tåget lämnar default track
                doSwitch(SENSOR_B_NORTH_UPPER);
                onIntersection(this.semB);

            } else if (coords.equals(SENSOR_B_NORTH_LOWER)) { // 8 on map
                doSwitch(SENSOR_B_NORTH_LOWER);
                onIntersection(this.semB);

            } else if (coords.equals(SENSOR_B_SOUTH_UPPER)) { // 9 on map
                doSwitch(SENSOR_B_SOUTH_UPPER);
                onIntersection(this.semB);

            } else if (coords.equals(SENSOR_B_SOUTH_LOWER)) { // 10 on map
                doSwitch(SENSOR_B_SOUTH_LOWER);
                onIntersection(this.semB);

        // Critical section C (junction bottom left)
            } else if (coords.equals(SENSOR_C_NORTH_UPPER)) { // 11 on map
                doSwitch(SENSOR_C_NORTH_UPPER);
                onIntersection(this.semC);

            } else if (coords.equals(SENSOR_C_NORTH_LOWER)) { // 12 on map
                doSwitch(SENSOR_C_NORTH_LOWER);
                onIntersection(this.semC);

            } else if (coords.equals(SENSOR_C_SOUTH_UPPER)) { // 13 on map
                doSwitch(SENSOR_C_SOUTH_UPPER);
                onIntersection(this.semC);

            } else if (coords.equals(SENSOR_C_SOUTH_LOWER)) { // 14 on map
                // TODO släpp semaforen när tåget lämnar default track
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
