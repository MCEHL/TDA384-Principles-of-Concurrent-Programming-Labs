import TSim.*;

import java.util.concurrent.Semaphore;

public class Lab1 {

  public Lab1(int speed1, int speed2) {
    TSimInterface tsi = TSimInterface.getInstance();

    try {
      tsi.setSpeed(1,speed1);
    }
    catch (CommandException e) {
      e.printStackTrace();    // or only e.getMessage() for the error
      System.exit(1);
    }

    Semaphore semA =  new Semaphore(1);
    Semaphore semB =  new Semaphore(1);
    Semaphore semC =  new Semaphore(1);

    final int[] swA = {17, 7};
    final int[] swB = {15, 9};
    final int[] swC = {4, 9};
    final int[] swD = {3, 11};

    final int[] A_N  = {8, 6};
    final int[] A_S  = {9, 8};
    final int[] A_W  = {7, 7};
    final int[] A_E  = {9, 7};

    final int[] STATION_N_UPP  = {17, 3};
    final int[] STATION_N_LOW  = {17, 5};
    final int[] STATION_S_UPP  = {17, 11};
    final int[] STATION_S_LOW  = {17, 13};

    final int[] B_N_UPP  = {16, 7};
    final int[] B_N_LOW  = {16, 8};
    final int[] B_S_UPP  = {14, 9};
    final int[] B_S_LOW  = {14, 10};

    final int[] C_N_UPP  = {5, 9};
    final int[] C_N_LOW  = {5, 10};
    final int[] C_S_UPP  = {4, 11};
    final int[] C_S_LOW  = {3, 12};

    while(true){
        try{
            SensorEvent se1 = tsi.getSensor(1);
            SensorEvent se2 = tsi.getSensor(2);
        } catch(Exception e){
            System.out.println(e.getMessage());
        }



    }

  }
}
