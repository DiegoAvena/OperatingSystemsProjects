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

      //System.out.println("Thread "+(i + 1)+" has been assigned a priority of: "+threadsCreated[i].getPriority());

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
    //threadsCreated[0].start();
    for (int i = 0; i < threadsCreated.length; i++) {

      //System.out.println("Starting thread "+ (i + 1));
      threadsCreated[i].start();
      arrayOfObjectsToCreateAndScheduleThreadsFor[i].setHasBeenScheduled(true);

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

      //System.out.println("Current frame count: "+currentFrame);
      //ResetSemaphoreReferences();
      dispatcher.stop();
      currentFrame++;
      //amountOfTimeElapsedSinceStartOfCurrentFramePeriod = System.currentTimeMillis() - timeSchedularBeganCurrentFramePeriodAt;

      //check for overruns in current task:
      for (int i = 1; i < arrayOfObjectsToCreateAndScheduleThreadsFor.length; i++) {

        if ((arrayOfObjectsToCreateAndScheduleThreadsFor[i].getFrameTaskMustBeCompletedBy() > currentFrame) && (arrayOfObjectsToCreateAndScheduleThreadsFor[i].getFinishedRunning() == false) && (arrayOfObjectsToCreateAndScheduleThreadsFor[i].getThisTaskRecentlyOverranItsDeadline() == false)) {

          //this task had an overrun:
          arrayOfObjectsToCreateAndScheduleThreadsFor[i].setThisTaskRecentlyOverranItsDeadline(true); //so that this task skips its next execution period
          arrayOfObjectsToCreateAndScheduleThreadsFor[i].SetNumberOfTimesThreadOverran(arrayOfObjectsToCreateAndScheduleThreadsFor[i].getNumberOfTimesThreadOverran() + 1);
          arrayOfObjectsToCreateAndScheduleThreadsFor[i].setHasBeenScheduled(false);
          //break;

        }
        else if (arrayOfObjectsToCreateAndScheduleThreadsFor[i].getThisTaskRecentlyOverranItsDeadline() /*&& ((currentFrame % arrayOfObjectsToCreateAndScheduleThreadsFor[i].getTaskPeriod()) == 0)*/) {

          //System.out.println("Task "+(currentTaskIndex + 1)+"is can now be scheduled for its upcomming execution period, since it already skipped an execution period due to an overrun.");

          //Insures that while the scheduling of this thread is skipped for its next execution period, it does not skip the one after
          arrayOfObjectsToCreateAndScheduleThreadsFor[i].setThisTaskRecentlyOverranItsDeadline(false);
          arrayOfObjectsToCreateAndScheduleThreadsFor[i].setHasBeenScheduled(true); //so that dispatcher knows it can now resume this thread for completion
          //break;

        }

      }

      /*if ((arrayOfObjectsToCreateAndScheduleThreadsFor[currentTaskIndex].getFrameTaskMustBeCompletedBy() > currentFrame) && (arrayOfObjectsToCreateAndScheduleThreadsFor[currentTaskIndex].getFinishedRunning() == false) && (arrayOfObjectsToCreateAndScheduleThreadsFor[currentTaskIndex].getThisTaskRecentlyOverranItsDeadline() == false)) {

        //this task had an overrun:
        arrayOfObjectsToCreateAndScheduleThreadsFor[currentTaskIndex].setThisTaskRecentlyOverranItsDeadline(true); //so that this task skips its next execution period
        arrayOfObjectsToCreateAndScheduleThreadsFor[currentTaskIndex].SetNumberOfTimesThreadOverran(arrayOfObjectsToCreateAndScheduleThreadsFor[currentTaskIndex].getNumberOfTimesThreadOverran() + 1);
        arrayOfObjectsToCreateAndScheduleThreadsFor[currentTaskIndex].setHasBeenScheduled(false);

      }
      else if (arrayOfObjectsToCreateAndScheduleThreadsFor[currentTaskIndex].getThisTaskRecentlyOverranItsDeadline() && ((currentFrame % arrayOfObjectsToCreateAndScheduleThreadsFor[currentTaskIndex].getTaskPeriod()) == 0)) {

        //System.out.println("Task "+(currentTaskIndex + 1)+"is can now be scheduled for its upcomming execution period, since it already skipped an execution period due to an overrun.");

        //Insures that while the scheduling of this thread is skipped for its next execution period, it does not skip the one after
        arrayOfObjectsToCreateAndScheduleThreadsFor[currentTaskIndex].setThisTaskRecentlyOverranItsDeadline(false);
        arrayOfObjectsToCreateAndScheduleThreadsFor[currentTaskIndex].setHasBeenScheduled(true); //so that dispatcher knows it can now resume this thread for completion

      } */

      //pause thread that was running, since thread 1 now needs to run
      if ((arrayOfObjectsToCreateAndScheduleThreadsFor[currentTaskIndex].getFinishedRunning() == false) && (currentTaskIndex != 0)) {

        arrayOfObjectsToCreateAndScheduleThreadsFor[currentTaskIndex].Pause();

        /*try {

          //System.out.println("RMS Pausing thread "+(currentTaskIndex + 1)); //NOTE: THIS IS WHERE THE RMS GETS STUCK WHEN THERE IS AN OVERRUN!! THE BOTTOM PRINT STATEMENT NEVER PRINTS
          arrayOfObjectsToCreateAndScheduleThreadsFor[currentTaskIndex].Pause();
          //System.out.println("RMS Paused thread "+(currentTaskIndex + 1));

        } catch (Exception e){

          //System.out.println("Exception caught");

        }*/

      }

      if (currentFrame == framePeriod) {

        //System.out.println("Current frame: "+currentFrame);

        //timeSchedularBeganCurrentFramePeriodAt = System.currentTimeMillis();
        currentFrame = 0;
        numberOfFramePeriodsCompleted++;
        currentTaskIndex = 0;

        if (numberOfFramePeriodsCompleted == numberOfFramePeriodsToComplete) {

          //System.out.println("Number of frame periods completed: "+numberOfFramePeriodsCompleted);

          //dispatcher.stop();

          //done
          for (int i = 0; i < arrayOfObjectsToCreateAndScheduleThreadsFor.length; i++) {

            arrayOfObjectsToCreateAndScheduleThreadsFor[i].stop();
            arrayOfObjectsToCreateAndScheduleThreadsFor[i].getSemaphore().release();

          }

          printResults();
          break;

        }

      }

      //Reschedule thread 1, since the frame period is always the period of thread 1
      arrayOfObjectsToCreateAndScheduleThreadsFor[0].getSemaphore().release();
      arrayOfObjectsToCreateAndScheduleThreadsFor[0].setFrameTaskMustBeCompletedBy(currentFrame + arrayOfObjectsToCreateAndScheduleThreadsFor[0].getTaskPeriod());
      threadsCreated[0] = new Thread(arrayOfObjectsToCreateAndScheduleThreadsFor[0]);
      threadsCreated[0].start();
      arrayOfObjectsToCreateAndScheduleThreadsFor[0].setHasBeenScheduled(true);

      //System.out.println("Closing semaphores of the other threads, and creating new threads");

      for (int i = 1; i < threadsCreated.length; i++) {

        //Close the semaphores of the other threads:
        arrayOfObjectsToCreateAndScheduleThreadsFor[i].getSemaphore().release();

        try {

          arrayOfObjectsToCreateAndScheduleThreadsFor[i].getSemaphore().acquire();

        }
        catch (Exception e) {


        }

        if (arrayOfObjectsToCreateAndScheduleThreadsFor[i].getFinishedRunning() && ((currentFrame % arrayOfObjectsToCreateAndScheduleThreadsFor[i].getTaskPeriod()) == 0) && (arrayOfObjectsToCreateAndScheduleThreadsFor[i].getThisTaskRecentlyOverranItsDeadline() == false)) {

          //reschedule this thread
          arrayOfObjectsToCreateAndScheduleThreadsFor[i].setFrameTaskMustBeCompletedBy(currentFrame + arrayOfObjectsToCreateAndScheduleThreadsFor[i].getTaskPeriod());
          threadsCreated[i] = new Thread(arrayOfObjectsToCreateAndScheduleThreadsFor[i]);
          arrayOfObjectsToCreateAndScheduleThreadsFor[i].setHasBeenScheduled(true);

          try {

            threadsCreated[i].start();
            //arrayOfObjectsToCreateAndScheduleThreadsFor[i].Pause();

          } catch (Exception e) {

          }

        }
        /*else if (arrayOfObjectsToCreateAndScheduleThreadsFor[i].getThisTaskRecentlyOverranItsDeadline() && ((currentFrame % arrayOfObjectsToCreateAndScheduleThreadsFor[i].getTaskPeriod()) == 0)) {

          //Insures that while the scheduling of this thread is skipped for its next execution period, it does not skip the one after
          arrayOfObjectsToCreateAndScheduleThreadsFor[i].setThisTaskRecentlyOverranItsDeadline(false);

          //if ((i - 1) >= 0) {

            //arrayOfObjectsToCreateAndScheduleThreadsFor[i - 1].setSemaphoreOfOtherTaskThatMustWaitForMeToFinish(arrayOfObjectsToCreateAndScheduleThreadsFor[i].getSemaphoreOfOtherTaskThatMustWaitForMeToFinish());

          //}

        } */

      }

      //System.out.println("Done Closing semaphores of the other threads, and creating new threads");

      //ResetSemaphoreReferences();

      dispatcher = new Dispatcher(this);
      Thread dispatchThread = new Thread(dispatcher);
      dispatchThread.start();

      try {

        //System.out.println("RMS going to sleep");
        //make rms go to sleep for an idle frame period * unitSize:
        Thread.sleep(idleFramePeriod * unitSize);

      }
      catch (InterruptedException e){

        //wake up
        //Thread.currentThread().interrupt(); // preserve interruption status

      }

    }

  }

  void ResetSemaphoreReferences() {

    arrayOfObjectsToCreateAndScheduleThreadsFor[0].getSemaphore().release();

    for (int i = 0; i < arrayOfObjectsToCreateAndScheduleThreadsFor.length; i++) {

      if ((i + 1) < arrayOfObjectsToCreateAndScheduleThreadsFor.length) {

        arrayOfObjectsToCreateAndScheduleThreadsFor[i].setSemaphoreOfOtherTaskThatMustWaitForMeToFinish(arrayOfObjectsToCreateAndScheduleThreadsFor[i + 1].getSemaphore());

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
