/*

A data structure that stores the errors and suggestions
for fixing those errors as detected by the Sodoku validators.

*/
class ErrorAndSuggestionContainer {

  int rowOfError;
  int columnOfError;
  String suggestion;

  ErrorAndSuggestionContainer(int rowOfError, int columnOfError, String suggestion) {

    this.rowOfError = rowOfError;
    this.columnOfError = columnOfError;
    this.suggestion = suggestion;

  }

  @Override public boolean equals(Object otherObject) {

    if (otherObject == null) {

      return false;

    }

    ErrorAndSuggestionContainer otherContainer = (ErrorAndSuggestionContainer) otherObject;

    return (this.rowOfError == otherContainer.rowOfError) && (this.columnOfError == otherContainer.columnOfError);

  }

  public void setSuggestion(String suggestion) {

    this.suggestion = suggestion;

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
