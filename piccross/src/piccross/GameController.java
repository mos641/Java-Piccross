/*
 * File name: GameController.java
 * Author: Mostapha A
 * Purpose: Manages the timer and deals with actions
 * Class list: Game.java, GameView.java, GameModel.java, GameController.java
 */

package piccross;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * GameController class that sets up the pic cross game
 * 
 * @author mos
 * @version 2.1 
 * @see piccross package, Game.java, GameModel.java, GameView.java
 * @since Java 16
 */
public class GameController extends JFrame {
	/**
	 * Default variable
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The view portion of the game, will be set to what is passed through from the
	 * main
	 */
	private GameView gameView;
	/**
	 * The model portion of the game, will be set to what is passed through from the
	 * main
	 */
	private GameModel gameModel;
	/** Keeps track of current dimension/grid size */
	private int dimension = 5;
	/** Keeps track of the score */
	private int score = 0;
	/** Keeps track of how many buttons selected for checking end game */
	private int selections = 0;
	/** Stores the colour for correct selections */
	private static Color correctColour = new Color(97, 197, 255);
	/** Stores the colour for correct marked selections */
	private static Color markedColour = new Color(195, 180, 165);
	/** Stores the colour for incorrect selections */
	private static Color errorColour = new Color(200, 86, 70);
	/** The handler for the mark check box */
	CheckBoxHandler checkBoxHandler = new CheckBoxHandler();
	/** This is the time and score, it is set to 0 until the game is finished */
	private static String gameInfo = "0#0";

	/**
	 * Constructor for controller, takes in GameView and GameModel objects
	 * 
	 * @param view  The view portion of the game
	 * @param model The model portion of the game
	 */
	public GameController(GameView view, GameModel model) {
		// set to class variables
		gameView = view;
		gameModel = model;
	}

	/**
	 * Starts the game, creates visual components and data needed
	 */
	public void startGame(String gameConfig) {
		// create data for game logic
		if (gameConfig == "") {
			gameModel.generateString();
			gameModel.generateBoard(gameModel.getString());
		} else {
			gameModel.generateBoard(gameConfig);
			dimension = gameModel.getDimension();
			gameView.setDimension(dimension);
		}

		score = 0;
		// create all the visual components
		gameView.startGame(new MenuHandler(), new PlayButtonHandler(), new ResetHandler(), checkBoxHandler);
		/*
		gameView.reset();
		gameModel.reset();
		gameView.newGame(new PlayButtonHandler(), checkBoxHandler);
		*/
		
		// setup hints
		setupHints();
		// start time
		int time = gameModel.returnTime();
		if (time == 0) {
			gameModel.startTimer(gameView);
		} else {
			gameModel.resetTime();
		}
		
	}
	
	/**
	 * Sets the dimension for controller
	 * @param newDimension The new dimension value
	 */
	public void setDimension(int newDimension) {
		dimension = newDimension;
	}

	/**
	 * Return the current time and score
	 * 
	 * @return The time and score in a string
	 */
	public String returnInfo() {
		return gameInfo;
	}

	/**
	 * Calculates the hints and sends them to the View to write
	 */
	public void setupHints() {
		// create hint arrays, and write in hint areas
		int hintDimension = 6;
		Integer[][] hintsTop = new Integer[dimension][hintDimension];
		Integer[][] hintsSide = new Integer[dimension][hintDimension];
		// variables for loops
		int j;
		int i;
		int hintCounterSide;
		int hintCounterTop;
		int rowHint;
		int colHint;
		int rowHintPrev;
		int colHintPrev;

		// set hints to 0 by default
		for (i = 0; i < dimension; i++) {
			for (j = 0; j < hintDimension; j++) {
				hintsTop[i][j] = 0;
				hintsSide[i][j] = 0;
			}
		}

		// loop to check solution for hints
		for (i = 0; i < dimension; i++) {
			// hints and related variables start/reset at 0
			rowHint = 0;
			colHint = 0;
			rowHintPrev = 0;
			colHintPrev = 0;
			hintCounterSide = 0;
			hintCounterTop = 0;
			for (j = 0; j < dimension; j++) {
				// check how many trues are in a row for the hintsSide (row)
				if (gameModel.checkSolution(j, i) == 1) {
					// increment current hint if the solution is true
					rowHint++;
				} else {
					// otherwise set to 0
					rowHint = 0;
				}

				// check if the current hint is 0, and previous was not we are storing hint, or
				// if it is the last loop
				if ((rowHint == 0) && (rowHintPrev > 0)) {
					// store previous hint
					hintsSide[i][hintCounterSide] = rowHintPrev;
					hintCounterSide++;
				} else if (j == dimension - 1) {
					// if its last loop store the current row hint
					hintsSide[i][hintCounterSide] = rowHint;
					hintCounterSide++;
				}

				// set the previous row hint to current
				rowHintPrev = rowHint;

				// check how many trues are in a row for the hintsTop (column)
				if (gameModel.checkSolution(i, j) == 1) {
					// increment current hint if the solution is true
					colHint++;
				} else {
					// otherwise set to 0
					colHint = 0;
				}

				// check if the current hint is 0, and previous was not we are storing hint, or
				// if it is the last loop
				if ((colHint == 0) && (colHintPrev > 0)) {
					// store previous hint
					hintsTop[i][hintCounterTop] = colHintPrev;
					hintCounterTop++;
				} else if (j == dimension - 1) {
					// if its last loop store the current column hint
					hintsTop[i][hintCounterTop] = colHint;
					hintCounterTop++;
				}

				// set the previous column hint to current
				colHintPrev = colHint;
			}

		}

		// call function to write to hint areas
		gameView.writeHints(hintsTop, hintsSide);
	}

	/**
	 * Works with the view to change the colours of the buttons
	 * 
	 * @param whichColour indicates which colour (selection/marked/error) to change
	 */
	private void changeColours(int whichColour) {
		int column = 0;
		int row = 0;
		Color colour;

		// set colour based on whats selected
		if (whichColour == 0) {
			colour = markedColour;
		} else if (whichColour == 1) {
			colour = correctColour;
		} else {
			colour = errorColour;
		}

		// loop through the grid of buttons, changing corresponding colour
		for (row = 0; row < dimension; row++) {
			for (column = 0; column < dimension; column++) {
				// check the button selection status and update it's colour if it matches
				if (gameModel.checkButtonSelected(column, row) == whichColour) {
					gameView.changeButton(column, row, colour);
				}
			}
		}
	}

	/**
	 * Shows the solution on the buttons
	 */
	private void showSolution() {
		int column = 0;
		int row = 0;

		// loop through the grid of buttons, changing corresponding colour
		for (row = 0; row < dimension; row++) {
			for (column = 0; column < dimension; column++) {
				// check the button solution and change button
				if (gameModel.checkSolution(column, row) == 0) {
					gameModel.selectButton(column, row, true);
					gameView.changeButton(column, row, markedColour);
				} else {
					gameModel.selectButton(column, row, false);
					gameView.changeButton(column, row, correctColour);
				}
			}
		}
	}

	/**
	 * Creates a new game
	 */
	public void createNewGame() {
		gameView.newGame(new PlayButtonHandler(), checkBoxHandler);
	}
	
	/**
	 * Resets the controller variables keeping track of the game
	 */
	public void resetController() {
		selections = 0;
		score = 0;
	}
	
	/**
	 * Inner action listener class for managing the playing buttons
	 * 
	 * @author mos
	 * @version 1.0
	 * @since Java 16
	 * @see GameController
	 */
	private class PlayButtonHandler implements ActionListener {
		/**
		 * This is called when a playing area button is clicked, it will print the
		 * position in the history area
		 * 
		 * @param e The event object with the event information
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			String actionCommand = e.getActionCommand();
			// split the action command into integers for column and row
			String[] dimensions = actionCommand.split(",");
			int column = Integer.parseInt(dimensions[0]) - 1;
			int row = Integer.parseInt(dimensions[1]) - 1;

			// print action to history area (text area in control panel) and console
			gameView.historyAreaMessage("Pos " + actionCommand + " clicked;\n");
			System.out.println(actionCommand);

			// check if the button is selected
			if (gameModel.checkButtonSelected(column, row) == -1) {
				// call the model to keep track of selections, and set button color
				int selection = gameModel.selectButton(column, row, gameView.checkMark());
				// 0 = correct mark (false/0)
				// 1 = correct selection (true/1)
				// 2 = incorrect mark(false/0) or selection(true/1)
				if (selection == 0) {
					score++;
					gameView.changeButton(column, row, markedColour);
				} else if (selection == 1) {
					score++;
					gameView.changeButton(column, row, correctColour);
				} else {
					score--;
					gameView.changeButton(column, row, errorColour);
				}
				// increment selections
				selections++;
				// if we reached end of the game, show dialog
				if (selections == dimension * dimension) {
					gameView.endGame(score);
					int time = gameModel.returnTime();
					gameInfo = time + "#" + score;
				}
			}

			// update the score
			gameView.updateScore(score);

		}

	}

	/**
	 * Inner action listener class for managing the reset button
	 * 
	 * @author mos
	 * @version 1.0
	 * @since Java 16
	 * @see GameController
	 */
	private class ResetHandler implements ActionListener {
		/**
		 * This is called when the reset button is clicked, it will clear the history
		 * area
		 * 
		 * @param e The event object with the event information
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			// clear the history area text
			gameView.historyAreaMessage("");
			System.out.println("History area cleared");

			// reset buttons
			gameView.reset();
			gameModel.reset();

			// reset score
			score = 0;
			selections = 0;
		}
	}

	/**
	 * Inner item listener class for managing the mark check box
	 * 
	 * @author mos
	 * @version 1.0
	 * @since Java 16
	 * @see GameController
	 */
	private class CheckBoxHandler implements ItemListener {
		/**
		 * This is called when the mark check box is changed
		 * 
		 * @param e The event object with the event information
		 */
		@Override
		public void itemStateChanged(ItemEvent e) {
			// if the check box is selected or unselected print info and toggle our variable
			if (gameView.checkMark() == true) {
				gameView.historyAreaMessage("Mark set;" + "\n");
				System.out.println("Mark set;");
			} else {
				gameView.historyAreaMessage("Mark reset;" + "\n");
				System.out.println("Mark reset;");
			}
		}
	}

	/**
	 * Inner action listener class for managing the menus
	 * 
	 * @author mos
	 * @version 1.0
	 * @since Java 16
	 * @see GameController
	 */
	private class MenuHandler implements ActionListener {
		/**
		 * This is called when any menu item is selected
		 * 
		 * @param e The event object with the event information
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			// 13 = 3x3 grid
			// 15 = 5x5 grid
			// 110 = 10x10 grid
			// 2 = solution
			// 3 = exit
			// 4 = colours
			// 5 = about

			String menuChoice = e.getActionCommand();

			// depending on menu choice do something
			if (menuChoice == "13") {
				// make a new game with a 3x3 grid
				System.out.println("menu choice 3x3 grid selected");

				// reset view and model
				gameView.reset();
				gameModel.reset();
				resetController();

				// set new dimension
				dimension = 3;
				gameView.setDimension(dimension);
				gameModel.setDimension(dimension);

				// reset play area
				gameView.newGame(new PlayButtonHandler(), checkBoxHandler);

				// create data for game logic
				gameModel.generateString();
				gameModel.generateBoard(gameModel.getString());

				// setup hints
				setupHints();

			} else if (menuChoice == "15") {
				// make a new game with a 5x5 grid
				System.out.println("menu choice 5x5 grid selected");
				// reset view and model
				gameView.reset();
				gameModel.reset();
				resetController();

				// set new dimension
				dimension = 5;
				gameView.setDimension(dimension);
				gameModel.setDimension(dimension);

				// reset play area
				gameView.newGame(new PlayButtonHandler(), checkBoxHandler);

				// create data for game logic
				gameModel.generateString();
				gameModel.generateBoard(gameModel.getString());

				// setup hints
				setupHints();
			} else if (menuChoice == "110") {
				// make a new game with a 10x10 grid
				System.out.println("menu choice 10x10 grid selected");
				// reset view and model
				gameView.reset();
				gameModel.reset();
				resetController();
				
				// set new dimension
				dimension = 10;
				gameView.setDimension(dimension);
				gameModel.setDimension(dimension);

				// reset play area
				gameView.newGame(new PlayButtonHandler(), checkBoxHandler);

				// create data for game logic
				gameModel.generateString();
				gameModel.generateBoard(gameModel.getString());

				// setup hints
				setupHints();
			} else if (menuChoice == "2") {
				// display the solution, set score to 0
				System.out.println("menu choice solution selected");
				gameModel.reset();
				gameView.reset();
				showSolution();
				score = 0;

				// get solution string
				String solution = gameModel.getString();
				String[] rows = solution.split(",");
				// print to history area
				for (String row : rows) {
					gameView.historyAreaMessage(row + "\n");
					System.out.println(row);
				}
			} else if (menuChoice == "3") {
				// exit the game
				System.out.println("menu choice exit selected");
				System.exit(0);
			} else if (menuChoice == "4") {
				// display colour chooser
				System.out.println("menu choice colour selected");
				new ColourChooser();
			} else if (menuChoice == "5") {
				// display about dialog
				System.out.println("menu choice about selected");
				gameView.aboutDialog();
			}
		}

	}

	/**
	 * Inner colour chooser class deals with changing the colours in the menu
	 * 
	 * @author mos
	 * @version 1.0
	 * @see piccross package, GameController.java, GameModel.java
	 * @since Java 16
	 */
	private class ColourChooser extends JFrame implements ActionListener {
		/** Default variable */
		private static final long serialVersionUID = 1L;
		/** The label that will display the correct selection colour */
		private JLabel correctLabel;
		/** The label that will display the marked selection colour */
		private JLabel markedLabel;
		/** The label that will display the incorrect selection colour */
		private JLabel errorLabel;

		/**
		 * Constructor for the menu option to change colour, creates a new frame with
		 * selections
		 */
		ColourChooser() {
			// new frame for choosing colours
			JFrame coloursFrame = new JFrame("Choose Colours");
			coloursFrame.setLayout(new BorderLayout());

			// Three panels for each Colour
			JPanel correctPanel = new JPanel();
			correctPanel.setLayout(new BorderLayout());
			JPanel markedPanel = new JPanel();
			markedPanel.setLayout(new BorderLayout());
			JPanel errorPanel = new JPanel();
			errorPanel.setLayout(new BorderLayout());

			// create labels and buttons for each colour
			correctLabel = new JLabel();
			correctLabel.setBackground(correctColour);
			correctLabel.setPreferredSize(new Dimension(150, 80));
			correctLabel.setOpaque(true);
			markedLabel = new JLabel();
			markedLabel.setBackground(markedColour);
			markedLabel.setPreferredSize(new Dimension(150, 80));
			markedLabel.setOpaque(true);
			errorLabel = new JLabel();
			errorLabel.setPreferredSize(new Dimension(150, 80));
			errorLabel.setBackground(errorColour);
			errorLabel.setOpaque(true);

			// buttons
			JButton correctButton = new JButton("Colour 1: Correct");
			correctButton.setPreferredSize(new Dimension(150, 30));
			correctButton.setActionCommand("correct");
			correctButton.addActionListener(this);
			JButton markedButton = new JButton("Colour 2: Marked");
			markedButton.setPreferredSize(new Dimension(150, 30));
			markedButton.setActionCommand("marked");
			markedButton.addActionListener(this);
			JButton errorButton = new JButton("Colour 3: Error");
			errorButton.setPreferredSize(new Dimension(150, 30));
			errorButton.setActionCommand("error");
			errorButton.addActionListener(this);

			// add labels and buttons to their respective panels
			correctPanel.add(correctLabel, BorderLayout.NORTH);
			correctPanel.add(correctButton, BorderLayout.SOUTH);
			markedPanel.add(markedLabel, BorderLayout.NORTH);
			markedPanel.add(markedButton, BorderLayout.SOUTH);
			errorPanel.add(errorLabel, BorderLayout.NORTH);
			errorPanel.add(errorButton, BorderLayout.SOUTH);

			// add panels to frame
			coloursFrame.add(correctPanel, BorderLayout.WEST);
			coloursFrame.add(markedPanel, BorderLayout.CENTER);
			coloursFrame.add(errorPanel, BorderLayout.EAST);

			// set dialog size and visibility
			coloursFrame.setSize(470, 150);
			coloursFrame.setVisible(true);
		}

		/**
		 * Action listener for keeping track of which colours are being changed
		 */
		public void actionPerformed(ActionEvent e) {
			// store action command
			String colourButton = e.getActionCommand();

			// change colours with a colour chooser depending on button
			if (colourButton == "correct") {
				// change the correct colour, and corresponding colour in the chooser and
				// buttons
				Color c = JColorChooser.showDialog(this, "Correct Colour", correctColour);
				if (c != null) {
					correctColour = c;
					correctLabel.setBackground(c);
					changeColours(1);
				}
			} else if (colourButton == "marked") {
				// change the marked colour, and corresponding colour in the chooser and buttons
				Color c = JColorChooser.showDialog(this, "Marked Colour", markedColour);
				if (c != null) {
					markedColour = c;
					markedLabel.setBackground(c);
					changeColours(0);
				}
			} else if (colourButton == "error") {
				// change the error colour, and corresponding colour in the chooser and buttons
				Color c = JColorChooser.showDialog(this, "Error Colour", errorColour);
				if (c != null) {
					errorColour = c;
					errorLabel.setBackground(c);
					changeColours(2);
				}
			}
		}
	}

}