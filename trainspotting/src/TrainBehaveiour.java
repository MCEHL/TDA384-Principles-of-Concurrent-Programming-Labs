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
    private final Point SW_A = new Point(17, 7);
    private final Point SW_B = new Point(15, 9);
    private final Point SW_C = new Point(4, 9);
    private final Point SW_D = new Point(3, 11);

    // Station sensors
    private final Point STATION_N_UPP = new Point(17, 3);
    private final Point STATION_N_LOW = new Point(17, 5);
    private final Point STATION_S_UPP = new Point(17, 11);
    private final Point STATION_S_LOW = new Point(17, 13);

    // Critical section A (junction near top)
    private final Point A_N = new Point(8, 6);
    private final Point A_S = new Point(9, 8);
    private final Point A_W = new Point(7, 7);
    private final Point A_E = new Point(9, 7);

    // Critical section B (junction middle right)
    private final Point B_N_UPP = new Point(16, 7);
    private final Point B_N_LOW = new Point(16, 8);
    private final Point B_S_UPP = new Point(14, 9);
    private final Point B_S_LOW = new Point(14, 10);

    // Critical section C (junction bottom left)
    private final Point C_N_UPP = new Point(5, 9);
    private final Point C_N_LOW = new Point(5, 10);
    private final Point C_S_UPP = new Point(4, 11);
    private final Point C_S_LOW = new Point(3, 12);

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
            if (coords.equals(STATION_N_UPP)) {
                onStation();

            } else if (coords.equals(STATION_N_LOW)) {
                onStation();

            } else if (coords.equals(STATION_S_UPP)) {
                onStation();

            } else if (coords.equals(STATION_S_LOW)) {
                onStation();

        // Critical section A (junction near top)
            } else if (coords.equals(A_N)) {
                onIntersection(this.semA);

            } else if (coords.equals(A_S)) {
                onIntersection(this.semA);


            } else if (coords.equals(A_W)) {
                onIntersection(this.semA);

            } else if (coords.equals(A_E)) {
                onIntersection(this.semA);

        // Critical section B (junction middle right)
            } else if (coords.equals(B_N_UPP)) {
                onIntersection(this.semB);
                doSwitch(SW_A);
            } else if (coords.equals(B_N_LOW)) {
                onIntersection(this.semB);
                doSwitch(SW_A);
                //TODO FIX SWITCHING
            } else if (coords.equals(B_S_UPP)) {
                onIntersection(this.semB);
                doSwitch(SW_B);
            } else if (coords.equals(B_S_LOW)) {
                onIntersection(this.semB);
                doSwitch(SW_B);

        // Critical section C (junction bottom left)
            } else if (coords.equals(C_N_UPP)) {
                onIntersection(this.semC);
                doSwitch(SW_C);
            } else if (coords.equals(C_N_LOW)) {
                onIntersection(this.semC);
                doSwitch(SW_C);
            } else if (coords.equals(C_S_UPP)) {
                onIntersection(this.semC);
                doSwitch(SW_D);
            } else if (coords.equals(C_S_LOW)) {
                onIntersection(this.semC);
                doSwitch(SW_D);
            } else {
                System.err.println("Unhandled sensor at " + coords + " for train " + trainId);
            }
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
