
//Prints marco 10 times every second
class Marco implements Runnable {

  public Marco() {


  }

  public void run() {

    for (int i = 0; i < 10; i++) {

      System.out.print("Marco");

      try {

        Thread.sleep(1000); //wait for a sec 

      } catch (Exception e) {


      }

    }

  }

}
