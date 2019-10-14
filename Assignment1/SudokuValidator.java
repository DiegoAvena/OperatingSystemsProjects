import java.util.Scanner;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import java.awt.Color;

//Base class for soduke validator
class SodukuValidator {

  protected SodokuUIManager sodokuUIManager;

  protected int[][] sodukuGrid;
  protected int[][][] arrayOfArrayOfcurrentValuesFoundInRowsBeingChecked; //Each inner array is a collection of the values found for the row, column, or sub grid, indicated by true if found or false if not found
  protected int[][][] arrayOfArrayOfcurrentValuesFoundInColumnsBeingChecked; //Each inner array is a collection of the values found for the row, column, or sub grid, indicated by true if found or false if not found
  protected int[][][] arrayOfArrayOfcurrentValuesFoundInGridsBeingChecked; //Each inner array is a collection of the values found for the row, column, or sub grid, indicated by true if found or false if not found

  protected ArrayList<ErrorAndSuggestionContainer> listOfErrorsAndSuggestionsThatHaveBeenDetected;

  Scanner reader;
  boolean programShouldEndSinceUserDidNotGiveAFileToReadFrom;

  public ArrayList<ErrorAndSuggestionContainer> getListOfErrorsAndSuggestionsThatHaveBeenDetected() {

    return listOfErrorsAndSuggestionsThatHaveBeenDetected;

  }

  public int[][][] getArrayOfArrayOfcurrentValuesFoundInRowsBeingChecked() {

    return arrayOfArrayOfcurrentValuesFoundInRowsBeingChecked;

  }

  public int[][][] getArrayOfArrayOfcurrentValuesFoundInColumnsBeingChecked() {

    return arrayOfArrayOfcurrentValuesFoundInColumnsBeingChecked;

  }

  public int[][][] getArrayOfArrayOfcurrentValuesFoundInGridsBeingChecked() {

    return arrayOfArrayOfcurrentValuesFoundInGridsBeingChecked;

  }

  private void makeSuggestion(int errorNumber) {

    int errorRow = listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getRowOfError();
    int errorColumn = listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getColumnOfError();

    int rowOfNumberThisValueConflictsWith = listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getRowOfNumberThisValueConflictsWith();
    int columnOfNumberThisValueConflictsWith = listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getColumnOfNumberThisValueConflictsWith();

    int gridErrorWasIn = determineGrid(errorRow, errorColumn);

    //Determine if the assumed error is indeed the cause of the error:
    boolean rowOfAssumedErrorIsMissingSomething = false;
    for (int i = 0; i < 9; i++) {

      if (arrayOfArrayOfcurrentValuesFoundInRowsBeingChecked[errorRow][i] == null) {

        //the row this assumed error is in is missing something
        rowOfAssumedErrorIsMissingSomething = true;
        break;

      }

    }

    boolean columnOfAssumedErrorIsMissingSomething = false;
    for (int i = 0; i < 9; i++) {

      if (arrayOfArrayOfcurrentValuesFoundInColumnsBeingChecked[errorColumn][i] == null) {

        //the column this assumed error is in is missing something
        columnOfAssumedErrorIsMissingSomething = true;
        break;

      }

    }

    boolean gridOfAssumedErrorIsMissingSomething = false;
    for (int i = 0; i < 9; i++) {

      if (arrayOfArrayOfcurrentValuesFoundInGridsBeingChecked[gridErrorWasIn][i] == null) {

        //the grid this assumed error is in is missing something
        gridOfAssumedErrorIsMissingSomething = true;
        break;

      }

    }

    if (rowOfAssumedErrorIsMissingSomething && columnOfAssumedErrorIsMissingSomething && gridOfAssumedErrorIsMissingSomething) {

      //the assumed error is the error, so a suggestion is possible for this error
      System.out.println("There was an error at row "+(errorRow + 1)+" column "+(errorColumn + 1));
      System.out.println("Solution: Replace "+sodukuGrid[errorRow][errorColumn]+ " with " +determineSolutionForCell(errorRow, errorColumn));

      sodokuUIManager.setResultsText("There was an error at row "+(errorRow + 1)+" column "+(errorColumn + 1));
      sodokuUIManager.setResultsText("    Solution: Replace "+sodukuGrid[errorRow][errorColumn]+ " with " +determineSolutionForCell(errorRow, errorColumn));

      sodokuUIManager.setPanelColor(Color.RED, errorRow, errorColumn);

    }
    else {

      int solution = determineSolutionForCell(rowOfNumberThisValueConflictsWith, columnOfNumberThisValueConflictsWith);

      /*

      Although duplicates are supposed to not be in the arrays above, it can still happen since there is no
      synchronization. So if this is the case, the if statement above will never enter and the program will go here,
      but then the solution may be 0 because for a previous version of this duplicate a solution has already been
      given, so a check is needed here to prevent to reporting of an invalid solution of 0.

      */
      if (solution != 0) {

        //the error must be the value the assumed error conflicts with, so a suggestion should be made for this one instead:
        System.out.println("There was an error at row "+(rowOfNumberThisValueConflictsWith + 1)+" column "+(columnOfNumberThisValueConflictsWith + 1));
        System.out.println("Solution: Replace "+sodukuGrid[rowOfNumberThisValueConflictsWith][columnOfNumberThisValueConflictsWith]+ " with " +solution);

        sodokuUIManager.setResultsText("There was an error at row "+(rowOfNumberThisValueConflictsWith + 1)+" column "+(columnOfNumberThisValueConflictsWith + 1));
        sodokuUIManager.setResultsText("    Solution: Replace "+sodukuGrid[rowOfNumberThisValueConflictsWith][columnOfNumberThisValueConflictsWith]+ " with " +solution);

        sodokuUIManager.setPanelColor(Color.RED, rowOfNumberThisValueConflictsWith, columnOfNumberThisValueConflictsWith);

      }

    }

  }

  private int determineSolutionForCell(int cellRow, int cellColumn) {

    int[][] arrayOfValuesFoundInRowThisCellIsAt = arrayOfArrayOfcurrentValuesFoundInRowsBeingChecked[cellRow];
    int[][] arrayOfValuesFoundInColumnThisCellIsAt = arrayOfArrayOfcurrentValuesFoundInColumnsBeingChecked[cellColumn];

    int gridThisCellIsIn = determineGrid(cellRow, cellColumn);
    int[][] arrayOfValuesFoundInGridThisCellIsAt = arrayOfArrayOfcurrentValuesFoundInGridsBeingChecked[gridThisCellIsIn];

    int solutionForCell = 0;
    for (int i = 0; i < 9; i++) {

      if ((arrayOfValuesFoundInRowThisCellIsAt[i] == null) && (arrayOfValuesFoundInGridThisCellIsAt[i] == null) && (arrayOfValuesFoundInColumnThisCellIsAt[i] == null)) {

        //Signal that the solution given here fixes the missing value here:
        arrayOfValuesFoundInRowThisCellIsAt[i] = new int[2];
        arrayOfValuesFoundInRowThisCellIsAt[i][0] = cellRow;
        arrayOfValuesFoundInRowThisCellIsAt[i][1] = cellColumn;

        arrayOfValuesFoundInColumnThisCellIsAt[i] = new int[2];
        arrayOfValuesFoundInColumnThisCellIsAt[i][0] = cellRow;
        arrayOfValuesFoundInColumnThisCellIsAt[i][1] = cellColumn;

        arrayOfValuesFoundInGridThisCellIsAt[i] = new int[2];
        arrayOfValuesFoundInGridThisCellIsAt[i][0] = cellRow;
        arrayOfValuesFoundInGridThisCellIsAt[i][1] = cellColumn;

        //return the solution for this cell:
        solutionForCell = i + 1; //n = index + 1, because n is 1 based and index is 0 based
        break;

      }

    }

    return solutionForCell;

  }

  public void getResults() {

    if (listOfErrorsAndSuggestionsThatHaveBeenDetected.size() != 0) {

      for (int i = 0; i < listOfErrorsAndSuggestionsThatHaveBeenDetected.size(); i++) {

        /*int errorRow = listOfErrorsAndSuggestionsThatHaveBeenDetected.get(i).getRowOfError();
        int errorColumn = listOfErrorsAndSuggestionsThatHaveBeenDetected.get(i).getColumnOfError();

        int rowOfNumberThisValueConflictsWith = listOfErrorsAndSuggestionsThatHaveBeenDetected.get(i).getRowOfNumberThisValueConflictsWith();
        int columnOfNumberThisValueConflictsWith = listOfErrorsAndSuggestionsThatHaveBeenDetected.get(i).getColumnOfNumberThisValueConflictsWith();

        int gridErrorWasIn = determineGrid(errorRow, errorColumn);

        System.out.println("There was an at row "+(errorRow + 1)+" column "+(errorColumn + 1) + ", this conflicts with the number at row: "+ (rowOfNumberThisValueConflictsWith + 1) + " column: "+(columnOfNumberThisValueConflictsWith + 1));
        */
        makeSuggestion(i);

      }

    }
    else {

      System.out.println("The grid was a valid sodoku grid!");
      sodokuUIManager.setResultsText("The grid was a valid sodoku grid!");

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
    arrayOfArrayOfcurrentValuesFoundInRowsBeingChecked = new int[9][9][2];
    arrayOfArrayOfcurrentValuesFoundInColumnsBeingChecked = new int[9][9][2];
    arrayOfArrayOfcurrentValuesFoundInGridsBeingChecked = new int[9][9][2];

    for (int row = 0; row < 9; row++) {

      for (int column = 0; column < 9; column++) {

        arrayOfArrayOfcurrentValuesFoundInRowsBeingChecked[row][column] = null;
        arrayOfArrayOfcurrentValuesFoundInColumnsBeingChecked[row][column] = null;
        arrayOfArrayOfcurrentValuesFoundInGridsBeingChecked[row][column] = null;

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

      sodokuUIManager = new SodokuUIManager(sodukuGrid);
      sodokuUIManager.setVisible(true);

    }

  }


  public void showProcessTakenToArriveAtResults() {

    System.out.println("1.) First go through every row to see what the rows are missing, and determine the potential values can be taken out to fit in these missing values...");
    sodokuUIManager.setWorkText("1.) First go through every row to see what the rows are missing, and determine the potential values can be taken out to fit in these missing values...");

    for (int rowNumber = 0; rowNumber < 9; rowNumber++) {

      //highlight this row as dark green to see signal it is being checked:
      System.out.println("1a.) Checking row "+rowNumber);
      sodokuUIManager.setEntireRowOfPanelsToColor(Color.YELLOW, rowNumber);

      try {

        Thread.sleep(1000);

      }
      catch (Exception e) {
      }

      for (int errorNumber = 0; errorNumber < listOfErrorsAndSuggestionsThatHaveBeenDetected.size(); errorNumber++) {

        if (rowNumber == listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getRowOfError()) {

          //highlight the potential errors in this row as red:
          try  {

            Thread.sleep(500);

          } catch (Exception e) {

          }

          sodokuUIManager.setPanelColor(Color.RED, rowNumber, listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getColumnOfError());
          sodokuUIManager.setWorkText("   Duplicate value of "+sodukuGrid[rowNumber][listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getColumnOfError()]+" at row: "+rowNumber+" , column: "+listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getColumnOfError());

        }

      }

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
