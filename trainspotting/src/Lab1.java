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
