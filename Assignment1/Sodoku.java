class Sodoku {

  public static void main(String[] args) {

    Thread[] threadsRunning = new Thread[9];
    SodukuValidator masterValidator = new SodukuValidator();
    masterValidator.setSodukuGrid("");

    if (masterValidator.getProgramShouldEndSinceUserDidNotGiveAFileToReadFrom() == false) {

      //the grid has been set

      /*

      For multithreading:

      There are 9 columns, 9 rows, and 9 subgrids to check, which gives a total of 27 tasks that need to
      be done.

      I have 9 threads, since the main will count as 1 thread and the max threads allowed is 10.

      This means then that there will be 3 threads assigned to each task, since 27 / 9 = 3, which will
      divide the 27 tasks evenly amongst the 9 threads. Each thread in turn will have 3 subtasks listed below:

      The 3 threads for column checking the 9 columns
      -each thread handles the checking of 3 columns, giving a total of 9 column checks.

      The 3 threads for row checking the 9 rows
      -each thread handles the checking of 3 rows, giving a total of 9 rows by the time all 3 threads finish

      The 3 threads for grid checking the 9 subgrids
      -each thread will check 3 subgrids, giving a total of 9 subgrid checks by the time all 3 threads finish

      *This way, the tasks are evenly spread out amongst the threads allowed

      */

      //The column threads:
      Thread columnThreadOne = new Thread(new SodokuColumnValidator(3, 0, masterValidator.getArrayOfArrayOfcurrentValuesFoundInColumnsBeingChecked(), masterValidator.getSodukuGrid(), masterValidator.getListOfErrorsAndSuggestionsThatHaveBeenDetected()));
      Thread columnThreadTwo = new Thread(new SodokuColumnValidator(3, 3, masterValidator.getArrayOfArrayOfcurrentValuesFoundInColumnsBeingChecked(), masterValidator.getSodukuGrid(), masterValidator.getListOfErrorsAndSuggestionsThatHaveBeenDetected()));
      Thread columnThreadThree = new Thread(new SodokuColumnValidator(3, 6, masterValidator.getArrayOfArrayOfcurrentValuesFoundInColumnsBeingChecked(), masterValidator.getSodukuGrid(), masterValidator.getListOfErrorsAndSuggestionsThatHaveBeenDetected()));

      //The row threads:
      Thread rowThreadOne = new Thread(new SodokuRowValidator(3, 0, masterValidator.getArrayOfArrayOfcurrentValuesFoundInRowsBeingChecked(), masterValidator.getSodukuGrid(), masterValidator.getListOfErrorsAndSuggestionsThatHaveBeenDetected()));
      Thread rowThreadTwo = new Thread(new SodokuRowValidator(3, 3, masterValidator.getArrayOfArrayOfcurrentValuesFoundInRowsBeingChecked(), masterValidator.getSodukuGrid(), masterValidator.getListOfErrorsAndSuggestionsThatHaveBeenDetected()));
      Thread rowThreadThree = new Thread(new SodokuRowValidator(3, 6, masterValidator.getArrayOfArrayOfcurrentValuesFoundInRowsBeingChecked(), masterValidator.getSodukuGrid(), masterValidator.getListOfErrorsAndSuggestionsThatHaveBeenDetected()));

      //The grid threads:
      int[] locationOfGridToStartAt = new int[2];
      locationOfGridToStartAt[0] = 0;
      locationOfGridToStartAt[1] = 0;
      Thread gridThreadOne = new Thread(new SodokuSubGridValidator(3, masterValidator.getArrayOfArrayOfcurrentValuesFoundInGridsBeingChecked(), locationOfGridToStartAt, masterValidator.getSodukuGrid(), masterValidator.getListOfErrorsAndSuggestionsThatHaveBeenDetected()));

      locationOfGridToStartAt[0] = 3;
      locationOfGridToStartAt[1] = 0;
      Thread gridThreadTwo = new Thread(new SodokuSubGridValidator(3, masterValidator.getArrayOfArrayOfcurrentValuesFoundInGridsBeingChecked(), locationOfGridToStartAt, masterValidator.getSodukuGrid(), masterValidator.getListOfErrorsAndSuggestionsThatHaveBeenDetected()));

      locationOfGridToStartAt[0] = 6;
      locationOfGridToStartAt[1] = 0;
      Thread gridThreadThree = new Thread(new SodokuSubGridValidator(3, masterValidator.getArrayOfArrayOfcurrentValuesFoundInGridsBeingChecked(), locationOfGridToStartAt, masterValidator.getSodukuGrid(), masterValidator.getListOfErrorsAndSuggestionsThatHaveBeenDetected()));

      threadsRunning[0] = columnThreadOne;
      threadsRunning[1] = columnThreadTwo;
      threadsRunning[2] = columnThreadThree;

      threadsRunning[3] = rowThreadOne;
      threadsRunning[4] = rowThreadTwo;
      threadsRunning[5] = rowThreadThree;

      threadsRunning[6] = gridThreadOne;
      threadsRunning[7] = gridThreadTwo;
      threadsRunning[8] = gridThreadThree;

      //start the threads
      for (int i = 0; i < 9; i++) {

        threadsRunning[i].start();

      }

      for (int i = 0; i < 9; i++) {

        try {

          threadsRunning[i].join();

        }
        catch(Exception e) {



        }

      }

      System.out.println("RESULTS:");
      masterValidator.getResults();

    }

  }

}
