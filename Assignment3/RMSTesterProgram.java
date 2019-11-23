class RMSTesterProgram {

  public static void main(String[] args) {

    //BusyWork busyWork = new BusyWork(100);
    BusyWork taskOne = new BusyWork(100, 1);
    BusyWork taskTwo = new BusyWork(200, 2);
    BusyWork taskThree = new BusyWork(400, 4);
    BusyWork taskFour = new BusyWork(1600, 16);

    BusyWork[] arrayOfTasks = new BusyWork[] {taskOne, taskTwo, taskThree, taskFour};
    RMS<BusyWork> scheduler = new RMS<BusyWork>(arrayOfTasks, 10);

    Thread RMSThread = new Thread(scheduler);

    taskOne.setRMSThread(RMSThread);
    taskTwo.setRMSThread(RMSThread);
    taskThree.setRMSThread(RMSThread);
    taskFour.setRMSThread(RMSThread);

    RMSThread.start();

  }

}
