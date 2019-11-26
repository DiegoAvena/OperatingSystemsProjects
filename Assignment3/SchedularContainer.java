import java.util.concurrent.*;

/*

Tasks that are to be scheduled by the RMS
must extend from this class, since this class
contains all of the data needed for the task to be
handled by the RMS

*/
public abstract class SchedularContainer implements Runnable {

  protected int numberOfTimesThreadHasRan; //keeps track of how many times this task has ran
  private int numberOfTimesThreadOverran;
  protected boolean finishedRunning; //signals that this task finished
  protected boolean thisTaskRecentlyOverranItsDeadline;

  protected boolean alreadyAllowedPersonWaitingOnMeToGoSinceIAmSkippingThisTurn;

  protected boolean exit;
  protected boolean threadShouldPauseAndWait;

  protected Semaphore mySemaphore;
  protected Semaphore semaphoreOfOtherTaskThatMustWaitForMeToFinish;

  //Used by the RMS:
  protected int taskPeriod;
  protected int frameTaskMustBeCompletedBy;
  protected Thread RMSThread;
  protected boolean hasBeenScheduled;

  public abstract void run();

  public SchedularContainer() {

    mySemaphore = new Semaphore(0);
    numberOfTimesThreadHasRan = 0; //keeps track of how many times this task has ran
    numberOfTimesThreadOverran = 0;
    finishedRunning = false; //signals that this task finished

    //Used by the RMS:
    taskPeriod = 0;

  }

  public boolean getAlreadyAllowedPersonWaitingOnMeToGoSinceIAmSkippingThisTurn() {

    return alreadyAllowedPersonWaitingOnMeToGoSinceIAmSkippingThisTurn;

  }

  public void setAlreadyAllowedPersonWaitingOnMeToGoSinceIAmSkippingThisTurn(boolean alreadyAllowedPersonWaitingOnMeToGoSinceIAmSkippingThisTurn) {

    this.alreadyAllowedPersonWaitingOnMeToGoSinceIAmSkippingThisTurn = alreadyAllowedPersonWaitingOnMeToGoSinceIAmSkippingThisTurn;

  }

  public boolean getHasBeenScheduled() {

    return hasBeenScheduled;

  }

  public void setHasBeenScheduled(boolean hasBeenScheduled) {

    this.hasBeenScheduled = hasBeenScheduled;

  }

  public void setSemaphoreOfOtherTaskThatMustWaitForMeToFinish(Semaphore semaphoreOfOtherTaskThatMustWaitForMeToFinish) {

    this.semaphoreOfOtherTaskThatMustWaitForMeToFinish = semaphoreOfOtherTaskThatMustWaitForMeToFinish;

  }

  public Semaphore getSemaphoreOfOtherTaskThatMustWaitForMeToFinish() {

    return semaphoreOfOtherTaskThatMustWaitForMeToFinish;

  }

  public void setMySemaphore(Semaphore mySemaphore) {

    this.mySemaphore = mySemaphore;

  }

  public void SetSemaphoreFree() {

    mySemaphore.release();

  }

  public Semaphore getSemaphore() {

    return mySemaphore;

  }

  public void setFrameTaskMustBeCompletedBy(int frameTaskMustBeCompletedBy) {

    this.frameTaskMustBeCompletedBy = frameTaskMustBeCompletedBy;

  }

  public int getFrameTaskMustBeCompletedBy() {

    return frameTaskMustBeCompletedBy;

  }

  public void setRMSThread(Thread RMSThread) {

    this.RMSThread = RMSThread;

  }

  public void SetNumberOfTimesThreadOverran(int numberOfTimesThreadOverran) {

    this.numberOfTimesThreadOverran = numberOfTimesThreadOverran;

  }

  public int getNumberOfTimesThreadOverran() {

    return numberOfTimesThreadOverran;

  }

  public int getTaskPeriod() {

    return taskPeriod;

  }

  public int getNumberOfTimesThreadHasRan() {

    return numberOfTimesThreadHasRan;

  }

  public boolean getFinishedRunning() {

    return this.finishedRunning;

  }

  public void setThisTaskRecentlyOverranItsDeadline(boolean thisTaskRecentlyOverranItsDeadline) {

    this.thisTaskRecentlyOverranItsDeadline = thisTaskRecentlyOverranItsDeadline;

  }

  public boolean getThisTaskRecentlyOverranItsDeadline() {

    return thisTaskRecentlyOverranItsDeadline;

  }

  public void stop() {

    exit = true;

  }

  protected void CheckIfThreadShouldPauseAndWait() {

    if (threadShouldPauseAndWait) {

      try {

        Thread.sleep(Long.MAX_VALUE);

      } catch (InterruptedException e) {

        threadShouldPauseAndWait = false;

      }

    }

  }

  public void Pause() {

    threadShouldPauseAndWait = true;

    /*try {

      //System.out.println("THREAD PAUSED");
      //System.out.println("Thread done running: "+finishedRunning);
      //Thread.sleep(Long.MAX_VALUE);
      //Thread.currentThread().sleep(Long.MAX_VALUE);
      threadShouldPauseAndWait = true;

    } catch (InterruptedException e) {

      System.out.println("Thread resumed");

    } */

  }

  public void Resume() {

    //interrupt();

  }

}
