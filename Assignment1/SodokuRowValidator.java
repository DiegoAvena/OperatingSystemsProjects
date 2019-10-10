import java.util.ArrayList;

class SodokuRowValidator extends SodukuValidator implements Runnable {

  private int numberOfRowsToValidate;
  private int rowToStartValidatingAt;

  public SodokuRowValidator(int numberOfRowsToValidate, int rowToStartValidatingAt, boolean[][] arrayOfArrayOfcurrentValuesFoundInRowsBeingChecked, int[][]sodukuGrid, ArrayList<ErrorAndSuggestionContainer> listOfErrorsAndSuggestionsThatHaveBeenDetected) {

    this.numberOfRowsToValidate = numberOfRowsToValidate;
    this.rowToStartValidatingAt = rowToStartValidatingAt;

    this.arrayOfArrayOfcurrentValuesFoundInRowsBeingChecked = arrayOfArrayOfcurrentValuesFoundInRowsBeingChecked;
    this.sodukuGrid = sodukuGrid;

    this.listOfErrorsAndSuggestionsThatHaveBeenDetected = listOfErrorsAndSuggestionsThatHaveBeenDetected;

  }

  public SodokuRowValidator() {

    numberOfRowsToValidate = 9;
    rowToStartValidatingAt = 0;

  }

  public void run() {

    validateRows();

  }

  private void validateRows() {

    for (int row = rowToStartValidatingAt; row < (rowToStartValidatingAt + numberOfRowsToValidate); row++) {

      for (int column = 0; column < 9; column++) {

        if ((arrayOfArrayOfcurrentValuesFoundInRowsBeingChecked[row][sodukuGrid[row][column] - 1]) != true) {

          //System.out.println("Checking column: "+(column + 1) + "row: "+(row + 1));
          //no error, this number has not yet appeared in this row, so now mark that it has appeared and move to next column in this row:
          arrayOfArrayOfcurrentValuesFoundInRowsBeingChecked[row][sodukuGrid[row][column] - 1] = true;

        }
        else {

          //this value has already appeared in this column, this is an error, store the location:
          System.out.println("Error in row detected, specifically at row: "+row+" column: "+column);

          /*

          -NOTE: error solution message is set to empty for now, this can only be
            set once I am done since I need to know what values are missing in order to suggest them as solutions

          */
          //Prevent adding of duplicates:
          ErrorAndSuggestionContainer errorAndSuggestionContainer = new ErrorAndSuggestionContainer(row, column, "");
          if (listOfErrorsAndSuggestionsThatHaveBeenDetected.contains(errorAndSuggestionContainer) == false) {

            listOfErrorsAndSuggestionsThatHaveBeenDetected.add(errorAndSuggestionContainer);

          }

        }

      }

    }

  }

}
