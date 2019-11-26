import java.util.concurrent.*;

class BusyWork extends SchedularContainer {

  private int howManyTimesToWork; //specifies how many times this thread should run DoWork()

  BusyWork(int howManyTimesToWork, int taskPeriod) {

    super.taskPeriod = taskPeriod;
    this.howManyTimesToWork = howManyTimesToWork;

  }

  public void run() {

    finishedRunning = false;

    if (exit) {

      return;

    }

    try {

      //System.out.println("Task with period "+taskPeriod+" waiting on semaphore");
      mySemaphore.acquire();
      //System.out.println("Task with period "+taskPeriod+" obtained semaphore");

    }
    catch (Exception e) {


    }

    for (int i = 0; i < howManyTimesToWork; i++) {

      if (exit) {

        break;

      }

      CheckIfThreadShouldPauseAndWait();
      DoWork();

    }

    if (semaphoreOfOtherTaskThatMustWaitForMeToFinish != null) {

      semaphoreOfOtherTaskThatMustWaitForMeToFinish.release();

    }

    finishedRunning = true;
    numberOfTimesThreadHasRan++;

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

      if (exit) {

        break;

      }

      CheckIfThreadShouldPauseAndWait();

      currentColumnInMatrix = orderInWhichToTraverseColumnsOfMatrixBy[i];

      for (int row = 0; row < 10; row++) {

        if (exit) {

          break;

        }

        product *= matrix[row][currentColumnInMatrix];

      }

    }

  }

}
