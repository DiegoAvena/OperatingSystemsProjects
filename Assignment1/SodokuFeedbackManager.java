import java.awt.Color;

import java.util.ArrayList;

class SodokuFeedbackManager {

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

  private SodokuUIManager sodokuUIManager;
  private ArrayList<ErrorAndSuggestionContainer> listOfErrorsAndSuggestionsThatHaveBeenDetected;
  private int[][] sodukuGrid;

  public SodokuFeedbackManager(SodokuUIManager sodokuUIManager, ArrayList<ErrorAndSuggestionContainer> listOfErrorsAndSuggestionsThatHaveBeenDetected, int[][] sodukuGrid) {

    this.sodukuGrid = sodukuGrid;
    this.listOfErrorsAndSuggestionsThatHaveBeenDetected = listOfErrorsAndSuggestionsThatHaveBeenDetected;
    this.sodokuUIManager = sodokuUIManager;

    steppingThroughRows = true;
    steppingThroughColumns = false;
    steppingThroughGrids = false;
    stepThroughJustStarted = true;

    currentRowInStepThrough = 0;
    currentColumnInStepThrough = 0;
    currentGridInStepThrough = 0;
    currentErrorInStepThrough = 0;

  }

  private int determineWhatStartingRowShouldBeBasedOnGridNumber(int gridNumber) {

    if ((currentGridInStepThrough == 0) || (currentGridInStepThrough == 1) || (currentGridInStepThrough == 2)) {

      return 0;

    }
    else if ((currentGridInStepThrough == 3) || (currentGridInStepThrough == 4) || (currentGridInStepThrough == 5)) {

      return 3;

    }

    return 6;

  }

  private int determineWhatStartingColumnShouldBeBasedOnColumnNumber(int columnNumber) {

    if ((currentGridInStepThrough == 0) || (currentGridInStepThrough == 3) || (currentGridInStepThrough == 6)) {

      return 0;

    }
    else if ((currentGridInStepThrough == 1) || (currentGridInStepThrough == 4) || (currentGridInStepThrough == 7)) {

      return 3;

    }

    return 6;

  }

  private int determineGrid(int errorRow, int errorColumn) {

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

  public void takeAStep() {

    if (steppingThroughRows) {


      if (stepThroughJustStarted) {

        stepThroughJustStarted = false;
        if ((currentRowInStepThrough == 0) && (currentColumnInStepThrough == 0)) {

          sodokuUIManager.clearWorkText();

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

          System.out.println("       POTENTIAL ERROR: Duplicate value of "+sodukuGrid[currentRowInStepThrough][listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getColumnOfError()]+" at row: "+(currentRowInStepThrough + 1)+" , column: "+(listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getColumnOfError() + 1));
          System.out.println("             Potential Error conflicts with value at row " +(listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getRowOfNumberThisValueConflictsWith() + 1)+" column "+(listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getColumnOfNumberThisValueConflictsWith() + 1)+ " which has also been marked as red for later");

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
        sodokuUIManager.setWorkText("  Checking column: "+(currentColumnInStepThrough + 1));

      }


      if (sodokuUIManager.getPanelColorOnGrid(currentRowInStepThrough, currentColumnInStepThrough) != Color.RED) {

        sodokuUIManager.setPanelColor(Color.YELLOW, currentRowInStepThrough, currentColumnInStepThrough);

      }

      boolean errorDetected = false;

      for (int errorNumber = 0; errorNumber < listOfErrorsAndSuggestionsThatHaveBeenDetected.size(); errorNumber++) {

        if ((currentRowInStepThrough == listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getRowOfError()) && (currentColumnInStepThrough == listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getColumnOfError()) && (currentColumnInStepThrough == listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getColumnOfNumberThisValueConflictsWith())) {

          System.out.println("       POTENTIAL ERROR: Duplicate value of "+sodukuGrid[currentRowInStepThrough][listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getColumnOfError()]+" at row: "+(currentRowInStepThrough + 1)+" , column: "+(listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getColumnOfError() + 1));
          System.out.println("             Potential Error conflicts with value at row " +(listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getRowOfNumberThisValueConflictsWith() + 1)+" column "+(listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getColumnOfNumberThisValueConflictsWith() + 1)+ " which has also been marked as red for later");

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

        if ((currentRowInStepThrough == listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getRowOfError()) && (currentColumnInStepThrough == listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getColumnOfError()) && (currentGridInStepThrough == listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getErrorGridNumber())) {

          System.out.println("       POTENTIAL ERROR: Duplicate value of "+sodukuGrid[currentRowInStepThrough][listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getColumnOfError()]+" at row: "+(currentRowInStepThrough + 1)+" , column: "+(listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getColumnOfError() + 1));
          System.out.println("             Potential Error conflicts with value at row " +(listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getRowOfNumberThisValueConflictsWith() + 1)+" column "+(listOfErrorsAndSuggestionsThatHaveBeenDetected.get(errorNumber).getColumnOfNumberThisValueConflictsWith() + 1)+ " which has also been marked as red for later");

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
    else if ((needAnotherStepThroughToConfirmThatStepThroughIsCompleteAndShouldRefresh == false) && (listOfErrorsAndSuggestionsThatHaveBeenDetected.size() > 0)) {

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

        System.out.println("");
        System.out.println("    Check the row, column, and grid of the potential error at row "+(errorContainer.getRowOfError() + 1)+", column "+(errorContainer.getColumnOfError() + 1));
        System.out.println("        For this potential error to be the actual source of the error, then all 3 of these things must be missing the same item.");

        sodokuUIManager.setEntireRowOfPanelsToColor(Color.YELLOW, Color.RED, errorContainer.getRowOfError());
        sodokuUIManager.setEntireColumnOfPanelsToColor(Color.YELLOW, Color.RED, errorContainer.getColumnOfError());
        sodokuUIManager.setThreeByThreeGridToColor(Color.YELLOW, Color.RED, determineGrid(errorContainer.getRowOfError(), errorContainer.getColumnOfError()));

        sodokuUIManager.setWorkText("");
        sodokuUIManager.setWorkText("     Check the row, column, and grid of the potential error at row "+(errorContainer.getRowOfError() + 1)+", column "+(errorContainer.getColumnOfError() + 1));
        sodokuUIManager.setWorkText("        For this potential error to be the actual source of the error, then all 3 of these things must be missing the same item.");

        if (errorContainer.getPotentialErrorWasDeterminedToBeTheActualError() && (errorContainer.getSuggestedValue() != 0)) {

          System.out.println("        I can now say that the conflicted value at row "+(errorContainer.getRowOfNumberThisValueConflictsWith() + 1)+" column "+(errorContainer.getColumnOfNumberThisValueConflictsWith() + 1) + " is clean, signaled by taking its redness away");
          System.out.println("        Since the row, column, and grid of this potential error are all mising the same thing, so I declare this grid as an actual error, and suggest the item that all of these things are missing as the solution, leading to a result of:");
          System.out.println("        RESULTS: There was an error at row "+(errorContainer.getRowOfError() + 1)+" column "+(errorContainer.getColumnOfError() + 1));
          System.out.println("        RESULTS: Solution: Replace "+sodukuGrid[errorContainer.getRowOfError()][errorContainer.getColumnOfError()]+" with "+errorContainer.getSuggestedValue());

          sodokuUIManager.setPanelColor(Color.LIGHT_GRAY, errorContainer.getRowOfNumberThisValueConflictsWith(), errorContainer.getColumnOfNumberThisValueConflictsWith());

          sodokuUIManager.setWorkText("        I can now say that the conflicted value at row "+(errorContainer.getRowOfNumberThisValueConflictsWith() + 1)+" column "+(errorContainer.getColumnOfNumberThisValueConflictsWith() + 1) + " is clean, signaled by taking its redness away");
          sodokuUIManager.setWorkText("        Since, the row, column, and grid of this potential error are all missing the same thing, so I declare this grid as an actual error, and suggest the item that all of these things are missing as the solution, leading to a result of:");
          sodokuUIManager.setWorkText("        RESULTS: There was an error at row "+(errorContainer.getRowOfError() + 1)+" column "+(errorContainer.getColumnOfError() + 1));
          sodokuUIManager.setWorkText("        RESULTS: Solution: Replace "+sodukuGrid[errorContainer.getRowOfError()][errorContainer.getColumnOfError()]+" with "+errorContainer.getSuggestedValue());

        }
        else if (errorContainer.getSuggestedValue() != 0) {

          System.out.println("        But the row, column, and grid of this potential error are not all missing the same thing, so I declare this grid as not being an error, signaled by taking away its redness");
          System.out.println("        This means then that the value this potential error conflicts with needs to be looked at to see if its the actual error.");

          sodokuUIManager.setPanelColor(Color.LIGHT_GRAY, errorContainer.getRowOfError(), errorContainer.getColumnOfError());
          sodokuUIManager.setWorkText("        But the row, column, and grid of this potential error are not all missing the same thing, so I declare this grid as not being an error, signaled by taking away its redness");
          sodokuUIManager.setWorkText("        This means then that the value this potential error conflicts with needs to be looked at to see if its the actual error.");

          needAnotherStepThroughToPortrayCheckingOfValueThePotentialErrorWasInConflictWith = true;
          return;

        }
        else if (errorContainer.getPotentialErrorWasDeterminedToBeTheActualError()) {

          //System.out.println("        But the row, column, and grid of this potential error are not all missing the same thing, so I declare this grid as not being an error")

          System.out.println("        This is a confirmed error, but the suggestion for this potential error has been reported by some other spot on the grid, so nothing to report here.");
          sodokuUIManager.setWorkText("        This is a confirmed error, but the suggestion for this potential error has been reported by some other spot on the grid, so nothing to report here.");
          sodokuUIManager.setWorkText("        Except that I can declare the conflicted value at row "+(errorContainer.getRowOfNumberThisValueConflictsWith() + 1)+ " column "+(errorContainer.getColumnOfNumberThisValueConflictsWith() + 1) +" as clean, signaled by taking its redness away");

          sodokuUIManager.setPanelColor(Color.LIGHT_GRAY, errorContainer.getRowOfNumberThisValueConflictsWith(), errorContainer.getColumnOfNumberThisValueConflictsWith());

        }
        else {

          //the suggested value is 0 because the error in conflict was the actual error, but was already given a solution by a previous error container:
          System.out.println("        But the row, column, and grid of this potential error are not all missing the same thing, so I declare this grid as not being an error, signaled by taking its redness away");
          System.out.println("        This means then that the value this potential error conflicts with needs to be looked at to see if its the actual error.");

          sodokuUIManager.setPanelColor(Color.LIGHT_GRAY, errorContainer.getRowOfError(), errorContainer.getColumnOfError());
          sodokuUIManager.setWorkText("        But the row, column, and grid of this potential error are not all missing the same thing, so I declare this grid as not being an error, signaled by taking its redness away");
          sodokuUIManager.setWorkText("        This means then that the value this potential error conflicts with needs to be looked at to see if its the actual error.");

          needAnotherStepThroughToPortrayCheckingOfValueThePotentialErrorWasInConflictWith = true;
          return;

        }

      }
      else {

        needAnotherStepThroughToPortrayCheckingOfValueThePotentialErrorWasInConflictWith = false;

        System.out.println("        So, I go to row "+(errorContainer.getRowOfNumberThisValueConflictsWith() + 1)+", column "+(errorContainer.getColumnOfNumberThisValueConflictsWith() + 1));
        System.out.println("        And repeat the same process, check the grid, row, and column this number that conflicts with the potential error is in, which must all be missing the same thing for this number to be the error");

        sodokuUIManager.setEntireRowOfPanelsToColor(Color.LIGHT_GRAY, Color.RED, errorContainer.getRowOfError());
        sodokuUIManager.setEntireColumnOfPanelsToColor(Color.LIGHT_GRAY, Color.RED, errorContainer.getColumnOfError());
        sodokuUIManager.setThreeByThreeGridToColor(Color.LIGHT_GRAY, Color.RED, determineGrid(errorContainer.getRowOfError(), errorContainer.getColumnOfError()));

        sodokuUIManager.setWorkText("        So, I go to row "+(errorContainer.getRowOfNumberThisValueConflictsWith() + 1)+", column "+(errorContainer.getColumnOfNumberThisValueConflictsWith() + 1));
        sodokuUIManager.setWorkText("        And repeat the same process, check the grid, row, and column this number that conflicts with the potential error is in, which must all be missing the same thing for this number to be the error");

        sodokuUIManager.setEntireRowOfPanelsToColor(Color.YELLOW, Color.RED, errorContainer.getRowOfNumberThisValueConflictsWith());
        sodokuUIManager.setEntireColumnOfPanelsToColor(Color.YELLOW, Color.RED, errorContainer.getColumnOfNumberThisValueConflictsWith());
        sodokuUIManager.setThreeByThreeGridToColor(Color.YELLOW, Color.RED, determineGrid(errorContainer.getRowOfNumberThisValueConflictsWith(), errorContainer.getColumnOfNumberThisValueConflictsWith()));

        if (errorContainer.getConflictingValueWasDeterminedToBeTheActualError() && (errorContainer.getSuggestedValue() != 0)) {

          System.out.println("        The row, column, and grid of this number are all missing the same thing, so I declare this grid as an actual error, and suggest the item that all of these things are missing as the solution, leading to a result of:");
          System.out.println("        RESULTS: There was an error at row "+(errorContainer.getRowOfNumberThisValueConflictsWith() + 1)+" column "+(errorContainer.getColumnOfNumberThisValueConflictsWith() + 1));
          System.out.println("        RESULTS: Solution: Replace "+sodukuGrid[errorContainer.getRowOfNumberThisValueConflictsWith()][errorContainer.getColumnOfNumberThisValueConflictsWith()]+" with "+errorContainer.getSuggestedValue());

          sodokuUIManager.setWorkText("        The row, column, and grid of this number are all missing the same thing, so I declare this grid as an actual error, and suggest the item that all of these things are missing as the solution, leading to a result of:");
          sodokuUIManager.setWorkText("        RESULTS: There was an error at row "+(errorContainer.getRowOfNumberThisValueConflictsWith() + 1)+" column "+(errorContainer.getColumnOfNumberThisValueConflictsWith() + 1));
          sodokuUIManager.setWorkText("        RESULTS: Solution: Replace "+sodukuGrid[errorContainer.getRowOfNumberThisValueConflictsWith()][errorContainer.getColumnOfNumberThisValueConflictsWith()]+" with "+errorContainer.getSuggestedValue());

        }
        else if (errorContainer.getSuggestedValue() != 0){

          System.out.println("        But the row, column, and grid of this number are also not all missing the same thing, so I declare this grid as not being an error as well by removing its red color");
          System.out.println("        This means then that the source of this error lies somewhere else, so I continue as before.");

          sodokuUIManager.setWorkText("        But the row, column, and grid of this number are also not all missing the same thing, so I declare this grid as not being an error as well by removing its red color");
          sodokuUIManager.setWorkText("        This means then that the source of this error lies somewhere else, so I continue as before.");
          sodokuUIManager.setPanelColor(Color.LIGHT_GRAY, errorContainer.getRowOfNumberThisValueConflictsWith(), errorContainer.getColumnOfNumberThisValueConflictsWith());

        }
        else {

          //The suggested value is 0 because this conflicted value is in fact an error, but it has already been confirmed so this is a duplicate error container:
          System.out.println("        The row, column, and grid of this number are all missing the same thing, so this grid is an actual error, but its suggestion has already been reported by another spot, so nothing new to report here.");
          sodokuUIManager.setWorkText("        The row, column, and grid of this number are all missing the same thing, so this grid is an actual error, but its suggestion has already been reported by another spot, so nothing new to report here.");

        }

      }

      stepThroughJustStarted = true;
      currentErrorInStepThrough++;
      if (currentErrorInStepThrough >= listOfErrorsAndSuggestionsThatHaveBeenDetected.size()) {

        needAnotherStepThroughToConfirmThatStepThroughIsCompleteAndShouldRefresh = true;

      }

    }
    else {

      resetStepThrough();

      System.out.println("That is all that is done to check the sodoku grid.");
      System.out.println("The things tagged with RESULTS end up being displayed in the results panel.");
      System.out.println("If there are no things tagged with results, the sodoku grid was a valid sodoku grid.");

      sodokuUIManager.setWorkText("That is all that is done to check the sodoku grid.");
      sodokuUIManager.setWorkText("The things tagged with RESULTS end up being displayed in the results panel.");
      sodokuUIManager.setWorkText("If there are no things tagged with results, the sodoku grid was a valid sodoku grid.");
      sodokuUIManager.setEntireGridOfPanelsToColor(Color.LIGHT_GRAY, Color.RED);

    }

  }

  private void resetStepThrough() {

    needAnotherStepThroughToConfirmThatStepThroughIsCompleteAndShouldRefresh = false;
    steppingThroughRows = true;
    steppingThroughColumns = false;
    steppingThroughGrids = false;

    currentRowInStepThrough = 0;
    currentGridInStepThrough = 0;
    currentColumnInStepThrough = 0;
    currentErrorInStepThrough = 0;

  }



}
