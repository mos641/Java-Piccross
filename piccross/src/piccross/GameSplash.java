/*
 * File name: GameSplash.java
 * Author: Mostapha A
 * Purpose: This sets up the splash screen
 * Class list: Piccross.java, GameSplash.java, GameController.java
 */

package piccross;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;

/**
 * GameSplash class that sets up the splash screen
 * 
 * @author mos
 * @version 1.0
 * @see piccross package, Piccross.java
 * @since Java 16
 */
public class GameSplash extends JWindow {
	/** Required long value */
	private static final long serialVersionUID = 1L;

	/**
	 * Displays a splash screen for 5 seconds
	 */
	public void showSplashWindow() {
		// create panel that will hold image
		JPanel splashPanel = new JPanel(new BorderLayout());

		// create a label to place splash screen image
		JLabel label;
		try {
			label = new JLabel(new ImageIcon("images/loadingscreen.gif"));
		} catch (Exception e) {
			// print error and no image will be displayed instead of selected image
			e.printStackTrace();
			label = new JLabel("No image");
		}
		
		// add the image to our panel
		splashPanel.add(label, BorderLayout.CENTER);

		// get the size of the screen
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

		// calculate the position to place window in center of screens
		int x = (screen.width - (600)) / 2;
		int y = (screen.height - (300)) / 2;

		// set the window size and put in the center of the screen
		setBounds(x, y, (600), (300));

		// replace the window content with our image in the panel
		setContentPane(splashPanel);

		// ensure splash window is visible
		setVisible(true);
		
		// keep the screen up for 5 seconds
		try {
			// splash screen displays for 5000 milliseconds
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// print error if one is caught
			e.printStackTrace();
			System.out.println("Interrupted Exception " + e);
		}
		
		// release resources
		dispose();
	}
}