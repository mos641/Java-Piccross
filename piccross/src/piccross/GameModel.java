/*
 * File name: GameModel.java
 * Author: Mostapha A
 * Purpose: This maintains data and values
 * Class list: Game.java, GameView.java, GameModel.java, GameController.java
 */

package piccross;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * GameModel class contains game data
 * 
 * @author mos
 * @version 1.0
 * @see piccross package, Game.java, GameController.java
 * @since Java 16
 */
public class GameModel {
	/** The board dimension */
	private int dimension = 5;
	/** The board array representation */
	private Integer[][] board;
	/** The board selection array representation */
	private Integer[][] selectedBoard;
	/** The string with the solution */
	private String configString = "0";

	/** The starting seconds for timer */
	private int seconds = 0;
	/** Timer initialization */
	private Timer timer = new Timer();
	/** The timer task initialization */
	private TimerTask timerTask;

	/**
	 * Default game model constructor
	 */
	GameModel() {

	}

	/**
	 * Generates a solution string based on dimensions
	 */
	public void generateString() {
		int column = 0;
		int row = 0;
		// create a random object to randomize game solution
		Random rd = new Random();
		// reset current string
		configString = "";
		// temporary array and booleans for validation
		int[][] stringArray = new int[dimension][dimension];
		boolean rowCheck = false;
		boolean colCheck = false;

		// loop through dimension, generating a string for solution
		for (row = 0; row < dimension; row++) {
			// reset boolean checks
			rowCheck = false;
			for (column = 0; column < dimension; column++) {
				colCheck = false;
				// check that each column has at least one 1, otherwise generate randoms
				if (row == dimension - 1) {
					// loop through the row
					for (int i = 0; i < dimension - 1; i++) {
						// check if there is a 1 in the row
						if (stringArray[column][i] == 1) {
							colCheck = true;
						}
					}
					// if row check is false make last column in row a 1, otherwise randomise
					if (colCheck == false) {
						// set string and checks
						colCheck = true;
						configString = configString + "1";
						stringArray[column][row] = 1;
					} else {
						if (rd.nextBoolean() == true) {
							// set string and checks
							rowCheck = true;
							configString = configString + "1";
							stringArray[column][row] = 1;
						} else if (column == dimension - 1 && rowCheck == false) {
							// if there isn't at least one 1 in row, make last column in row a 1
							rowCheck = true;
							configString = configString + "1";
							stringArray[column][row] = 1;
						} else {
							// set string and checks
							configString = configString + "0";
							stringArray[column][row] = 0;
						}
					}
				} else {
					// generate a random boolean, true is 1
					if (rd.nextBoolean() == true) {
						// set string and checks
						rowCheck = true;
						configString = configString + "1";
						stringArray[column][row] = 1;
					} else if (column == dimension - 1 && rowCheck == false) {
						// if there isn't at least one 1 in row, make last column in row a 1
						rowCheck = true;
						configString = configString + "1";
						stringArray[column][row] = 1;
					} else {
						// set string and checks
						configString = configString + "0";
						stringArray[column][row] = 0;
					}
				}
			}
			// add a comma to the string
			if (row != dimension - 1) {
				configString = configString + ",";
			}
		}
		System.out.println(configString);
	}

	/**
	 * Generates a 2d array representing the board solution based on the configuration string
	 * @param string The configuration string to generate board out of
	 */
	public void generateBoard(String string) {
		configString = string;
		// split the config string based on commas
		String[] rows = string.split(",");
		dimension = rows[0].length();
		// reset / make a new board representation
		board = new Integer[dimension][dimension];
		selectedBoard = new Integer[dimension][dimension];
		
		int column = 0;
		int row = 0;

		// loop through strings and create board
		for(row = 0; row < dimension; row++) {
			for(column = 0; column < dimension; column++) {
				// loop through each column string and put corresponding value in the board
				board[column][row] = Integer.parseInt(Character.toString(rows[row].charAt(column)));
				// set selection to -1 for unselected
				selectedBoard[column][row] = -1;
			}
		}
	}
	
	/**
	 * Returns the current configuration string
	 * @return The configuration string
	 */
	public String getString() {
		return configString;
	}
	
	/**
	 * Resets the time to 0
	 */
	public void resetTime() {
		seconds = 0;
	}
	
	/**
	 * Returns the current time
	 * @return The time in seconds
	 */
	public int returnTime() {
		return seconds;
	}

	/**
	 * Changes the selection board 2d array representation based on a selection
	 * @param column The button's column dimension to change
	 * @param row The button's row dimension to change
	 * @param markOn Whether the mark checkbox is in use
	 * @return Whether the button was a correct selection
	 */
	public int selectButton(int column, int row, boolean markOn) {
		// -1 = unselected
		// 0 = correct mark (false/0)
		// 1 = correct selection (true/1)
		// 2 = incorrect mark(false/0) or selection(true/1)

		// select the chosen button accordingly, return associated number
		if ((board[column][row] == 1) && (markOn == false)) {
			// if mark is unselected, and button is true set to 1 (correct selection/true)
			selectedBoard[column][row] = 1;
			return 1;
		} else if ((board[column][row] == 0) && (markOn == true)) {
			// if mark is selected, and button is false set to 0 (correct mark/false)
			selectedBoard[column][row] = 0;
			return 0;
		} else {
			// otherwise set to 2 (incorrect)
			selectedBoard[column][row] = 2;
			return 2;
		}
	}
	
	/**
	 * Changes the boards dimension
	 * @param newDimension The new dimension for the board
	 */
	public void setDimension(int newDimension) {
		dimension = newDimension;
	}
	
	/**
	 * Gets the dimension of the model
	 * @return the dimension size
	 */
	public int getDimension() {
		return dimension;
	}

	/**
	 * Checks if a specific button is selected or not
	 * @param column The button's column dimension to check
	 * @param row The button's column dimension to check
	 * @return Whether the button is selected and if it is correct
	 */
	public int checkButtonSelected(int column, int row) {
		// return the provided buttons status
		return (selectedBoard[column][row]);
	}

	/**
	 * Checks the solution for a specific button
	 * @param column The button's column dimension to check
	 * @param row The button's column dimension to check
	 * @return The button's solution
	 */
	public int checkSolution(int column, int row) {
		// return the provided buttons solution
		return (board[column][row]);
	}

	/**
	 * Resets the selection boards 2d array representation to all unselected and timer to 0
	 */
	public void reset() {
		// set each button to unselected
		int column = 0;
		int row = 0;
		for (row = 0; row < dimension; row++) {
			for (column = 0; column < dimension; column++) {
				selectedBoard[column][row] = -1;
			}
		}
		seconds = 0;
	}

	/**
	 * Starts and keeps track of the timer on the GUI
	 * @param gameView The GameView that will update the timer for
	 */
	public void startTimer(GameView gameView) {
		// Timer task
		timerTask = new TimerTask() {
			public void run() {
				seconds++;
				// Update your interface
				gameView.updateTimer(seconds);
			}
		};
		try {
			timer.scheduleAtFixedRate(timerTask, 0, 1000);
		} catch (Exception e) {
			// Eventual treatment

		}
	}
}
