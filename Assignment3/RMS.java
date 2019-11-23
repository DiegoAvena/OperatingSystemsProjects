import java.util.concurrent.*;
import java.util.*;

class RMS <T extends SchedularContainer> implements Runnable{

  /*

  Contains all of the threads that will be scheduled here. Sorted based
  on the thread priority

  */
  private T[] arrayOfObjectsToCreateAndScheduleThreadsFor;
  private Thread[] threadsCreated;
  private int unitSize; //should be between 10ms to 100ms

  private int framePeriod; //For this assignment, the program ends when the scheduler goes through this frame period 10x
  private int numberOfFramePeriodsToComplete; //program ends when numberOfFramePeriodsCompleted >= this
  private int numberOfFramePeriodsCompleted; //number of frame periods completed so far
  private int currentFrame; //when this mod framePeriod is 0, I increment numberOfFramePeriodsCompleted
  private int idleFramePeriod; //how long the RMS sleeps for until it wakes up on its own to being a new frame
  private int currentTaskIndex;
  private float timeSchedularBeganCurrentFramePeriodAt;
  private float amountOfTimeElapsedSinceStartOfCurrentFramePeriod;

  private boolean RMSIsDone;

  private Dispatcher dispatcher;
  Thread dispatchThread;

  public RMS(T[] arrayOfObjectsToCreateAndScheduleThreadsFor, int unitSize) {

    this.arrayOfObjectsToCreateAndScheduleThreadsFor = arrayOfObjectsToCreateAndScheduleThreadsFor;
    this.unitSize = unitSize;

    framePeriod = 16;
    numberOfFramePeriodsCompleted = 0;
    numberOfFramePeriodsToComplete = 10;
    currentFrame = 0;
    currentTaskIndex = 0;

    threadsCreated = new Thread[arrayOfObjectsToCreateAndScheduleThreadsFor.length];

  }

  public void run() {

    initialize();
    doScheduling();

  }

  public int getCurrentTaskIndex() {

    return currentTaskIndex;

  }

  public void setCurrentTaskIndex(int currentTaskIndex) {

    this.currentTaskIndex = currentTaskIndex;

  }

  public boolean getRMSIsDone() {

    return RMSIsDone;

  }

  public T[] getArrayOfObjectsToCreateAndScheduleThreadsFor() {

    return arrayOfObjectsToCreateAndScheduleThreadsFor;

  }

  public Thread[] getThreadsCreated() {

    return threadsCreated;

  }

  private void initialize() {

    AssignPrioritiesAndCreateThreads();

    //for debugging:
    for (int i = 0; i < threadsCreated.length; i++) {

      System.out.println("Thread "+(i + 1)+" has been assigned a priority of: "+threadsCreated[i].getPriority());

    }

    //want the idle frame period to be as small as the smallest period for the threads that need to be scheduled:
    idleFramePeriod = arrayOfObjectsToCreateAndScheduleThreadsFor[0].getTaskPeriod();

    //initialize the semaphore of the thread with highest priority (which is at index 0 since I already sorted the array) to 1 so that it can run
    arrayOfObjectsToCreateAndScheduleThreadsFor[0].getSemaphore().release();

    for (int i = 0; i < arrayOfObjectsToCreateAndScheduleThreadsFor.length; i++) {

      if ((i + 1) < arrayOfObjectsToCreateAndScheduleThreadsFor.length) {

        arrayOfObjectsToCreateAndScheduleThreadsFor[i].setSemaphoreOfOtherTaskThatMustWaitForMeToFinish(arrayOfObjectsToCreateAndScheduleThreadsFor[i + 1].getSemaphore());

      }

    }

    for (int i = 0; i < arrayOfObjectsToCreateAndScheduleThreadsFor.length; i++) {

      arrayOfObjectsToCreateAndScheduleThreadsFor[i].setFrameTaskMustBeCompletedBy(currentFrame + arrayOfObjectsToCreateAndScheduleThreadsFor[i].getTaskPeriod());

    }

    timeSchedularBeganCurrentFramePeriodAt = System.currentTimeMillis();

    //At time 0, all threads scheduled:
    threadsCreated[0].start();
    for (int i = 0; i < threadsCreated.length; i++) {

      //threadsCreated[i].start();

    }

    dispatcher = new Dispatcher(this);
    Thread dispatchThread = new Thread(dispatcher);
    dispatchThread.start();

    try {

      //make rms go to sleep for an idle frame period * unitSize:
      Thread.sleep(idleFramePeriod * unitSize);

    } catch (InterruptedException e) {


    }

  }

  private void doScheduling() {

    while (true) {

      System.out.println("Current frame count: "+currentFrame);

      dispatcher.stop();
      currentFrame++;
      //amountOfTimeElapsedSinceStartOfCurrentFramePeriod = System.currentTimeMillis() - timeSchedularBeganCurrentFramePeriodAt;

      //check for overruns in current task:
      if ((arrayOfObjectsToCreateAndScheduleThreadsFor[currentTaskIndex].getFrameTaskMustBeCompletedBy() > currentFrame) && (arrayOfObjectsToCreateAndScheduleThreadsFor[currentTaskIndex].getFinishedRunning() == false)) {

        //this task had an overrun:
        arrayOfObjectsToCreateAndScheduleThreadsFor[currentTaskIndex].setThisTaskRecentlyOverranItsDeadline(true); //so that this task skips its next execution period
        arrayOfObjectsToCreateAndScheduleThreadsFor[currentTaskIndex].SetNumberOfTimesThreadOverran(arrayOfObjectsToCreateAndScheduleThreadsFor[currentTaskIndex].getNumberOfTimesThreadOverran() + 1);

      }

      if (currentFrame == framePeriod) {

        System.out.println("Current frame: "+currentFrame);

        //timeSchedularBeganCurrentFramePeriodAt = System.currentTimeMillis();
        currentFrame = 0;
        numberOfFramePeriodsCompleted++;
        currentTaskIndex = 0;

        if (numberOfFramePeriodsCompleted == numberOfFramePeriodsToComplete) {

          System.out.println("Number of frame periods completed: "+numberOfFramePeriodsCompleted);

          //done
          for (int i = 0; i < arrayOfObjectsToCreateAndScheduleThreadsFor.length; i++) {

            arrayOfObjectsToCreateAndScheduleThreadsFor[i].getSemaphore().release();
            arrayOfObjectsToCreateAndScheduleThreadsFor[i].exit();

          }

          printResults();
          dispatcher.stop();
          break;

        }

      }

      //Reschedule threads:
      arrayOfObjectsToCreateAndScheduleThreadsFor[0].getSemaphore().release();
      arrayOfObjectsToCreateAndScheduleThreadsFor[0].setFrameTaskMustBeCompletedBy(currentFrame + arrayOfObjectsToCreateAndScheduleThreadsFor[0].getTaskPeriod());
      threadsCreated[0] = new Thread(arrayOfObjectsToCreateAndScheduleThreadsFor[0]);
      threadsCreated[0].start();

      for (int i = 1; i < threadsCreated.length; i++) {

        //Close the semaphores of the other threads:
        if (arrayOfObjectsToCreateAndScheduleThreadsFor[i].getSemaphore().tryAcquire()) {

          try {

            arrayOfObjectsToCreateAndScheduleThreadsFor[i].getSemaphore().acquire();

          } catch (Exception e) {


          }

        }

        if (arrayOfObjectsToCreateAndScheduleThreadsFor[i].getFinishedRunning() && ((currentFrame) % arrayOfObjectsToCreateAndScheduleThreadsFor[i].getTaskPeriod() == 0)) {

          arrayOfObjectsToCreateAndScheduleThreadsFor[i].setFrameTaskMustBeCompletedBy(currentFrame + arrayOfObjectsToCreateAndScheduleThreadsFor[i].getTaskPeriod());
          threadsCreated[i] = new Thread(arrayOfObjectsToCreateAndScheduleThreadsFor[i]);

          try {

            //reschedule
            //threadsCreated[i].start();

          } catch (Exception e) {

          }

        }

      }

      dispatcher = new Dispatcher(this);
      Thread dispatchThread = new Thread(dispatcher);
      dispatchThread.start();

      try {

        //make rms go to sleep for an idle frame period * unitSize:
        Thread.sleep(idleFramePeriod * unitSize);

      }
      catch (InterruptedException e){

        //wake up
        //Thread.currentThread().interrupt(); // preserve interruption status

      }

    }

  }

  private void printResults() {

    for (int i = 0; i < arrayOfObjectsToCreateAndScheduleThreadsFor.length; i++) {

      System.out.println("Task "+(i + 1)+" results: ");
      System.out.println("Times ran: "+arrayOfObjectsToCreateAndScheduleThreadsFor[i].getNumberOfTimesThreadHasRan());
      System.out.println("Times it overran: "+arrayOfObjectsToCreateAndScheduleThreadsFor[i].getNumberOfTimesThreadOverran());
      System.out.println();

    }

  }

  private void AssignPrioritiesAndCreateThreads() {

    //start insertion sort to sort tasks from least period to greatest period
    for (int i = 1; i < arrayOfObjectsToCreateAndScheduleThreadsFor.length; i++) {

      T currentItem = arrayOfObjectsToCreateAndScheduleThreadsFor[i];
      int periodOfTaskAtCurrentSpot = arrayOfObjectsToCreateAndScheduleThreadsFor[i].getTaskPeriod();

      /*
      -going to compare this value to the left neighbors,
      and if any of these are bigger, we shift them right to make room for the
      currentValueToInsertIntoCorrectSpot in the array
      */

      int j = i - 1;

      while (j >= 0 && (arrayOfObjectsToCreateAndScheduleThreadsFor[j].getTaskPeriod() > periodOfTaskAtCurrentSpot)) {

        //this left neighbor is bigger, and needs to be shifted right to make some room for currentValueToInsertIntoCorrectSpot
        arrayOfObjectsToCreateAndScheduleThreadsFor[j + 1] = arrayOfObjectsToCreateAndScheduleThreadsFor[j];
        j--;

      }

      /*
      -By this point we are at the correct spot to insert the value at in the array:
      do j + 1 because j will always be 1 less then the actual spot this value should be inserted at due to doing j--
      */
      arrayOfObjectsToCreateAndScheduleThreadsFor[j + 1] = currentItem;

    }

    //by this point, I have an array of objects that are sorted from least task period to greatest task period, so now I can easily assign priorities to the threads I create below:

    int currentPriority = 10; //the max priority goes to the task with the smallest period, which is at index 0 in arrayOfObjectsToCreateAndScheduleThreadsFor since its sorted...

    //Assign the priorites now, and create the threads:
    for (int i = 0; i < arrayOfObjectsToCreateAndScheduleThreadsFor.length; i++) {

      Thread thread = new Thread(arrayOfObjectsToCreateAndScheduleThreadsFor[i]);
      thread.setPriority(currentPriority);
      currentPriority--;
      threadsCreated[i] = thread;

    }

  }

}