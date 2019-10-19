import java.util.Scanner;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.swing.Timer;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.awt.Color;

//Base class for soduke validator
class SodukuValidator implements ActionListener {

  protected SodokuUIManager sodokuUIManager;

  protected int[][] sodukuGrid;
  protected int[][][] arrayOfArrayOfcurrentValuesFoundInRowsBeingChecked; //Each inner array is a collection of the values found for the row, column, or sub grid, indicated by true if found or false if not found
  protected int[][][] arrayOfArrayOfcurrentValuesFoundInColumnsBeingChecked; //Each inner array is a collection of the values found for the row, column, or sub grid, indicated by true if found or false if not found
  protected int[][][] arrayOfArrayOfcurrentValuesFoundInGridsBeingChecked; //Each inner array is a collection of the values found for the row, column, or sub grid, indicated by true if found or false if not found

  protected ArrayList<ErrorAndSuggestionContainer> listOfErrorsAndSuggestionsThatHaveBeenDetected;

  Scanner reader;
  boolean programShouldEndSinceUserDidNotGiveAFileToReadFrom;
  boolean waitForTimerToExpire;

  private boolean steppingThroughRows;
  private boolean steppingThroughColumns;
  private boolean steppingThroughGrids;
  private boolean stepThroughJustStarted;

  private boolean needAnotherStepThroughToPortrayCheckingOfValueThePotentialErrorWasInConflictWith;
  private boolean needAnotherStepThroughToConfirmThatStepThroughIsCompleteAndShouldRefresh;

  private int currentRowInStepThrough;
  private int currentColumnInStepThrough;
  private int currentGridInStepThrough;
  private int startingRowNum;
  private int startingColumnNum;
  private int currentErrorInStepThrough;

  private ErrorAndSuggestionContainer errorContainer;

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

    ErrorAndSuggestionContainer errorContainer = listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber);
    int errorRow = errorContainer.getRowOfError();
    int errorColumn = errorContainer.getColumnOfError();

    int rowOfNumberThisValueConflictsWith = errorContainer.getRowOfNumberThisValueConflictsWith();
    int columnOfNumberThisValueConflictsWith = errorContainer.getColumnOfNumberThisValueConflictsWith();

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
      int solution = determineSolutionForCell(errorRow, errorColumn);
      System.out.println("There was an error at row "+(errorRow + 1)+" column "+(errorColumn + 1));
      System.out.println("Solution: Replace "+sodukuGrid[errorRow][errorColumn]+ " with " +solution);

      sodokuUIManager.setResultsText("There was an error at row "+(errorRow + 1)+" column "+(errorColumn + 1));
      sodokuUIManager.setResultsText("    Solution: Replace "+sodukuGrid[errorRow][errorColumn]+ " with " +solution);

      sodokuUIManager.setPanelColor(Color.RED, errorRow, errorColumn);

      errorContainer.setSuggestedValue(solution);
      errorContainer.setPotentialErrorWasDeterminedToBeTheActualError(true);

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

        errorContainer.setConflictingValueWasDeterminedToBeTheActualError(true);
        errorContainer.setSuggestedValue(solution);

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

    steppingThroughRows = true;
    steppingThroughColumns = false;
    steppingThroughGrids = false;
    stepThroughJustStarted = true;

    currentRowInStepThrough = 0;
    currentColumnInStepThrough = 0;
    currentGridInStepThrough = 0;
    currentErrorInStepThrough = 0;

    waitForTimerToExpire = false;

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
      sodokuUIManager.GetShowWorkButton().addActionListener(this);

    }

  }

  int determineWhatStartingRowShouldBeBasedOnGridNumber(int gridNumber) {

    if ((currentGridInStepThrough == 0) || (currentGridInStepThrough == 1) || (currentGridInStepThrough == 2)) {

      return 0;

    }
    else if ((currentGridInStepThrough == 3) || (currentGridInStepThrough == 4) || (currentGridInStepThrough == 5)) {

      return 3;

    }

    return 6;

  }

  int determineWhatStartingColumnShouldBeBasedOnColumnNumber(int columnNumber) {

    if ((currentGridInStepThrough == 0) || (currentGridInStepThrough == 3) || (currentGridInStepThrough == 6)) {

      return 0;

    }
    else if ((currentGridInStepThrough == 1) || (currentGridInStepThrough == 4) || (currentGridInStepThrough == 7)) {

      return 3;

    }

    return 6;

  }

  public void takeAStep() {

    //System.out.println("1.) First go through every row to see what the rows are missing, and determine the potential values can be taken out (marked in red, these are duplicates) to fit in these missing values...");
    //sodokuUIManager.setWorkText("1.) First go through every row to see what the rows are missing, and determine the potential values can be taken out to fit in these missing values...");
    if (steppingThroughRows) {


      if (stepThroughJustStarted) {

        stepThroughJustStarted = false;
        if ((currentRowInStepThrough == 0) && (currentColumnInStepThrough == 0)) {

          //just starting the step through the rows:
          System.out.println("1.) First go through every row to see what the rows are missing, and determine the potential values can be taken out (marked in red, these are duplicates) to fit in these missing values...");
          sodokuUIManager.setWorkText("1.) First go through every row to see what the rows are missing, and determine the potential values can be taken out to fit in these missing values...");

        }

        System.out.println("  Checking row: "+(currentRowInStepThrough +1));
        sodokuUIManager.setWorkText("  Checking row: "+(currentRowInStepThrough +1));

      }

      if (sodokuUIManager.getPanelColorOnGrid(currentRowInStepThrough, currentColumnInStepThrough)!= Color.RED) {

        sodokuUIManager.setPanelColor(Color.YELLOW, currentRowInStepThrough, currentColumnInStepThrough);

      }

      boolean errorDetected = false;

      for (int errorNumber = 0; errorNumber < listOfErrorsAndSuggestionsThatHaveBeenDetected.size(); errorNumber++) {

        if ((currentRowInStepThrough == listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getRowOfError()) && (currentColumnInStepThrough == listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getColumnOfError()) && (currentRowInStepThrough == listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getRowOfNumberThisValueConflictsWith())) {

          //highlight the potential errors in this row as red:
          sodokuUIManager.setPanelColor(Color.RED, currentRowInStepThrough, listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getColumnOfError());
          sodokuUIManager.setWorkText("       POTENTIAL ERROR: Duplicate value of "+sodukuGrid[currentRowInStepThrough][listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getColumnOfError()]+" at row: "+(currentRowInStepThrough + 1)+" , column: "+(listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getColumnOfError() + 1));

          errorDetected = true;
          sodokuUIManager.setWorkText("             Potential Error conflicts with value at row " +(listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getRowOfNumberThisValueConflictsWith() + 1)+" column "+(listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getColumnOfNumberThisValueConflictsWith() + 1)+ " which has also been marked as red for later");
          sodokuUIManager.setPanelColor(Color.RED, listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getRowOfNumberThisValueConflictsWith(), listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getColumnOfNumberThisValueConflictsWith());

          break;

        }

      }

      currentColumnInStepThrough++;

      if (currentColumnInStepThrough >= 9) {

        currentColumnInStepThrough = 0;
        currentRowInStepThrough++;
        sodokuUIManager.setEntireRowOfPanelsToColor(Color.LIGHT_GRAY, Color.RED, currentRowInStepThrough - 1);
        stepThroughJustStarted = true;

        if (currentRowInStepThrough >= 9) {

            //Already checked all rows, now it is time to check all columns:
            currentRowInStepThrough = 0;
            currentColumnInStepThrough = 0;

            steppingThroughRows = false;
            steppingThroughColumns = true;

        }
      }

    }
    else if (steppingThroughColumns) {

      if (stepThroughJustStarted) {

        stepThroughJustStarted = false;

        if ((currentRowInStepThrough == 0) && (currentColumnInStepThrough == 0)) {

          //just starting the step through the rows:
          System.out.println("2.) Now go through every column to see what the columns are missing, and determine the potential values can be taken out (marked in red, these are duplicates) to fit in these missing values...");
          sodokuUIManager.setWorkText("2.) Now go through every column to see what the columns are missing, and determine the potential values can be taken out (marked in red, these are duplicates) to fit in these missing values...");

        }

        System.out.println("  Checking column: "+(currentColumnInStepThrough + 1));
        sodokuUIManager.setWorkText(" Checking column: "+(currentColumnInStepThrough + 1));

      }


      if (sodokuUIManager.getPanelColorOnGrid(currentRowInStepThrough, currentColumnInStepThrough) != Color.RED) {

        sodokuUIManager.setPanelColor(Color.YELLOW, currentRowInStepThrough, currentColumnInStepThrough);

      }

      boolean errorDetected = false;

      for (int errorNumber = 0; errorNumber < listOfErrorsAndSuggestionsThatHaveBeenDetected.size(); errorNumber++) {

        if ((currentRowInStepThrough == listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getRowOfError()) && (currentColumnInStepThrough == listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getColumnOfError()) && (currentColumnInStepThrough == listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getColumnOfNumberThisValueConflictsWith())) {

          //highlight the potential errors in this row as red:
          sodokuUIManager.setPanelColor(Color.RED, currentRowInStepThrough, listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getColumnOfError());
          sodokuUIManager.setWorkText("       POTENTIAL ERROR: Duplicate value of "+sodukuGrid[currentRowInStepThrough][listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getColumnOfError()]+" at row: "+(currentRowInStepThrough + 1)+" , column: "+(listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getColumnOfError() + 1));

          errorDetected = true;
          sodokuUIManager.setWorkText("             Potential Error conflicts with value at row " +(listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getRowOfNumberThisValueConflictsWith() + 1)+" column "+(listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getColumnOfNumberThisValueConflictsWith() + 1)+ " which has also been marked as red for later");
          sodokuUIManager.setPanelColor(Color.RED, listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getRowOfNumberThisValueConflictsWith(), listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getColumnOfNumberThisValueConflictsWith());

          break;

        }

      }

      currentRowInStepThrough++;

      if (currentRowInStepThrough >= 9) {

        //Move to next column
        currentRowInStepThrough = 0;
        currentColumnInStepThrough++;
        sodokuUIManager.setEntireColumnOfPanelsToColor(Color.LIGHT_GRAY, Color.RED, currentColumnInStepThrough - 1);
        stepThroughJustStarted = true;

        if (currentColumnInStepThrough >= 9) {

            //Already checked all columns, now it is time to check all grids:
            currentRowInStepThrough = 0;
            currentColumnInStepThrough = 0;

            steppingThroughRows = false;
            steppingThroughColumns = false;
            steppingThroughGrids = true;

        }
      }

    }
    else if (steppingThroughGrids) {

      if (stepThroughJustStarted) {

        stepThroughJustStarted = false;

        if (currentGridInStepThrough > 0) {

          sodokuUIManager.setThreeByThreeGridToColor(Color.LIGHT_GRAY, Color.RED, currentGridInStepThrough - 1);

        }
        else {

          System.out.println("3.) Now go through every 3x3 grid to see what the 3x3 grids are missing, and determine the potential values can be taken out (marked in red, these are duplicates) to fit in these missing values...");
          sodokuUIManager.setWorkText("3.) Now go through every 3x3 grid to see what the 3x3 grids are missing, and determine the potential values can be taken out (marked in red, these are duplicates) to fit in these missing values...");

        }

        currentRowInStepThrough = determineWhatStartingRowShouldBeBasedOnGridNumber(currentGridInStepThrough);
        currentColumnInStepThrough = determineWhatStartingColumnShouldBeBasedOnColumnNumber(currentGridInStepThrough);

        startingRowNum = currentRowInStepThrough;
        startingColumnNum = currentColumnInStepThrough;

        //just starting the step through the grid:
        System.out.println("  Checking grid: "+(currentGridInStepThrough + 1));
        sodokuUIManager.setWorkText("  Checking grid: "+(currentGridInStepThrough + 1));

      }

      if (sodokuUIManager.getPanelColorOnGrid(currentRowInStepThrough, currentColumnInStepThrough) != Color.RED) {

        sodokuUIManager.setPanelColor(Color.YELLOW, currentRowInStepThrough, currentColumnInStepThrough);

      }

      boolean errorDetected = false;

      for (int errorNumber = 0; errorNumber < listOfErrorsAndSuggestionsThatHaveBeenDetected.size(); errorNumber++) {

        if ((currentRowInStepThrough == listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getRowOfError()) && (currentColumnInStepThrough == listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getColumnOfError()) && (currentColumnInStepThrough == listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getColumnOfNumberThisValueConflictsWith())) {

          //highlight the potential errors in this row as red:
          sodokuUIManager.setPanelColor(Color.RED, currentRowInStepThrough, listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getColumnOfError());
          sodokuUIManager.setWorkText("       POTENTIAL ERROR: Duplicate value of "+sodukuGrid[currentRowInStepThrough][listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getColumnOfError()]+" at row: "+(currentRowInStepThrough + 1)+" , column: "+(listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getColumnOfError() + 1));

          errorDetected = true;
          sodokuUIManager.setWorkText("             Potential Error conflicts with value at row " +(listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getRowOfNumberThisValueConflictsWith() + 1)+" column "+(listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getColumnOfNumberThisValueConflictsWith() + 1)+ " which has also been marked as red for later");
          sodokuUIManager.setPanelColor(Color.RED, listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getRowOfNumberThisValueConflictsWith(), listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getColumnOfNumberThisValueConflictsWith());

          break;

        }

      }

      boolean columnWasOutOfBounds = false;
      currentColumnInStepThrough++;
      if ((currentColumnInStepThrough - startingColumnNum) >= 3) {

        currentRowInStepThrough++;

        if ((currentGridInStepThrough == 0) || (currentGridInStepThrough == 3) || (currentGridInStepThrough == 6)) {

          columnWasOutOfBounds = true;
          currentColumnInStepThrough = 0;

        }
        else if ((currentGridInStepThrough == 1) || (currentGridInStepThrough == 4) || (currentGridInStepThrough == 7)) {

          columnWasOutOfBounds = true;
          currentColumnInStepThrough = 3;

        }
        else {

          columnWasOutOfBounds = true;
          currentColumnInStepThrough = 6;

        }

      }

      boolean rowWasOutOfBounds = false;

      if ((currentRowInStepThrough - startingRowNum) >= 3) {

        rowWasOutOfBounds = true;
        //currentRowInStepThrough = determineWhatStartingRowShouldBeBasedOnGridNumber(currentGridInStepThrough);

      }

      if (rowWasOutOfBounds && columnWasOutOfBounds) {

        //Done checking this grid, move to next grid:
        sodokuUIManager.setThreeByThreeGridToColor(Color.LIGHT_GRAY, Color.RED, currentGridInStepThrough);
        currentGridInStepThrough++;
        stepThroughJustStarted = true;

        if (currentGridInStepThrough >= 9) {

          steppingThroughGrids = false;

        }

      }

    }
    else if (needAnotherStepThroughToConfirmThatStepThroughIsCompleteAndShouldRefresh == false) {

      if (needAnotherStepThroughToPortrayCheckingOfValueThePotentialErrorWasInConflictWith == false) {

        if (stepThroughJustStarted) {

          if (currentErrorInStepThrough == 0) {

            System.out.println("4.) Now, reduce potential sources of errors into the actual source of the error, so that correct solutions may be suggested: ");
            sodokuUIManager.setWorkText("4.) Now, reduce potential sources of errors into the actual source of the error, so that correct solutions may be suggested: ");

          }
          else {

            sodokuUIManager.setEntireRowOfPanelsToColor(Color.LIGHT_GRAY, Color.RED, errorContainer.getRowOfError());
            sodokuUIManager.setEntireColumnOfPanelsToColor(Color.LIGHT_GRAY, Color.RED, errorContainer.getColumnOfError());
            sodokuUIManager.setThreeByThreeGridToColor(Color.LIGHT_GRAY, Color.RED, determineGrid(errorContainer.getRowOfError(), errorContainer.getColumnOfError()));

            sodokuUIManager.setEntireRowOfPanelsToColor(Color.LIGHT_GRAY, Color.RED, errorContainer.getRowOfNumberThisValueConflictsWith());
            sodokuUIManager.setEntireColumnOfPanelsToColor(Color.LIGHT_GRAY, Color.RED, errorContainer.getColumnOfNumberThisValueConflictsWith());
            sodokuUIManager.setThreeByThreeGridToColor(Color.LIGHT_GRAY, Color.RED, determineGrid(errorContainer.getRowOfNumberThisValueConflictsWith(), errorContainer.getColumnOfNumberThisValueConflictsWith()));

          }

        }

        errorContainer = listOfErrorsAndSuggestionsThatHaveBeenDetected.get(currentErrorInStepThrough);
        System.out.println("    Check the row, column, and grid of the potential error at row "+(errorContainer.getRowOfError() + 1)+", column "+(errorContainer.getColumnOfError() + 1));
        System.out.println("        For this potential error to be the actual source of the error, then all 3 of these things must be missing the same item.");

        sodokuUIManager.setEntireRowOfPanelsToColor(Color.YELLOW, Color.RED, errorContainer.getRowOfError());
        sodokuUIManager.setEntireColumnOfPanelsToColor(Color.YELLOW, Color.RED, errorContainer.getColumnOfError());
        sodokuUIManager.setThreeByThreeGridToColor(Color.YELLOW, Color.RED, determineGrid(errorContainer.getRowOfError(), errorContainer.getColumnOfError()));

        sodokuUIManager.setWorkText("     Check the row, column, and grid of the potential error at row "+(errorContainer.getRowOfError() + 1)+", column "+(errorContainer.getColumnOfError() + 1));
        sodokuUIManager.setWorkText("        For this potential error to be the actual source of the error, then all 3 of these things must be missing the same item.");

        if (errorContainer.getPotentialErrorWasDeterminedToBeTheActualError()) {

          System.out.println("        The row, column, and grid of this potential error are all mising the same thing, so I declare this grid as an actual error, and suggest the item that all of these things are missing as the solution, leading to a result of:");
          System.out.println("        RESULTS: There was an error at row"+(errorContainer.getRowOfError() + 1)+" column "+(errorContainer.getColumnOfError() + 1));
          System.out.println("        RESULTS: Solution: Replace "+sodukuGrid[errorContainer.getRowOfError()][errorContainer.getColumnOfError()]+" with "+errorContainer.getSuggestedValue());

          sodokuUIManager.setWorkText("        The row, column, and grid of this potential error are all mising the same thing, so I declare this grid as an actual error, and suggest the item that all of these things are missing as the solution, leading to a result of:");
          sodokuUIManager.setWorkText("        RESULTS: There was an error at row"+(errorContainer.getRowOfError() + 1)+" column "+(errorContainer.getColumnOfError() + 1));
          sodokuUIManager.setWorkText("        RESULTS: Solution: Replace "+sodukuGrid[errorContainer.getRowOfError()][errorContainer.getColumnOfError()]+" with "+errorContainer.getSuggestedValue());


        }
        else {

          System.out.println("        But the row, column, and grid of this potential error are not all mising the same thing, so I declare this grid as not being an error");
          sodokuUIManager.setPanelColor(Color.LIGHT_GRAY, errorContainer.getRowOfError(), errorContainer.getColumnOfError());
          sodokuUIManager.setWorkText("        But the row, column, and grid of this potential error are not all mising the same thing, so I declare this grid as not being an error");
          sodokuUIManager.setWorkText("        This means then that the value this potential error conflict with needs to be looked at to see if its the actual error.");

          needAnotherStepThroughToPortrayCheckingOfValueThePotentialErrorWasInConflictWith = true;
          return;

        }

      }
      else {

        needAnotherStepThroughToPortrayCheckingOfValueThePotentialErrorWasInConflictWith = false;

        sodokuUIManager.setEntireRowOfPanelsToColor(Color.LIGHT_GRAY, Color.RED, errorContainer.getRowOfError());
        sodokuUIManager.setEntireColumnOfPanelsToColor(Color.LIGHT_GRAY, Color.RED, errorContainer.getColumnOfError());
        sodokuUIManager.setThreeByThreeGridToColor(Color.LIGHT_GRAY, Color.RED, determineGrid(errorContainer.getRowOfError(), errorContainer.getColumnOfError()));

        sodokuUIManager.setWorkText("        So, I go to row "+(errorContainer.getRowOfNumberThisValueConflictsWith() + 1)+", column "+(errorContainer.getColumnOfError() + 1));
        sodokuUIManager.setWorkText("        And repeat the same process, check the grid, row, and column this number that conflicts with the potential error is in, which must all be missing the same thing for this number to be the error");

        sodokuUIManager.setEntireRowOfPanelsToColor(Color.YELLOW, Color.RED, errorContainer.getRowOfNumberThisValueConflictsWith());
        sodokuUIManager.setEntireColumnOfPanelsToColor(Color.YELLOW, Color.RED, errorContainer.getColumnOfNumberThisValueConflictsWith());
        sodokuUIManager.setThreeByThreeGridToColor(Color.YELLOW, Color.RED, determineGrid(errorContainer.getRowOfNumberThisValueConflictsWith(), errorContainer.getColumnOfNumberThisValueConflictsWith()));

        if (errorContainer.getConflictingValueWasDeterminedToBeTheActualError()) {

          System.out.println("        The row, column, and grid of this number are all mising the same thing, so I declare this grid as an actual error, and suggest the item that all of these things are missing as the solution, leading to a result of:");
          System.out.println("        RESULTS: There was an error at row"+(errorContainer.getRowOfNumberThisValueConflictsWith() + 1)+" column "+(errorContainer.getColumnOfNumberThisValueConflictsWith() + 1));
          System.out.println("        RESULTS: Solution: Replace "+sodukuGrid[errorContainer.getRowOfNumberThisValueConflictsWith()][errorContainer.getColumnOfNumberThisValueConflictsWith()]+" with "+errorContainer.getSuggestedValue());

          sodokuUIManager.setWorkText("        The row, column, and grid of this number are all mising the same thing, so I declare this grid as an actual error, and suggest the item that all of these things are missing as the solution, leading to a result of:");
          sodokuUIManager.setWorkText("        RESULTS: There was an error at row"+(errorContainer.getRowOfNumberThisValueConflictsWith() + 1)+" column "+(errorContainer.getColumnOfNumberThisValueConflictsWith() + 1));
          sodokuUIManager.setWorkText("        RESULTS: Solution: Replace "+sodukuGrid[errorContainer.getRowOfNumberThisValueConflictsWith()][errorContainer.getColumnOfNumberThisValueConflictsWith()]+" with "+errorContainer.getSuggestedValue());

        }

      }

      stepThroughJustStarted = true;
      currentErrorInStepThrough++;
      if (currentErrorInStepThrough >= listOfErrorsAndSuggestionsThatHaveBeenDetected.size()) {

        needAnotherStepThroughToConfirmThatStepThroughIsCompleteAndShouldRefresh = true;

      }

    }
    else {

      needAnotherStepThroughToConfirmThatStepThroughIsCompleteAndShouldRefresh = false;
      steppingThroughRows = true;
      steppingThroughColumns = false;
      steppingThroughGrids = false;

      currentRowInStepThrough = 0;
      currentGridInStepThrough = 0;
      currentColumnInStepThrough = 0;
      currentErrorInStepThrough = 0;

      System.out.println("That is all that is done to check the sodoku grid.");
      sodokuUIManager.setWorkText("That is all that is done to check the sodoku grid.");
      sodokuUIManager.setWorkText("The things tagged with RESULTS end up being displayed in the results panel.");
      sodokuUIManager.setEntireGridOfPanelsToColor(Color.LIGHT_GRAY, Color.RED);

    }
    //Task task = null;
    //Timer timer;
    //TimerTask timerTask;

    //int rowNumber = 0;
    //int column = 0;
    //int grid = 0;



    /*timer = new Timer(1000, new ActionListener() {

      int rowNumber = 0;

      public void actionPerformed(ActionEvent e) {

        if (rowNumber < 9) {

          sodokuUIManager.setEntireRowOfPanelsToColor(Color.LIGHT_GRAY, Color.RED, rowNumber);
          System.out.println("1a.) Checking row "+rowNumber);
          sodokuUIManager.setWorkText("   1a.) Checking row" + rowNumber);
          sodokuUIManager.setEntireRowOfPanelsToColor(Color.YELLOW, null, rowNumber);

          rowNumber++;

        }
        else {

          ((Timer)e.getSource()).stop();

        }

      }

    });

    timer.start(); */

    /*for (int rowNumber = 0; rowNumber < 9; rowNumber++) {

      task = new Task();
      timer = new Timer(1000, task);
      timer.setRepeats(false);
      //timerTask = task;
      //timer.schedule(task, 1000);
      timer.start();

      //highlight this row as dark green to see signal it is being checked:
      System.out.println("1a.) Checking row "+rowNumber);
      sodokuUIManager.setWorkText("   1a.) Checking row" + rowNumber);
      sodokuUIManager.setEntireRowOfPanelsToColor(Color.YELLOW, null, rowNumber);

      while (true) {

        //System.out.println("Timer is on? "+ timer.isRunning());
        if (timer.isRunning() == false) {

          for (int errorNumber = 0; errorNumber < listOfErrorsAndSuggestionsThatHaveBeenDetected.size(); errorNumber++) {

            if (rowNumber == listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getRowOfError()) {

              //highlight the potential errors in this row as red:
              sodokuUIManager.setPanelColor(Color.RED, rowNumber, listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getColumnOfError());
              sodokuUIManager.setWorkText("       Duplicate value of "+sodukuGrid[rowNumber][listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getColumnOfError()]+" at row: "+rowNumber+" , column: "+listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getColumnOfError());

              sodokuUIManager.setPanelColor(Color.RED, listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getRowOfNumberThisValueConflictsWith(), listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getColumnOfNumberThisValueConflictsWith());

            }

          }

          //task.setTimerExpired(false);
          //sodokuUIManager.setEntireRowOfPanelsToColor(Color.LIGHT_GRAY, Color.RED, rowNumber);
          waitForTimerToExpire = true;
          break;

        }
        //System.out.println("Wait for timer?: "+waitForTimerToExpire);

        sodokuUIManager.setEntireRowOfPanelsToColor(Color.LIGHT_GRAY, Color.RED, rowNumber);

      }

    } */

    /*sodokuUIManager.setWorkText("2.) Go through every column to see what the columns are missing, and determine the potential values (marked in red, these are duplicates) that can be taken out to fit in these missing values...");

    for (int column = 0; column < 9; column++) {

      //highlight this column as yellow to see signal it is being checked:
      System.out.println("2a.) Checking column "+column);
      sodokuUIManager.setWorkText("   2a.) Checking column" + column);
      sodokuUIManager.setEntireColumnOfPanelsToColor(Color.YELLOW, null, column);

      for (int errorNumber = 0; errorNumber < listOfErrorsAndSuggestionsThatHaveBeenDetected.size(); errorNumber++) {

        if (column == listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getColumnOfError()) {

          //highlight the potential errors in this row as red:
          sodokuUIManager.setPanelColor(Color.RED, listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getRowOfError(), column);
          sodokuUIManager.setWorkText("       Duplicate value of "+sodukuGrid[listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getRowOfError()][column]+" at row: "+listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getRowOfError()+" , column: "+column);

          sodokuUIManager.setPanelColor(Color.RED, listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getRowOfNumberThisValueConflictsWith(), listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getColumnOfNumberThisValueConflictsWith());

        }

      }

      sodokuUIManager.setEntireColumnOfPanelsToColor(Color.LIGHT_GRAY, Color.RED, column);

    } */

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

  public void actionPerformed(ActionEvent e) {

    //do button action here...
    takeAStep();
    //System.exit(0);

  }

}
