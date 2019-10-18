import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class Task implements ActionListener {

  private boolean timerExpired;

  public Task() {

    timerExpired = false;

  }

  public boolean getTimerExpired() {

    return timerExpired;

  }

  public void setTimerExpired(boolean timerExpired) {

    this.timerExpired = timerExpired;

  }

  public void actionPerformed(ActionEvent e) {

    System.out.println("Timer expired!");
    timerExpired = true;

  }
  
}
