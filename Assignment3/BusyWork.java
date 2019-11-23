import java.util.concurrent.*;

class BusyWork extends SchedularContainer {

  private int howManyTimesToWork; //specifies how many times this thread should run DoWork()

  BusyWork(int howManyTimesToWork, int taskPeriod) {

    super.taskPeriod = taskPeriod;
    this.howManyTimesToWork = howManyTimesToWork;

  }

  public void run() {

    finishedRunning = false;

    while (exit == false) {

      /*if (mySemaphore.tryAcquire() == false) {

        System.out.println("Task with period "+taskPeriod+" is waiting on semaphore");

        if (exit) {

          break;

        }

        try {

          Thread.sleep(Long.MAX_VALUE);

        }
        catch (InterruptedException e) {

          Thread.currentThread().interrupt(); // preserve interruption status

        }

      }
      else {

        try {

          System.out.println("Task with period "+taskPeriod+" is acquiring semaphore");
          mySemaphore.acquire();
          System.out.println("Task with period "+taskPeriod+" acquired semaphore");

        }
        catch (Exception e) {


        }

      } */

      try {

        //System.out.println("Task with period "+taskPeriod+" is acquiring semaphore");
        mySemaphore.acquire();
        //System.out.println("Task with period "+taskPeriod+" acquired semaphore");

      }
      catch (Exception e) {


      }

      for (int i = 0; i < howManyTimesToWork; i++) {

        if (exit) {

          break;

        }

        DoWork();

      }

      //System.out.println("Task with period "+taskPeriod+" is running.");

      numberOfTimesThreadHasRan++;
      finishedRunning = true;

      //System.out.println("Task with period "+taskPeriod+" is done running.");

      if (semaphoreOfOtherTaskThatMustWaitForMeToFinish != null) {

        semaphoreOfOtherTaskThatMustWaitForMeToFinish.release();

      }

      //RMSThread.interrupt(); //wakes up RMS so that another task may be scheduled
      break;

    }

  }

  private void DoWork() {

    int currentColumnInMatrix = 0;
    float matrix[][] = {{1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f},
                        {1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f},
                        {1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f},
                        {1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f},
                        {1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f},
                        {1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f},
                        {1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f},
                        {1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f},
                        {1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f},
                        {1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f}
                      };

    int orderInWhichToTraverseColumnsOfMatrixBy[] = {0, 5, 1, 6, 2, 7, 3, 8, 4, 9};

    float product = 1.0f;

    for (int i = 0; i < 10; i++) {


      currentColumnInMatrix = orderInWhichToTraverseColumnsOfMatrixBy[i];

      for (int row = 0; row < 10; row++) {

        product *= matrix[row][currentColumnInMatrix];

      }

    }

  }

}
