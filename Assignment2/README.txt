SeeSawSimulator
By: Diego Avena

-----------------------------------------------------------------------------------------

Description:

This project simulates 2 seesaw riders named Fred and Wilma who
take turns pushing up and down on the seesaw. Semaphores are used to insure that
only 1 person pushes up at a time, allowing the riders to take turns. The output of
the program contains the height that each person is at for each simulation run, where 1 simulation
is represented by the a specific person going up and then down, or down and then up.

-----------------------------------------------------------------------------------------
Assumptions:

1.) Fred goes first all the time. He starts at position 1. Wilma pushes up after Fred, she starts at
position 7.

2.) 1 seesaw simulation is represented by a specific person going up and then down or going down and then
up. For this project, I chose to focus on when Fred goes up and down and down and up to count the number of
simulations that have occurred.

3.) Fred and Wilma only use the seesaw for 10 simulations: from the previous line, this means the
simulation ends after Fred has gone either up and down or down and up 10 times.

-----------------------------------------------------------------------------------------
How to run:

1.) TO COMPILE: make all. (or just do javac *.java)
2.) TO RUN: make run (or just do java SeeSawSimulator)
