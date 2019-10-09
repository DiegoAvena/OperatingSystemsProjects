
//Prints polo with a \n appended 10 times every sec
class Polo implements Runnable {

  public Polo() {


  }

  public void run() {

    for (int i = 0; i < 10; i++) {

      System.out.print("Polo "+'\n');

      try {

        Thread.sleep(1000);

      } catch (Exception e) {



      }

    }

  }

}
