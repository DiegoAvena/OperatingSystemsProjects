/*

A data structure that stores the errors and suggestions
for fixing those errors as detected by the Sodoku validators.

*/
class ErrorAndSuggestionContainer {

  int rowOfError;
  int columnOfError;
  int gridOfError;

  int rowOfNumberThisValueConflictsWith;
  int columnOfNumberThisValueConflictsWith;

  boolean potentialErrorWasDeterminedToBeTheActualError;
  boolean conflictingValueWasDeterminedToBeTheActualError;

  int suggestedValue;

  ErrorAndSuggestionContainer(int rowOfError, int columnOfError, int rowOfNumberThisValueConflictsWith, int columnOfNumberThisValueConflictsWith,String suggestion) {

    potentialErrorWasDeterminedToBeTheActualError = false;
    conflictingValueWasDeterminedToBeTheActualError = false;
    suggestedValue = 0;

    this.rowOfError = rowOfError;
    this.columnOfError = columnOfError;
    this.rowOfNumberThisValueConflictsWith = rowOfNumberThisValueConflictsWith;
    this.columnOfNumberThisValueConflictsWith = columnOfNumberThisValueConflictsWith;
    gridOfError = -1;

  }

  ErrorAndSuggestionContainer(int rowOfError, int columnOfError, int rowOfNumberThisValueConflictsWith, int columnOfNumberThisValueConflictsWith, int gridOfError, String suggestion) {

    potentialErrorWasDeterminedToBeTheActualError = false;
    conflictingValueWasDeterminedToBeTheActualError = false;
    suggestedValue = 0;

    this.rowOfError = rowOfError;
    this.columnOfError = columnOfError;
    this.rowOfNumberThisValueConflictsWith = rowOfNumberThisValueConflictsWith;
    this.columnOfNumberThisValueConflictsWith = columnOfNumberThisValueConflictsWith;
    this.gridOfError = gridOfError;

  }

  public void setSuggestedValue(int suggestedValue) {

    this.suggestedValue = suggestedValue;

  }

  public int getSuggestedValue() {

    return suggestedValue;

  }

  public int getErrorGridNumber() {

    return gridOfError;

  }
  
  @Override public boolean equals(Object otherObject) {

    if (otherObject == null) {

      return false;

    }

    ErrorAndSuggestionContainer otherContainer = (ErrorAndSuggestionContainer) otherObject;

    return (this.rowOfError == otherContainer.rowOfError) && (this.columnOfError == otherContainer.columnOfError)
        && (this.rowOfNumberThisValueConflictsWith == otherContainer.rowOfNumberThisValueConflictsWith)
        && (this.columnOfNumberThisValueConflictsWith == otherContainer.columnOfNumberThisValueConflictsWith);

  }

  public void setPotentialErrorWasDeterminedToBeTheActualError(boolean potentialErrorWasDeterminedToBeTheActualError) {

    this.potentialErrorWasDeterminedToBeTheActualError = potentialErrorWasDeterminedToBeTheActualError;

  }

  public void setConflictingValueWasDeterminedToBeTheActualError(boolean conflictingValueWasDeterminedToBeTheActualError) {

    this.conflictingValueWasDeterminedToBeTheActualError = conflictingValueWasDeterminedToBeTheActualError;

  }

  public boolean getPotentialErrorWasDeterminedToBeTheActualError() {

    return potentialErrorWasDeterminedToBeTheActualError;

  }

  public boolean getConflictingValueWasDeterminedToBeTheActualError() {

    return conflictingValueWasDeterminedToBeTheActualError;

  }

  public int getRowOfNumberThisValueConflictsWith() {

    return rowOfNumberThisValueConflictsWith;

  }

  public int getColumnOfNumberThisValueConflictsWith() {

    return columnOfNumberThisValueConflictsWith;

  }

  public int getRowOfError() {

    return rowOfError;

  }

  public int getColumnOfError() {

    return columnOfError;

  }

}
