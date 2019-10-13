/*

A data structure that stores the errors and suggestions
for fixing those errors as detected by the Sodoku validators.

*/
class ErrorAndSuggestionContainer {

  int rowOfError;
  int columnOfError;
  String suggestion;

  int rowOfNumberThisValueConflictsWith;
  int columnOfNumberThisValueConflictsWith;

  ErrorAndSuggestionContainer(int rowOfError, int columnOfError, int rowOfNumberThisValueConflictsWith, int columnOfNumberThisValueConflictsWith,String suggestion) {

    this.rowOfError = rowOfError;
    this.columnOfError = columnOfError;
    this.rowOfNumberThisValueConflictsWith = rowOfNumberThisValueConflictsWith;
    this.columnOfNumberThisValueConflictsWith = columnOfNumberThisValueConflictsWith;
    this.suggestion = suggestion;

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

  public void setSuggestion(String suggestion) {

    this.suggestion = suggestion;

  }

  int getRowOfNumberThisValueConflictsWith() {

    return rowOfNumberThisValueConflictsWith;

  }

  int getColumnOfNumberThisValueConflictsWith() {

    return columnOfNumberThisValueConflictsWith;

  }
  
  int getRowOfError() {

    return rowOfError;

  }

  int getColumnOfError() {

    return columnOfError;

  }

  String getSuggestion() {

    return suggestion;

  }

}