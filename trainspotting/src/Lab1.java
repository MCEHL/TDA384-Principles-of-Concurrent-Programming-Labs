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

    Semaphore semN =  new Semaphore(1); // NORTH section, upper track default/critical section
    Semaphore semS =  new Semaphore(1); // SOUTH section, lower track default/critical section


    Thread train1 = new Thread(new TrainBehaviour(1, speed1, semA, semB, semC, semN, semS));
    Thread train2 = new Thread(new TrainBehaviour(2, speed2, semA, semB, semC, semN, semS));

    train1.start();
    train2.start();

  }
}
