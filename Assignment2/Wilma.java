import java.util.concurrent.*;

//handles wilma's behavior on the seesaw
class Wilma extends SeeSawRider implements Runnable {

  public Wilma(float pushForce, float currentPositionInTheAir, int numberOfSimulationsToDo, boolean letThisRiderDetermineWhenTheSimulationEnds, String riderName) {

    this.pushForce = pushForce;
    this.currentPositionInTheAir = currentPositionInTheAir;
    this.numberOfSimulationsToDo = numberOfSimulationsToDo;
    this.letThisRiderDetermineWhenTheSimulationEnds = letThisRiderDetermineWhenTheSimulationEnds;
    this.riderName = riderName;

    mySemaphore = new Semaphore(0); //Wilma will go after fred

  }

  public void run() {

    pushUp();

  }

}
