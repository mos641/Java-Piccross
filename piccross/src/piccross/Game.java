/*
 * File name: Game.java
 * Author: Mostapha A
 * Purpose: This contains the main method and uses the other classes
 * Class list: Game.java, GameView.java, GameModel.java, GameController.java
 */

package piccross;

import java.awt.EventQueue;

/**
 * Piccross class that contains the main method for the program, displays the
 * splash screen then game
 * 
 * @author mos
 * @version 2.0
 * @see piccross package
 * @since Java 16
 */
public class Game {
	
	/**
	 * The main method that combines the other classes, displays a splash screen then the game
	 * @param args default arguments
	 */
	public static void main(String[] args) {
		GameView gameView = new GameView();
		GameModel gameModel = new GameModel();
		GameController gameController = new GameController(gameView, gameModel);		
		
		// create the splash screen
		GameView.GameSplash splashScreen = new GameView.GameSplash();

		// call method to display
		splashScreen.showSplashWindow();

		// display the game after the splash screen
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				gameController.startGame("");
			}
		});
		
	}
}

