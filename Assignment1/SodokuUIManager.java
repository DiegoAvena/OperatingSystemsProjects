import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

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
    private JTextArea resultsText;
    private JScrollPane resultsTextScrollPane;

    private JTextArea workText;
    private JButton showWorkButton;
    private JPanel workPanel;
    private JScrollPane workScrollPane;
    //showWorkButton.setBackground(Color.LIGHT_GRAY);

    public void setPanelColor(Color color, int row, int column) {

      thePanelsOnTheSodokuGrid[row][column].setBackground(color);

    }

    public void setResultsText(String message) {

      resultsText.append(message + '\n');

    }

    public void setWorkText(String message) {

      workText.append(message + '\n');

    }

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

      resultsText = new JTextArea(4, 20);
      resultsText.setText("Results: " + '\n');
      resultsText.setFont(new Font("Arial", Font.BOLD, 15));
      resultsText.setBackground(Color.WHITE);

      resultsTextScrollPane = new JScrollPane(resultsText);
      resultsTextScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
      resultsTextScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

      panelWithResults.add(resultsTextScrollPane, BorderLayout.CENTER);

      workPanel = new JPanel();
      workPanel.setBackground(Color.WHITE);
      workPanel.setLayout(new BorderLayout());

      //Create the button the user can click to see the process taken to get to this final result:
      showWorkButton = new JButton("Show work");
      showWorkButton.setBackground(Color.LIGHT_GRAY);
      showWorkButton.addActionListener(this);
      workPanel.add(showWorkButton, BorderLayout.NORTH);

      //workText = new JLabel("STEPS TAKEN SHOW HERE", SwingConstants.CENTER);
      //workText.setFont(new Font("Arial", Font.BOLD, 30));
      workText = new JTextArea(10, 20);
      workText.setText("STEPS TAKEN TO GET RESULTS WILL SHOW HERE: " + '\n');
      workText.setFont(new Font("Arial", Font.BOLD, 12));
      workText.setBackground(Color.WHITE);

      workScrollPane = new JScrollPane(workText);
      workScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
      workScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

      workPanel.add(workScrollPane, BorderLayout.CENTER);

      //Add the GUI sodoku grid and GUI results to the frame:
      add(panelThatContainsTheSodokuGridPanelsAndLabels, BorderLayout.CENTER);
      add(panelWithResults, BorderLayout.SOUTH);
      add(workPanel, BorderLayout.EAST);

      pack();
      setSize(windowWidth, windowHeight);

    }

    public void setEntireRowOfPanelsToColor(Color color, int rowNum) {

      for (int column = 0; column < 9; column++) {

        thePanelsOnTheSodokuGrid[rowNum][column].setBackground(color);

      }

    }

    public void setEntireColumnOfPanelsToColor(Color color, int columnNum) {

      for (int row = 0; row < 9; row++) {

        thePanelsOnTheSodokuGrid[row][columnNum].setBackground(color);

      }

    }

    public void setEntireGridOfPanelsToColor(Color color, int gridNum) {

      int startingRowNum = 0;
      int startingColumnNum = 0;

      if (gridNum == 0) {

        startingRowNum = 0;
        startingColumnNum = 0;

      }
      else if (gridNum == 1) {

        startingRowNum = 0;
        startingColumnNum = 3;

      }
      else if (gridNum == 2) {

        startingRowNum = 0;
        startingColumnNum = 6;

      }
      else if (gridNum == 3) {

        startingRowNum = 1;
        startingColumnNum = 0;

      }
      else if (gridNum == 4) {

        startingRowNum = 1;
        startingColumnNum = 3;

      }
      else if (gridNum == 5) {

        startingRowNum = 1;
        startingColumnNum = 6;

      }
      else if (gridNum == 6) {

        startingRowNum = 2;
        startingColumnNum = 0;

      }
      else if (gridNum == 7) {

        startingRowNum = 2;
        startingColumnNum = 3;

      }
      else if (gridNum == 8) {

        startingRowNum = 2;
        startingColumnNum = 6;

      }

      for (int row = startingRowNum; (row - startingRowNum) < 3; row++) {

        for (int column = startingColumnNum; (column - startingColumnNum) < 3; column++) {

          thePanelsOnTheSodokuGrid[row][column].setBackground(color);

        }

      }

    }

    public void actionPerformed(ActionEvent e) {

      //do button action here...
      System.exit(0);

    }

}
