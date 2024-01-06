/*
 * File name: GameView.java
 * Author: Mostapha A
 * Purpose: This makes the splash screen and creates/maintains visual components
 * Class list: Game.java, GameView.java, GameModel.java, GameController.java
 */

package piccross;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JWindow;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import java.awt.event.ActionListener;
import java.awt.event.ItemListener;

/**
 * GameView class that sets up the splash screen and visual game components
 * 
 * @author mos
 * @version 1.1
 * @see piccross package, Piccross.java, GameController.java
 * @since Java 16
 */
public class GameView extends JFrame {
	/** Required long value */
	private static final long serialVersionUID = 1L;
	/** A grey border for use on different elements */
	private static Border greyBorder = BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(70, 70, 70));
	/** A blue border for use on different elements */
	private static Border blueBorder = BorderFactory.createMatteBorder(3, 3, 3, 3, new Color(97, 197, 255));
	/** A grey background color for use on different elements */
	private static Color greyBG = new Color(84, 91, 102);
	/** A light grey background color for use on different elements */
	private static Color lightGreyBG = new Color(107, 112, 122);
	/** This is the check box for marking */
	private JCheckBox mark = new JCheckBox();
	/** This is the history area, the text box in the control panel */
	private JTextArea historyArea = new JTextArea();
	/** The panel that will contain the play area, hint area and mark check box */
	private static JPanel playArea;
	/**
	 * The panel that will contain the logo and control panel elements score, time,
	 * history area and reset button
	 */
	private static JPanel controlPanel = new JPanel();
	/** The text box for the score */
	private JTextField score;
	/** The text box for the time */
	private JTextField time;
	/** The array that holds all the play buttons */
	private JButton[][] playButtons;
	/** The array that holds all the hint areas */
	private JPanel[] hintAreas;
	/** The array that holds the hint texts for the top hints */
	private JTextPane[] hintTextTop;
	/** The array that holds the hint texts/labels for the side hints */
	private JLabel[] hintTextSide;
	/** Keeps track of the board dimension */
	private int dimension = 5;

	/**
	 * Default constructor, set the name
	 */
	GameView() {
		// set window name
		super("piccross - mostapha");
	}

	/**
	 * Sets up all visual components of the piccross game
	 * 
	 * @param menuHandler        Tha handler for the menu
	 * @param playButtonHandler  The handler for the playing buttons
	 * @param resetButtonHandler The handler for the reset button
	 * @param checkBoxHandler    The handler for the mark check box
	 */
	public void startGame(ActionListener menuHandler, ActionListener playButtonHandler,
			ActionListener resetButtonHandler, ItemListener checkBoxHandler) {
		// set attributes for main window
		setSize(987, 710);
		getContentPane().setBackground(greyBG);
		setResizable(false);

		// hides window if they close from the window button
		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				setVisible(false);
			}
		});
		
		// set the layout for our main window
		setLayout(new BorderLayout());

		// set the image for application
		ImageIcon miniLogo;
		try {
			miniLogo = new ImageIcon("images/logomini.png");
			setIconImage(miniLogo.getImage());
		} catch (Exception e) {
			// print error and default image will be displayed
			e.printStackTrace();
		}

		// set up menu
		setUpMenu(menuHandler);

		// call methods that setup the separate panels
		mark.addItemListener(checkBoxHandler);
		setUpControlPanel(resetButtonHandler);
		playArea = new JPanel();
		setUpPlayingArea(playButtonHandler, checkBoxHandler);

		// add components
		add(controlPanel, BorderLayout.WEST);
		add(playArea, BorderLayout.CENTER);

		// set visible as true and center in the screen
		setVisible(true);
		setLocationRelativeTo(null);
		pack();
	}
	
	/**
	 * sets the window as visible
	 */
	public void setVisible() {
		setVisible(true);
	}
	
	/**
	 * Returns whether the mark check box is selected
	 * @return Selected status of mark check box
	 */
	public boolean checkMark() {
		return mark.isSelected();
	}

	/**
	 * Resets a games playing area visual components
	 * 
	 * @param playButtonHandler The handler for play buttons
	 * @param checkBoxHandler   The handler for the check box
	 */
	public void newGame(ActionListener playButtonHandler, ItemListener checkBoxHandler) {
		// remove components to update
		remove(playArea);
		playArea.removeAll();
		// set up playing are again
		setUpPlayingArea(playButtonHandler, checkBoxHandler);
		revalidate();
		repaint();
		// re add and validate
		add(playArea, BorderLayout.CENTER);
		revalidate();
		repaint();
	}

	/**
	 * Sets up the menu bar and all it's items
	 * 
	 * @param menuHandler The handler for the menu items
	 */
	public void setUpMenu(ActionListener menuHandler) {
		// create all the image icons
		// ImageIcon newGameIcon;
		ImageIcon solutionIcon;
		ImageIcon exitIcon;
		ImageIcon coloursIcon;
		ImageIcon aboutIcon;
		/*
		 * new game will be sub menu for different dimensions, no icon try { newGameIcon
		 * = new ImageIcon("images/newgame.gif"); } catch (Exception e) { // print error
		 * and no image will be displayed instead of selected image e.printStackTrace();
		 * newGameIcon = new ImageIcon(); }
		 */
		try {
			solutionIcon = new ImageIcon("images/solution.gif");
		} catch (Exception e) { // print error and no image will be displayed instead of selected image
			e.printStackTrace();
			solutionIcon = new ImageIcon();
		}
		try {
			exitIcon = new ImageIcon("images/exit.gif");
		} catch (Exception e) { // print error and no image will be displayed instead of selected image
			e.printStackTrace();
			exitIcon = new ImageIcon();
		}
		try {
			coloursIcon = new ImageIcon("images/colours.gif");
		} catch (Exception e) { // print error and no image will be displayed instead of selected image
			e.printStackTrace();
			coloursIcon = new ImageIcon();
		}
		try {
			aboutIcon = new ImageIcon("images/about.gif");
		} catch (Exception e) { // print error and no image will be displayed instead of selected image
			e.printStackTrace();
			aboutIcon = new ImageIcon();
		}
		// Action commands
		// 13 = 3x3 grid
		// 15 = 5x5 grid
		// 110 = 10x10 grid
		// 2 = solution
		// 3 = exit
		// 4 = colours
		// 5 = about

		// Game menu
		JMenu gameMenu = new JMenu("Game");

		// create submenu for new game
		JMenu newGameMenu = new JMenu("New");

		// create new game menu options
		JMenuItem newGame3 = new JMenuItem("3x3 Game");
		newGameMenu.add(newGame3);
		newGame3.setActionCommand("13");
		newGame3.addActionListener(menuHandler);
		JMenuItem newGame5 = new JMenuItem("5x5 Game");
		newGameMenu.add(newGame5);
		newGame5.setActionCommand("15");
		newGame5.addActionListener(menuHandler);
		JMenuItem newGame10 = new JMenuItem("10x10 Game");
		newGameMenu.add(newGame10);
		newGame10.setActionCommand("110");
		newGame10.addActionListener(menuHandler);

		// add new game to game menu
		gameMenu.add(newGameMenu);

		// create solution menu item - add in Game menu
		JMenuItem solutionItem = new JMenuItem("Solution", solutionIcon);
		gameMenu.add(solutionItem);
		solutionItem.setActionCommand("2");
		solutionItem.addActionListener(menuHandler);

		// create exit menu item - add in Game menu
		JMenuItem exitItem = new JMenuItem("Exit", exitIcon);
		gameMenu.add(exitItem);
		exitItem.setActionCommand("3");
		exitItem.addActionListener(menuHandler);

		// create menu bar, add game menu
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		menuBar.add(gameMenu);

		// create help menu
		JMenu helpMenu = new JMenu("Help");

		// create colours menu item - add in Help menu
		JMenuItem coloursItem = new JMenuItem("Colours", coloursIcon);
		helpMenu.add(coloursItem);
		coloursItem.setActionCommand("4");
		coloursItem.addActionListener(menuHandler);

		// create about menu item - add in Help menu
		JMenuItem aboutItem = new JMenuItem("About", aboutIcon);
		helpMenu.add(aboutItem);
		aboutItem.setActionCommand("5");
		aboutItem.addActionListener(menuHandler);

		// add help menu to menu bar
		menuBar.add(helpMenu);

	}

	/**
	 * Adds to the history area a passed through message
	 * 
	 * @param message The string to be added to the history area
	 */
	public void historyAreaMessage(String message) {
		historyArea.append(message);
		historyArea.setCaretPosition(historyArea.getDocument().getLength());
	}

	/**
	 * Changes a buttons colour
	 * 
	 * @param column The button's column dimension
	 * @param row    The button's row dimension
	 * @param colour The colour to change to
	 */
	public void changeButton(int column, int row, Color colour) {
		// change the indicated button to the indicated colour
		playButtons[column][row].setBackground(colour);
	}

	/**
	 * Writes the hints in the hint areas
	 * 
	 * @param hintsTop  The hints to be written in the top hint areas
	 * @param hintsSide The hints to be written in the side hint areas
	 */
	public void writeHints(Integer[][] hintsTop, Integer[][] hintsSide) {
		int i = 0;
		int j = 0;
		String hint;

		// loop to write hints in the top hint areas
		for (i = 0; i < dimension; i++) {
			// reset hint
			hint = "";
			for (j = 0; j < 6; j++) {
				// if the hint is not 0, store at end of string
				if (hintsTop[i][j] != 0) {
					hint = hint + hintsTop[i][j].toString() + "\n";
				} else {
					// otherwise, hint is 0, put an empty space at the beginning of string
					hint = " \n" + hint;
				}
			}
			// store all the hints in the corresponding hint label for hint area
			hintTextTop[i].setText(hint);
			hintTextTop[i].setBackground(lightGreyBG);
			hintTextTop[i].setHighlighter(null);
			hintTextTop[i].setEditable(false);
			// center the hints
			StyledDocument doc = hintTextTop[i].getStyledDocument();
			SimpleAttributeSet center = new SimpleAttributeSet();
			StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
			doc.setParagraphAttributes(0, doc.getLength(), center, false);
		}

		// loop to write hints in the side hint areas
		for (i = 0; i < dimension; i++) {
			// reset hint
			hint = "";
			for (j = 0; j < 6; j++) {
				// if the hint is not 0, store at end of string
				if (hintsSide[i][j] != 0) {
					hint = hint + "   " + hintsSide[i][j].toString();
				} else {
					// otherwise, hint is 0, put an empty space at the beginning of string
					hint = "   " + hint;
				}
			}
			// store all the hints in the corresponding hint label for hint area
			hint = hint + " ";
			hintTextSide[i].setText(hint);
			hintTextSide[i].setBackground(lightGreyBG);
		}
	}

	/**
	 * Resets the history area and score
	 */
	public void reset() {
		// set each button colour to white
		int row = dimension;
		int column = dimension;
		for (row = 0; row < dimension; row++) {
			for (column = 0; column < dimension; column++) {
				playButtons[column][row].setBackground(Color.WHITE);
			}
		}
		mark.setSelected(false);
		historyArea.setText("");

		updateScore(0);
	}

	/**
	 * Updates the score
	 * 
	 * @param scoreNum The new score
	 */
	public void updateScore(Integer scoreNum) {
		score.setText(scoreNum.toString());
	}

	/**
	 * Updates the timer
	 * 
	 * @param seconds The new time
	 */
	public void updateTimer(Integer seconds) {
		time.setText(seconds.toString());
	}

	/**
	 * Creates and sets up the logo and control panel elements, including score,
	 * time, history text area and reset button
	 */
	public void setUpControlPanel(ActionListener resetButtonHandler) {
		// set up the panels and layouts
		controlPanel.setLayout(new BorderLayout());
		controlPanel.setBackground(greyBG);
		controlPanel.setPreferredSize(new Dimension(300, 650));
		// panel for scrolling history area
		JPanel historyAreaPanel = new JPanel();
		historyAreaPanel.setBackground(greyBG);
		historyAreaPanel.setLayout(new BorderLayout());
		// panel that will contain logo, and panel that contains the score and time
		JPanel topArea = new JPanel();
		topArea.setLayout(new BorderLayout());
		topArea.setBackground(greyBG);
		// panel that will contain the score and time
		JPanel scoreAndTime = new JPanel();
		scoreAndTime.setLayout(new BorderLayout());
		scoreAndTime.setBackground(greyBG);
		// panel that contains the score label and text box
		JPanel scorePanel = new JPanel();
		scorePanel.setLayout(new BorderLayout());
		scorePanel.setBackground(greyBG);
		scorePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		// panel that contains the time label and text box
		JPanel timePanel = new JPanel();
		timePanel.setLayout(new BorderLayout());
		timePanel.setBackground(greyBG);
		timePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		// panel that will contain the reset button
		JPanel resetPanel = new JPanel();
		resetPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		resetPanel.setBackground(greyBG);

		// configure the elements that will be placed in the panels
		// configure the label for score
		JLabel labelScore = new JLabel("score ");
		labelScore.setForeground(Color.white);
		labelScore.setFont(controlPanel.getFont().deriveFont(25f));

		// configure the score text box
		score = new JTextField();
		score.setEditable(false);
		score.setText("0");
		score.setBorder(blueBorder);
		score.setBackground(lightGreyBG);
		score.setForeground(Color.white);
		score.setFont(controlPanel.getFont().deriveFont(18f));
		score.setHorizontalAlignment(JTextField.CENTER);

		// configure the label for the time
		JLabel labelTime = new JLabel("time ");
		labelTime.setForeground(Color.white);
		labelTime.setFont(controlPanel.getFont().deriveFont(25f));

		// configure the text box for the time
		time = new JTextField();
		time.setEditable(false);
		time.setText("time");
		time.setBorder(blueBorder);
		time.setBackground(lightGreyBG);
		time.setForeground(Color.white);
		time.setFont(controlPanel.getFont().deriveFont(18f));
		time.setHorizontalAlignment(JTextField.CENTER);

		// configure history area text box
		historyArea.setEditable(false);
		historyArea.setFont(controlPanel.getFont().deriveFont(18f));
		historyArea.setText("");
		// historyArea.setBorder(blueBorder);
		historyArea.setBackground(lightGreyBG);
		historyArea.setForeground(Color.white);
		// historyArea.setPreferredSize(new Dimension(280, 370));

		// create scrolling pane for history area
		JScrollPane scrollingHistory = new JScrollPane(historyArea);
		scrollingHistory.setBackground(greyBG);
		scrollingHistory.setBorder(blueBorder);
		// scrollingHistory.setPreferredSize(new Dimension(280, 370));
		scrollingHistory.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

		// add history area elements
		historyAreaPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		historyAreaPanel.add(scrollingHistory, BorderLayout.CENTER);

		// configure the reset button
		JButton reset = new JButton();
		reset.addActionListener(resetButtonHandler);
		reset.setPreferredSize(new Dimension(100, 40));
		reset.setAlignmentX(Component.CENTER_ALIGNMENT);
		reset.setBackground(new Color(84, 91, 102));
		reset.setForeground(Color.white);
		reset.setBorder(blueBorder);
		reset.setText("reset");
		reset.setFont(controlPanel.getFont().deriveFont(25f));

		// Create and configure a label for the logo image
		JLabel logo = new JLabel();
		ImageIcon logoImg;
		try {
			logoImg = new ImageIcon("images/logocpl.png");
			logo.setIcon(logoImg);
			logo.setPreferredSize(new Dimension(300, 100));
		} catch (Exception e) {
			// print error and display no image
			e.printStackTrace();
		}

		// add all elements to their respective panels
		// the score
		scorePanel.add(labelScore, BorderLayout.WEST);
		scorePanel.add(score, BorderLayout.CENTER);
		// the time
		timePanel.add(labelTime, BorderLayout.WEST);
		timePanel.add(time, BorderLayout.CENTER);
		// reset button
		resetPanel.add(reset);

		// add our element panels to organizational panels
		scoreAndTime.add(scorePanel, BorderLayout.NORTH);
		scoreAndTime.add(timePanel, BorderLayout.SOUTH);
		topArea.add(logo, BorderLayout.NORTH);
		topArea.add(scoreAndTime, BorderLayout.CENTER);

		// add all the panels to the control panel that will be placed west on main
		// window
		controlPanel.add(topArea, BorderLayout.NORTH);
		controlPanel.add(historyAreaPanel, BorderLayout.CENTER);
		controlPanel.add(resetPanel, BorderLayout.SOUTH);
	}

	/**
	 * Changes the boards dimension
	 * 
	 * @param newDimension The new dimension
	 */
	public void setDimension(int newDimension) {
		dimension = newDimension;
	}

	/**
	 * Displays a dialog at the end of the game
	 * 
	 * @param score The user's score
	 */
	public void endGame(int score) {
		// get end game images
		ImageIcon victory;
		ImageIcon failure;

		// if perfect score show victory else defeat
		if (score == dimension * dimension) {
			try {
				victory = new ImageIcon("images/gamepicwinner.png");
				JOptionPane.showMessageDialog(null, null, "Victory!", JOptionPane.INFORMATION_MESSAGE, victory);
			} catch (Exception e) { // print error and no image will be displayed instead of selected image
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "You won!", "Victory!", JOptionPane.INFORMATION_MESSAGE, null);
			}
		} else {
			try {
				failure = new ImageIcon("images/gamepicend.png");
				JOptionPane.showMessageDialog(null, null, "Defeat!", JOptionPane.INFORMATION_MESSAGE, failure);
			} catch (Exception e) { // print error and no image will be displayed instead of selected image
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "You lost the game!", "Defeat!", JOptionPane.INFORMATION_MESSAGE,
						null);
			}
		}
	}

	/**
	 * Displays a dialog for about menu item
	 */
	public void aboutDialog() {
		// get end game images
		ImageIcon about;

		try {
			about = new ImageIcon("images/piccross.png");
			JOptionPane.showMessageDialog(null, null, "About", JOptionPane.INFORMATION_MESSAGE, about);
		} catch (Exception e) { // print error and no image will be displayed instead of selected image
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "About", "About", JOptionPane.INFORMATION_MESSAGE, null);
		}
	}

	/**
	 * Creates and sets up the mark check box and playing area elements, including
	 * the buttons and hint area
	 */
	public void setUpPlayingArea(ActionListener playButtonHandler, ItemListener checkBoxHandler) {
		// array for all the button names/action codes
		String[] buttonNames;
		// create an array for all the buttons, size of grid
		playButtons = new JButton[dimension][dimension];
		// create a panel to add the buttons to
		JPanel buttonGrid = new JPanel();
		buttonGrid.setLayout(new GridLayout(dimension, dimension));
		// create arrays for the hint areas
		hintAreas = new JPanel[dimension + dimension];
		hintTextTop = new JTextPane[dimension];
		hintTextSide = new JLabel[dimension];
		// variables for grid size, and looping
		int i = 0;
		int row = 0;
		int column = 0;

		// set the JPanel for the play area attributes
		playArea.setLayout(new GridBagLayout());
		playArea.setBackground(greyBG);
		playArea.setBorder(BorderFactory.createEmptyBorder());
		// gridbaglayout constraints
		GridBagConstraints c = new GridBagConstraints();

		// set amount of names
		buttonNames = new String[dimension * dimension];

		// set up the panel for the mark box
		JPanel markPanel = new JPanel();
		// create check box and mark label
		JLabel markLabel = new JLabel("mark");
		markLabel.setForeground(Color.white);
		markLabel.setFont(controlPanel.getFont().deriveFont(25f));
		// set the item listener and add
		mark.setBackground(greyBG);

		// add the mark elements to the mark panel
		markPanel.add(markLabel);
		markPanel.add(mark);
		markPanel.setBackground(greyBG);

		// add mark panel to playing area
		c.gridx = 1;
		c.gridy = 1;
		playArea.add(markPanel, c);

		// loop to set the hint area
		for (i = 0; i < (dimension * 2); i++) {
			// create a panel
			JPanel hintArea = new JPanel();

			// set attributes
			hintArea.setBackground(lightGreyBG);

			// set positioning in play area
			// if top row
			if (i < dimension) {
				hintArea.setBorder(BorderFactory.createMatteBorder(2, 2, 0, 2, greyBG));
				hintArea.setPreferredSize(new Dimension((500 / dimension), 150));
				hintArea.setLayout(new BoxLayout(hintArea, BoxLayout.PAGE_AXIS));

				c.gridy = 0; // doesnt change
				c.gridx = i + 2; // moves over one

				// size
				c.gridheight = 2;
				c.gridwidth = 1;
			} else {
				// if vertical/side hint area
				hintArea.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 0, greyBG));
				hintArea.setPreferredSize(new Dimension(150, (500 / dimension)));
				hintArea.setLayout(new BorderLayout());

				c.gridx = 0; // doesnt change
				c.gridy = i + 2 - dimension; // moves down one

				// size
				c.gridheight = 1;
				c.gridwidth = 2;
			}
			c.fill = GridBagConstraints.BOTH;

			// add text and change postioning depending on top or side
			if (i < dimension) {
				// top hints
				// create and add a text panel to top areas area
				JTextPane hintLabel = new JTextPane();
				hintLabel.setFont(controlPanel.getFont().deriveFont(18f));
				hintLabel.setForeground(Color.WHITE);

				hintLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
				hintLabel.setAlignmentY(Component.BOTTOM_ALIGNMENT);

				// add to array
				hintTextTop[i] = hintLabel;
				// add to JPanel hintArea
				hintArea.add(hintTextTop[i]);
			} else {
				// side hints
				// create and add a text panel to top areas area
				JLabel hintLabel = new JLabel("", JLabel.RIGHT);
				hintLabel.setFont(controlPanel.getFont().deriveFont(18f));
				hintLabel.setForeground(Color.WHITE);

				hintLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
				hintLabel.setAlignmentY(Component.CENTER_ALIGNMENT);

				// add to array
				hintTextSide[i - dimension] = hintLabel;
				// add to JPanel hintArea
				hintArea.add(hintTextSide[i - dimension], BorderLayout.EAST);
			}

			// add to array list
			hintAreas[i] = hintArea;

			// add to play area
			playArea.add(hintAreas[i], c);
		}

		// loop for grid size and create buttons
		for (i = 0; i < (dimension * dimension); i++) {
			// reset column after reaches gridSize
			if (column >= dimension) {
				column = 0;
				row++;
			}

			// create button name/action
			buttonNames[i] = (column + 1) + "," + (row + 1);

			// create and add button to array list
			playButtons[column][row] = createButton(buttonNames[i], playButtonHandler);

			// thicker border for outside edge
			if (column == 0 && row == 0) {
				// top left corner
				playButtons[column][row].setBorder(BorderFactory.createMatteBorder(4, 4, 2, 2, new Color(70, 70, 70)));
			} else if (column == dimension - 1 && row == 0) {
				// top right corner
				playButtons[column][row].setBorder(BorderFactory.createMatteBorder(4, 2, 2, 4, new Color(70, 70, 70)));
			} else if (column == 0 && row == dimension - 1) {
				// bottom left corner
				playButtons[column][row].setBorder(BorderFactory.createMatteBorder(2, 4, 4, 2, new Color(70, 70, 70)));
			} else if (column == dimension - 1 && row == dimension - 1) {
				// bottom right corner
				playButtons[column][row].setBorder(BorderFactory.createMatteBorder(2, 2, 4, 4, new Color(70, 70, 70)));
			} else if (column == 0) {
				// left edge
				playButtons[column][row].setBorder(BorderFactory.createMatteBorder(2, 4, 2, 2, new Color(70, 70, 70)));
			} else if (row == 0) {
				// top edge
				playButtons[column][row].setBorder(BorderFactory.createMatteBorder(4, 2, 2, 2, new Color(70, 70, 70)));
			} else if (column == dimension - 1) {
				// right edge
				playButtons[column][row].setBorder(BorderFactory.createMatteBorder(2, 2, 2, 4, new Color(70, 70, 70)));
			} else if (row == dimension - 1) {
				// bottom edge
				playButtons[column][row].setBorder(BorderFactory.createMatteBorder(2, 2, 4, 2, new Color(70, 70, 70)));
			}

			// add to panel
			buttonGrid.add(playButtons[column][row]);

			column++;
		}

		// add button grid to play area layout
		// set positions
		c.gridx = 2;
		c.gridy = 2;

		// size
		c.gridheight = dimension;
		c.gridwidth = dimension;
		c.fill = GridBagConstraints.BOTH;

		// add to play area
		playArea.add(buttonGrid, c);
	}

	/**
	 * Creates a button and sets it up for actions
	 * 
	 * @param name          The name of the button, or the position
	 * @param buttonHandler The action listener/event handler for the button
	 * @return Returns the created button
	 */
	public JButton createButton(String name, ActionListener buttonHandler) {
		// create button
		JButton button = new JButton();

		// set attributes
		button.setPreferredSize(new Dimension((500 / dimension), (500 / dimension)));
		button.setFocusable(false);
		button.setBackground(Color.white);
		button.setBorder(greyBorder);
		// button.setText(name);

		// set the action
		button.setActionCommand(name);
		button.addActionListener(buttonHandler);

		// return the created button
		return button;
	}

	/**
	 * GameSplash class that sets up the splash screen
	 * 
	 * @author mos
	 * @version 1.0
	 * @see piccross package, Piccross.java
	 * @since Java 16
	 */
	public static class GameSplash extends JWindow {
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
}