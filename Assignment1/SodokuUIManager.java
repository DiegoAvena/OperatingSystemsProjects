import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.SwingConstants;
import javax.swing.JButton;

import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

class SodokuUIManager extends JFrame implements ActionListener {

    private int windowWidth; //the width of the UI window
    private int windowHeight; //the height of the UI window

    private JPanel[][] thePanelsOnTheSodokuGrid;
    private JLabel[][] theLabelsOnTheSodokuGrid;

    private JPanel panelThatContainsTheSodokuGridPanelsAndLabels;

    private JPanel panelWithResults;
    private JLabel resultsLabel;

    private JLabel workText;
    private JButton showWorkButton;
    private JPanel workPanel;
    //showWorkButton.setBackground(Color.LIGHT_GRAY);


    public SodokuUIManager(int[][] theSodokuGridToRepresent) {

      super("Sodoku Validator");
      windowWidth = 1000;
      windowHeight = 600;

      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Makes sure the program ends when user closes window
      setLayout(new BorderLayout());
      setBackground(Color.BLACK);

      panelThatContainsTheSodokuGridPanelsAndLabels = new JPanel();
      panelThatContainsTheSodokuGridPanelsAndLabels.setLayout(new GridLayout(9, 9, -1, -1));
      panelThatContainsTheSodokuGridPanelsAndLabels.setBorder(BorderFactory.createEmptyBorder());

      thePanelsOnTheSodokuGrid = new JPanel[9][9];
      theLabelsOnTheSodokuGrid = new JLabel[9][9];

      //Create the UI representation of the sodoku grid that has been supplied:
      for (int row = 0; row < 9; row++) {

        for (int column = 0; column < 9; column++) {

          //Create sub panel that will contain the number in the grid location of the sodoku grid supplied:
          thePanelsOnTheSodokuGrid[row][column] = new JPanel();
          thePanelsOnTheSodokuGrid[row][column].setBackground(Color.LIGHT_GRAY);
          thePanelsOnTheSodokuGrid[row][column].setLayout(new BorderLayout());
          thePanelsOnTheSodokuGrid[row][column].setBorder(BorderFactory.createLineBorder(Color.BLACK));

          //store the number in a label, and store this label at the center of the sub panel created above:
          theLabelsOnTheSodokuGrid[row][column] = new JLabel(Integer.toString(theSodokuGridToRepresent[row][column]), SwingConstants.CENTER);
          theLabelsOnTheSodokuGrid[row][column].setFont(new Font("Arial", Font.BOLD, 30));
          thePanelsOnTheSodokuGrid[row][column].add(theLabelsOnTheSodokuGrid[row][column], BorderLayout.CENTER);

          panelThatContainsTheSodokuGridPanelsAndLabels.add(thePanelsOnTheSodokuGrid[row][column]);

        }

      }

      //Create the panel that reports the results:
      panelWithResults = new JPanel();
      panelWithResults.setBackground(Color.GRAY);
      panelWithResults.setLayout(new BorderLayout());
      resultsLabel = new JLabel("FINAL RESULTS WILL SHOW HERE...");
      resultsLabel.setFont(new Font("Arial", Font.BOLD, 15));
      panelWithResults.add(resultsLabel, BorderLayout.CENTER);


      workPanel = new JPanel();
      workPanel.setBackground(Color.WHITE);
      workPanel.setLayout(new BorderLayout());

      //Create the button the user can click to see the process taken to get to this final result:
      showWorkButton = new JButton("Show work");
      showWorkButton.setBackground(Color.LIGHT_GRAY);
      showWorkButton.addActionListener(this);
      workPanel.add(showWorkButton, BorderLayout.NORTH);

      workText = new JLabel("STEPS TAKEN SHOW HERE", SwingConstants.CENTER);
      workText.setFont(new Font("Arial", Font.BOLD, 30));
      workPanel.add(workText, BorderLayout.CENTER);

      //Add the GUI sodoku grid and GUI results to the frame:
      add(panelThatContainsTheSodokuGridPanelsAndLabels, BorderLayout.CENTER);
      add(panelWithResults, BorderLayout.SOUTH);
      add(workPanel, BorderLayout.EAST);

      pack();
      setSize(windowWidth, windowHeight);

    }

    public void actionPerformed(ActionEvent e) {

      //do button action here...
      System.exit(0);

    }

}
