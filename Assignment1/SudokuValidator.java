import java.util.Scanner;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

//Base class for soduke validator
class SodukuValidator {

  protected int[][] sodukuGrid;
  protected boolean[][] arrayOfArrayOfcurrentValuesFoundInRowsBeingChecked; //Each inner array is a collection of the values found for the row, column, or sub grid, indicated by true if found or false if not found
  protected boolean[][] arrayOfArrayOfcurrentValuesFoundInColumnsBeingChecked; //Each inner array is a collection of the values found for the row, column, or sub grid, indicated by true if found or false if not found
  protected boolean[][] arrayOfArrayOfcurrentValuesFoundInGridsBeingChecked; //Each inner array is a collection of the values found for the row, column, or sub grid, indicated by true if found or false if not found

  protected ArrayList<ErrorAndSuggestionContainer> listOfErrorsAndSuggestionsThatHaveBeenDetected;

  Scanner reader;
  boolean programShouldEndSinceUserDidNotGiveAFileToReadFrom;

  public ArrayList<ErrorAndSuggestionContainer> getListOfErrorsAndSuggestionsThatHaveBeenDetected() {

    return listOfErrorsAndSuggestionsThatHaveBeenDetected;

  }

  public boolean[][] getArrayOfArrayOfcurrentValuesFoundInRowsBeingChecked() {

    return arrayOfArrayOfcurrentValuesFoundInRowsBeingChecked;

  }

  public boolean[][] getArrayOfArrayOfcurrentValuesFoundInColumnsBeingChecked() {

    return arrayOfArrayOfcurrentValuesFoundInColumnsBeingChecked;

  }

  public boolean[][] getArrayOfArrayOfcurrentValuesFoundInGridsBeingChecked() {

    return arrayOfArrayOfcurrentValuesFoundInGridsBeingChecked;

  }

  public void getResults() {

    if (listOfErrorsAndSuggestionsThatHaveBeenDetected.size() != 0) {

      for (int i = 0; i < listOfErrorsAndSuggestionsThatHaveBeenDetected.size(); i++) {

        int errorRow = listOfErrorsAndSuggestionsThatHaveBeenDetected.get(i).getRowOfError();
        int errorColumn = listOfErrorsAndSuggestionsThatHaveBeenDetected.get(i).getColumnOfError();

        int gridErrorWasIn = determineGrid(errorRow, errorColumn);

        System.out.println("There was an error at grid " +gridErrorWasIn+" row "+errorRow+" column "+errorColumn);

      }

    }
    else {

      System.out.println("The grid was a valid sodoku grid!");

    }


  }

  protected int determineGrid(int errorRow, int errorColumn) {

    if (errorColumn < 3) {

      //either in grid 0, 3, or 6
      if (errorRow < 3) {

        //in grid 0
        return 0;

      }
      else if (errorRow < 6) {

        //we are in grid 3
        return 3;

      }
      else {

        //we are in grid 6
        return 6;

      }

    }
    else if (errorColumn < 6) {

      //either in grid 1, 4, or 7
      if (errorRow < 3) {

        //in grid 1
        return 1;

      }
      else if (errorRow < 6) {

        //we are in grid 4
        return 4;

      }
      else {

        //we are in grid 7
        return 7;

      }

    }
    else {

      //either in grid 2, 5, or 8
      if (errorRow < 3) {

        //in grid 2
        return 2;

      }
      else if (errorRow < 6) {

        //we are in grid 5
        return 5;

      }
      else {

        //we are in grid 8
        return 8;

      }

    }

  }


  public SodukuValidator() {

    sodukuGrid = new int[9][9];
    listOfErrorsAndSuggestionsThatHaveBeenDetected = new ArrayList<ErrorAndSuggestionContainer>();
    /*

    I use an array to keep track of which numbers have been found in
    the current thing that is being checked.

    The numbers 1, 2, 3, 4, ... 9 are represented in the array by
    the index, so 1 is 0 in the array, 2 is 1, and 9 is 8... Or, the 1 based
    number n in the grid is equal to index + 1, so index = n - 1. The boolean at this index
    signals if this number has been found yet.

    So if 1 has been found, then the value at
    1 - 1, or index 0 in the array, will be set to true, and if this value has already been
    set to true, then I know that there is an error in the current thing being checked, and store the
    location (row and column) of this error in a list.

    The values that are left as false by the end of the validation sequence are the values that are missing
    in the current thing being checked, so these are the things I can suggest as fixes to the errors introduced
    by situations as described in the previous paragraph.

    */
    arrayOfArrayOfcurrentValuesFoundInRowsBeingChecked = new boolean[9][9];
    arrayOfArrayOfcurrentValuesFoundInColumnsBeingChecked = new boolean[9][9];
    arrayOfArrayOfcurrentValuesFoundInGridsBeingChecked = new boolean[9][9];

    for (int row = 0; row < 9; row++) {

      for (int column = 0; column < 9; column++) {

        arrayOfArrayOfcurrentValuesFoundInRowsBeingChecked[row][column] = false;
        arrayOfArrayOfcurrentValuesFoundInColumnsBeingChecked[row][column] = false;
        arrayOfArrayOfcurrentValuesFoundInGridsBeingChecked[row][column] = false;

      }

    }

    reader = null;
    programShouldEndSinceUserDidNotGiveAFileToReadFrom = false;

  }

  public int[][] getSodukuGrid() {

    return sodukuGrid;

  }

  public boolean getProgramShouldEndSinceUserDidNotGiveAFileToReadFrom() {

    return programShouldEndSinceUserDidNotGiveAFileToReadFrom;

  }

  public void setSodukuGrid(int[][] sodukuGrid) {

    this.sodukuGrid = sodukuGrid;

  }

  //initializes the grid, returns true if the grid was successfully intialized:
  public void setSodukuGrid(String nameOfFileToBaseGridOffOf) {

    tryToOpenFile(nameOfFileToBaseGridOffOf);

    if (programShouldEndSinceUserDidNotGiveAFileToReadFrom == false) {

      String rowValuesFromTextFile = null;

      //the file was opened, so I can set the grid now.
      for (int i = 0; i < 9; i++) {

        rowValuesFromTextFile = reader.nextLine(); //get the current text representation of the values for this row
        rowValuesFromTextFile = rowValuesFromTextFile.replaceAll(",", "");

        for (int a = 0; a < 9; a++) {

          try {

            sodukuGrid[i][a] = Integer.parseInt(Character.toString(rowValuesFromTextFile.charAt(a)));

          }
          catch(Exception e) {

          }

        }

      }

      printGrid();

    }

  }

  protected void printGrid() {

    System.out.println("The current grid is: ");

    for (int i = 0; i < 9; i++) {

      for (int a = 0; a < 9; a++) {

        System.out.print("|"+sodukuGrid[i][a]);

      }

      System.out.print("|" + '\n');
      System.out.print("-------------------");
      System.out.println();

    }

  }

  //Attempts to open file:
  private void tryToOpenFile(String nameOfFileToBaseGridOffOf) {

    Scanner inputReader = new Scanner(System.in);
    String name = nameOfFileToBaseGridOffOf;

    if (name == null) {

      //user did not give the name of a file through the command line, ask them to give one:
      System.out.println("Enter the name of a file to read from, or enter e to exit.");

      name = inputReader.nextLine();
      if (name.equals("e")) {

        System.out.println("Bye.");
        programShouldEndSinceUserDidNotGiveAFileToReadFrom = true;
        return;

      }

    }

    while (true) {

      try {

        reader = new Scanner(new FileInputStream(name)); //A file has been given, the grid can be set and the program will run
        programShouldEndSinceUserDidNotGiveAFileToReadFrom = false;
        break;

      }
      catch(FileNotFoundException e) {

        System.out.println("The file "+nameOfFileToBaseGridOffOf+"could not be found.");
        System.out.println("Enter the name of another file to read from, or enter e to exit.");

        name = inputReader.nextLine();
        if (name.equals("e")) {

          System.out.println("Bye.");
          programShouldEndSinceUserDidNotGiveAFileToReadFrom = true;
          break;

        }

      }

    }



  }

}
