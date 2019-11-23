import java.util.concurrent.*;

class Dispatcher implements Runnable {

  RMS rmsToListenTo;

  Semaphore[] semaphoresOfTasks;
  SchedularContainer[] arrayOfTasks;
  Thread[] threadsForTheTasks;
  boolean exit;

  Dispatcher(RMS rmsToListenTo) {

    this.rmsToListenTo = rmsToListenTo;
    arrayOfTasks = rmsToListenTo.getArrayOfObjectsToCreateAndScheduleThreadsFor();
    threadsForTheTasks = rmsToListenTo.getThreadsCreated();
    semaphoresOfTasks = new Semaphore[threadsForTheTasks.length];

    for (int i = 0; i < threadsForTheTasks.length; i++) {

      semaphoresOfTasks[i] = arrayOfTasks[i].getSemaphore();

    }

  }

  public void run () {

    while (exit == false) {

      //wake up threads whose semaphores are available:
      for (int i = 0; i < semaphoresOfTasks.length; i++) {

        if (exit) {

          //stop thread
          break;

        }

        if (semaphoresOfTasks[i].tryAcquire()) {

            //resume the task thread:
            //rmsToListenTo.setCurrentTaskIndex(i);
            //threadsForTheTasks[i].interrupt();
            break;

        }

      }

    }

  }

  public void stop() {

    exit = true;

  }

}
