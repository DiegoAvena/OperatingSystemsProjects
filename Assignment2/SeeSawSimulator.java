//Contains the main method that launches the entire seesaw simumlation:
class SeeSawSimulator {

  public static void main(String[] args) {

    Thread[] theThreadsToWaitFor;

    Fred riderNamedFred;
    Wilma riderNamedWilma;

    riderNamedFred = new Fred(1f, 1, 10, true, "Fred");
    riderNamedWilma = new Wilma(1.5f, 7, 10, false, "Wilma");

    riderNamedFred.setOtherRider(riderNamedWilma);
    riderNamedWilma.setOtherRider(riderNamedFred);

    Thread fredsThread = new Thread(riderNamedFred);
    Thread wilmasThread = new Thread(riderNamedWilma);

    theThreadsToWaitFor = new Thread[2];
    theThreadsToWaitFor[0] = fredsThread;
    theThreadsToWaitFor[1] = wilmasThread;

    //Start the threads:
    fredsThread.start();
    wilmasThread.start();

    //wait for the threads to finish:
    for (int threadNumber = 0; threadNumber < theThreadsToWaitFor.length; threadNumber++) {

      try {

        theThreadsToWaitFor[threadNumber].join();

      } catch (Exception e) {

        System.out.println("Something went wrong while waiting for 1 of the threads to finish.");

      }

    }

    System.out.println("Did "+ (riderNamedFred.getNumberOfSimulationsRanSoFar() + 1) + " out of "+riderNamedFred.getNumberOfSimulationsToDo() + " simulations to do. Done!");

  }

}
