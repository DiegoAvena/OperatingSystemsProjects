import java.util.concurrent.*;

//Manages fred's behavior on the seesaw:
class Fred extends SeeSawRider implements Runnable {

  public Fred(float pushForce, float currentPositionInTheAir, int numberOfSimulationsToDo, boolean letThisRiderDetermineWhenTheSimulationEnds, String riderName) {

    this.pushForce = pushForce;
    this.currentPositionInTheAir = currentPositionInTheAir;
    this.numberOfSimulationsToDo = numberOfSimulationsToDo;
    this.letThisRiderDetermineWhenTheSimulationEnds = letThisRiderDetermineWhenTheSimulationEnds;
    this.riderName = riderName;

    mySemaphore = new Semaphore(1); //Fred will go first so initialize his semaphore to 1

  }

  public void run() {

    pushUp();

  }

}
