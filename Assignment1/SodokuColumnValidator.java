import java.util.ArrayList;

class SodokuColumnValidator extends SodukuValidator implements Runnable {

  private int numberOfColumnsToValidate;
  private int columnToStartValidationAt;

  void ValidateColumns() {

    for (int column = columnToStartValidationAt; column < (columnToStartValidationAt + numberOfColumnsToValidate); column++) {

      //check the ith column:
      for (int row = 0; row < 9; row++) {

        if (arrayOfArrayOfcurrentValuesFoundInColumnsBeingChecked[column][sodukuGrid[row][column] - 1] == null) {

          //This value has not yet appeared in this column, so no error to report, store its location in the grid:
          arrayOfArrayOfcurrentValuesFoundInColumnsBeingChecked[column][sodukuGrid[row][column] - 1] = new int[2];
          arrayOfArrayOfcurrentValuesFoundInColumnsBeingChecked[column][sodukuGrid[row][column] - 1][0] = row;
          arrayOfArrayOfcurrentValuesFoundInColumnsBeingChecked[column][sodukuGrid[row][column] - 1][1] = column;

        }
        else {

          //this value has already appeared in this column, this is an error, store the location:
          System.out.println("Error in column detected, specifically at row: "+row+" column: "+column);
          /*

          -NOTE: error solution message is set to empty for now, this can only be
            set once I am done since I need to know what values are missing in order to suggest them as solutions

          */
          ErrorAndSuggestionContainer errorAndSuggestionContainer = new ErrorAndSuggestionContainer(row, column, arrayOfArrayOfcurrentValuesFoundInColumnsBeingChecked[column][sodukuGrid[row][column] - 1][0], arrayOfArrayOfcurrentValuesFoundInColumnsBeingChecked[column][sodukuGrid[row][column] - 1][1], "");
          listOfErrorsAndSuggestionsThatHaveBeenDetected.add(errorAndSuggestionContainer);

        }

      }

    }

  }

  public SodokuColumnValidator() {

    numberOfColumnsToValidate = 9;
    columnToStartValidationAt = 0;

  }

  public SodokuColumnValidator(int numberOfColumnsToValidate, int columnToStartValidationAt, int[][][] arrayOfArrayOfcurrentValuesFoundInColumnsBeingChecked, int[][]sodukuGrid, ArrayList<ErrorAndSuggestionContainer> listOfErrorsAndSuggestionsThatHaveBeenDetected) {

    this.numberOfColumnsToValidate = numberOfColumnsToValidate;
    this.columnToStartValidationAt = columnToStartValidationAt;

    this.arrayOfArrayOfcurrentValuesFoundInColumnsBeingChecked = arrayOfArrayOfcurrentValuesFoundInColumnsBeingChecked;
    this.sodukuGrid = sodukuGrid;
    this.listOfErrorsAndSuggestionsThatHaveBeenDetected = listOfErrorsAndSuggestionsThatHaveBeenDetected;

  }

  public void run() {

    ValidateColumns();


  }

}
