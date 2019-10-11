import java.util.ArrayList;

class SodokuRowValidator extends SodukuValidator implements Runnable {

  private int numberOfRowsToValidate;
  private int rowToStartValidatingAt;

  public SodokuRowValidator(int numberOfRowsToValidate, int rowToStartValidatingAt, int[][][] arrayOfArrayOfcurrentValuesFoundInRowsBeingChecked, int[][]sodukuGrid, ArrayList<ErrorAndSuggestionContainer> listOfErrorsAndSuggestionsThatHaveBeenDetected) {

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

        if ((arrayOfArrayOfcurrentValuesFoundInRowsBeingChecked[row][sodukuGrid[row][column] - 1]) == null) {

          //no error, this number has not yet appeared in this row, so now mark that it has appeared by storing its grid location:
          arrayOfArrayOfcurrentValuesFoundInRowsBeingChecked[row][sodukuGrid[row][column] - 1] = new int[2];
          arrayOfArrayOfcurrentValuesFoundInRowsBeingChecked[row][sodukuGrid[row][column] - 1][0] = row;
          arrayOfArrayOfcurrentValuesFoundInRowsBeingChecked[row][sodukuGrid[row][column] - 1][1] = column;

        }
        else {

          //this value has already appeared in this column, this is an error, store the location:
          System.out.println("Error in row detected, specifically at row: "+row+" column: "+column);

          /*

          -NOTE: error solution message is set to empty for now, this can only be
            set once I am done since I need to know what values are missing in order to suggest them as solutions

          */
          //Prevent adding of duplicates:
          ErrorAndSuggestionContainer errorAndSuggestionContainer = new ErrorAndSuggestionContainer(row, column, arrayOfArrayOfcurrentValuesFoundInRowsBeingChecked[row][sodukuGrid[row][column] - 1][0], arrayOfArrayOfcurrentValuesFoundInRowsBeingChecked[row][sodukuGrid[row][column] - 1][1], "");
          if (listOfErrorsAndSuggestionsThatHaveBeenDetected.contains(errorAndSuggestionContainer) == false) {

            listOfErrorsAndSuggestionsThatHaveBeenDetected.add(errorAndSuggestionContainer);

          }

        }

      }

    }

  }

}
