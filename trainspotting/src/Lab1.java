import TSim.*;

import java.util.concurrent.Semaphore;

public class Lab1 {

  public Lab1(int speed1, int speed2) {
    TSimInterface tsi = TSimInterface.getInstance();

    try {
      tsi.setSpeed(1,speed1);
      tsi.setSpeed(2,speed2);
    }
    catch (CommandException e) {
      e.printStackTrace();    // or only e.getMessage() for the error
      System.exit(1);
    }

    Semaphore semA =  new Semaphore(1); // Junction A
    Semaphore semB =  new Semaphore(1); // Junction B
    Semaphore semC =  new Semaphore(1); // Junction C
    Semaphore semM =  new Semaphore(1); // Middle section



    Thread train1 = new Thread(new TrainBehaveiour(1, speed1, semA, semB, semC));
    Thread train2 = new Thread(new TrainBehaveiour(2, speed2, semA, semB, semC));

    train1.start();
    train2.start();

  }
}
