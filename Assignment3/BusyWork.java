
class BusyWork implements runnable{

  private int numberOfTimesRan; //keeps track of how many times this task has ran
  private boolean finishedRunning; //signals that this task finished

  BusyWork() {

    numberOfTimesRan = 0;
    finishedRunning = false;

  }

  public int getNumberOfTimesRan() {

    return this.numberOfTimesRan;

  }

  public boolean getFinishedRunning() {

    return this.finishedRunning;

  }

  public void run() {

    finishedRunning = false;
    DoWork();

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

    finishedRunning = true;
    numberOfTimesRan++;

  }

}
