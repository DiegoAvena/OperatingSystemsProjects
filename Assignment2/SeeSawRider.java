import java.util.concurrent.*;

//Base class for people who go on the seesaw:
class SeeSawRider {

  protected float pushForce;
  protected float currentPositionInTheAir;

  SeeSawRider otherRider; //used to update this persons position from here

  protected int numberOfSimulationsRanSoFar; //program ends after this reaches the specified threshold
  protected int numberOfSimulationsToDo; //the threshold for how many simulations should occur

  protected Semaphore mySemaphore; //the semaphore that belongs to this person

  protected boolean letThisRiderDetermineWhenTheSimulationEnds; //determines if this is the rider who determines when the simulation ends

  //when both booleans below are true, then this means 1 simulation has occured so increment the number of simulations completed:
  private boolean wentUpAlready;
  private boolean wentDownAlready;

  protected String riderName; //the name of the rider

  public SeeSawRider() {

    pushForce = 0;
    currentPositionInTheAir = 0;

    numberOfSimulationsRanSoFar = 0;

    otherRider = null;

    numberOfSimulationsToDo = 0;
    numberOfSimulationsToDo = 0;

    mySemaphore = null;

  }

  public float getCurrentPositionInTheAir() {

    return currentPositionInTheAir;

  }

  public void setCurrentPositionInTheAir(float currentPositionInTheAir) {

    this.currentPositionInTheAir = currentPositionInTheAir;

  }

  public Semaphore getMySymaphore() {

    return mySemaphore;

  }

  public String getRiderName() {

    return riderName;

  }

  public void setWentDownAlready(boolean wentDownAlready) {

    this.wentDownAlready = wentDownAlready;

  }

  public void setNumberofSimulationsRanSoFar(int numberOfSimulationsRanSoFar) {

    this.numberOfSimulationsRanSoFar = numberOfSimulationsRanSoFar;

  }

  public int getNumberOfSimulationsRanSoFar() {

    return numberOfSimulationsRanSoFar;

  }

  public int getNumberOfSimulationsToDo() {

    return numberOfSimulationsToDo;

  }

  public void setOtherRider(SeeSawRider otherRider) {

    this.otherRider = otherRider;

  }

  //Simulates the pushing up and falling down of riders on the seesaw
  protected void pushUp() {

    try {

      while (numberOfSimulationsRanSoFar < (numberOfSimulationsToDo - 1)) {

        mySemaphore.acquire(); //waits until the other person is done pushing up
        System.out.println();

        //Check if 1 more seesaw simulation has been completed (when the same person goes up and then down, this is considered as 1 simulation)
        if ((wentUpAlready && wentDownAlready) && (letThisRiderDetermineWhenTheSimulationEnds)) {

          numberOfSimulationsRanSoFar++;
          otherRider.setNumberofSimulationsRanSoFar(numberOfSimulationsRanSoFar);

          System.out.println("Did "+numberOfSimulationsRanSoFar + " out of "+numberOfSimulationsToDo +" simulations to do.");
          System.out.println();

          wentUpAlready = false;
          wentDownAlready = false;

        }

        System.out.println("It is time for "+riderName+" to push up.");

        //Keep pushing up until the other rider is within 1 ft of the ground below them
        while (otherRider.getCurrentPositionInTheAir() > 1) {

          //keep pushing up:
          currentPositionInTheAir += pushForce;
          otherRider.setCurrentPositionInTheAir(otherRider.getCurrentPositionInTheAir() - pushForce); //other person goes down more

          System.out.println(riderName+" position: "+currentPositionInTheAir);
          System.out.println(otherRider.getRiderName() + " position: "+otherRider.getCurrentPositionInTheAir());

          Thread.sleep(1000); //wait for about 1 sec before printing next results

        }

        wentUpAlready = true;
        otherRider.setWentDownAlready(true);

        //Signal to other rider that it is their turn:
        otherRider.getMySymaphore().release();

      }



    } catch (Exception e) {

      System.out.println("Something went wrong with" +riderName+"'s thread...'");

    }

  }

}
