import java.util.ArrayList;

class SodokuColumnValidator extends SodukuValidator implements Runnable {

  private int numberOfColumnsToValidate;
  private int columnToStartValidationAt;

  void ValidateColumns() {

    //System.out.println("Starting column check at column: "+columnToStartValidationAt);
    for (int column = columnToStartValidationAt; column < (columnToStartValidationAt + numberOfColumnsToValidate); column++) {

      //check the ith column:
      for (int row = 0; row < 9; row++) {

        //System.out.println("Checking column: "+(column + 1) + "row: "+(row + 1));
        if (arrayOfArrayOfcurrentValuesFoundInColumnsBeingChecked[column][sodukuGrid[row][column] - 1] != true) {

          //This value has not yet appeared in this column, so no error to report:
          arrayOfArrayOfcurrentValuesFoundInColumnsBeingChecked[column][sodukuGrid[row][column] - 1] = true;

        }
        else {

          //this value has already appeared in this column, this is an error, store the location:
          System.out.println("Error in column detected, specifically at row: "+row+" column: "+column);
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

  public SodokuColumnValidator() {

    numberOfColumnsToValidate = 9;
    columnToStartValidationAt = 0;

  }

  public SodokuColumnValidator(int numberOfColumnsToValidate, int columnToStartValidationAt, boolean[][] arrayOfArrayOfcurrentValuesFoundInColumnsBeingChecked, int[][]sodukuGrid, ArrayList<ErrorAndSuggestionContainer> listOfErrorsAndSuggestionsThatHaveBeenDetected) {

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
