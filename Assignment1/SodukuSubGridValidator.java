import java.util.ArrayList;

class SodokuSubGridValidator extends SodukuValidator implements Runnable {

  private int numberOfSubGridsToValidate;
  private int[] gridToStartValidationAt; //this is the location of the top right value in the grid that will be validated

  private int currentGridNumber;

  public SodokuSubGridValidator(int numberOfSubGridsToValidate, int[][][] arrayOfArrayOfcurrentValuesFoundInGridsBeingChecked, int[] gridToStartValidationAt, int[][] sodukuGrid, ArrayList<ErrorAndSuggestionContainer> listOfErrorsAndSuggestionsThatHaveBeenDetected) {

    this.numberOfSubGridsToValidate = numberOfSubGridsToValidate;
    this.gridToStartValidationAt = new int[2];
    this.gridToStartValidationAt[0] = gridToStartValidationAt[0];
    this.gridToStartValidationAt[1] = gridToStartValidationAt[1];
    this.arrayOfArrayOfcurrentValuesFoundInGridsBeingChecked = arrayOfArrayOfcurrentValuesFoundInGridsBeingChecked;
    this.sodukuGrid = sodukuGrid;
    this.listOfErrorsAndSuggestionsThatHaveBeenDetected = listOfErrorsAndSuggestionsThatHaveBeenDetected;

  }

  public SodokuSubGridValidator() {

    numberOfSubGridsToValidate = 9;
    gridToStartValidationAt = new int[2];
    gridToStartValidationAt[0] = 0;
    gridToStartValidationAt[1] = 0;

  }

  public void run() {

    validateGrids();

  }

  private void validateGrids() {

    int numberOfSubGridsValidated = 0;
    currentGridNumber = determineGrid(gridToStartValidationAt[0], gridToStartValidationAt[1]); //will either initialize to grid 0, 3, or 6
    int column;

    //Validates 3x3 grids from left to right:
    while (numberOfSubGridsValidated < numberOfSubGridsToValidate) {

      for (int row = gridToStartValidationAt[0]; (row - gridToStartValidationAt[0]) < 3; row++) {

        column = gridToStartValidationAt[1];

        while ((column - gridToStartValidationAt[1]) < 3) {

          if (arrayOfArrayOfcurrentValuesFoundInGridsBeingChecked[currentGridNumber][sodukuGrid[row][column] - 1] == null) {

            //System.out.println("Checking grid, "+" Column: "+(column + 1) + "row: "+(row + 1));
            //this value is not yet in this grid, so no error, but now signal that it is in this grid:
            arrayOfArrayOfcurrentValuesFoundInGridsBeingChecked[currentGridNumber][sodukuGrid[row][column] - 1] = new int[2];
            arrayOfArrayOfcurrentValuesFoundInGridsBeingChecked[currentGridNumber][sodukuGrid[row][column] - 1][0] = row;
            arrayOfArrayOfcurrentValuesFoundInGridsBeingChecked[currentGridNumber][sodukuGrid[row][column] - 1][1] = column;

          }
          else {

            //this value has already appeared in this column, this is an error, store the location:

            System.out.println("Error in grid detected, particularly at row:  "+row+" column"+column);

            /*

            -NOTE: error solution message is set to empty for now, this can only be
              set once I am done since I need to know what values are missing in order to suggest them as solutions

            */
            //Prevent adding of duplicates:
            ErrorAndSuggestionContainer errorAndSuggestionContainer = new ErrorAndSuggestionContainer(row, column, arrayOfArrayOfcurrentValuesFoundInGridsBeingChecked[currentGridNumber][sodukuGrid[row][column] - 1][0], arrayOfArrayOfcurrentValuesFoundInGridsBeingChecked[currentGridNumber][sodukuGrid[row][column] - 1][1], "");
            if (listOfErrorsAndSuggestionsThatHaveBeenDetected.contains(errorAndSuggestionContainer) == false) {

              listOfErrorsAndSuggestionsThatHaveBeenDetected.add(errorAndSuggestionContainer);

            }

          }

          column++;

        }

      }

      gridToStartValidationAt[1] = gridToStartValidationAt[1] + 3; //Move to starting column in next grid on the right
      numberOfSubGridsValidated++;
      currentGridNumber++;

    }

  }

}
