
/*

-Contains the main thread, from here a marco thread
is started and a polo thread is started. This thread then waits to finish
until both marco and polo threads have finished.

*/
class MarcoAndPolo {

  public static void main(String[] args) {

    Thread[] threadsCreated = new Thread[2]; //Used to store the marco and polo thread

    //Create a marco and polo thread
    Thread marcoThread = new Thread(new Marco());
    Thread poloThread = new Thread(new Polo());

    threadsCreated[0] = marcoThread;
    threadsCreated[1] = poloThread;

    //start the threads
    marcoThread.start();
    poloThread.start();

    //Wait for both threads to finish
    for (int i = 0; i < 2; i++) {

      try {

        threadsCreated[i].join();

      }
      catch (Exception e) {



      }

    }

    System.out.println();
    System.out.println("Thats all folks!");

  }

}
