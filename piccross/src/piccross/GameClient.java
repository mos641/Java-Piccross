/*
 * File name: GameServer.java
 * Author: Mostapha A
 * Purpose: Creates and Manages a game client
 * Class list: gameView.java, gameModel.java, gameController.java, DrawGame, ButtonHandler
 */

package piccross;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * GameClient class that sets up a piccross client
 * 
 * @author mos
 * @version 1.0
 * @see piccross package, Game.java, GameModel.java, GameView.java
 * @since Java 16
 */
public class GameClient {
	/** The console to write information to */
	private static JTextArea console;
	/** The button to initiate connection */
	private static JButton connect;
	/** The text box for port number input */
	private static JTextField portInput;
	/** The text box for user name input */
	private static JTextField userInput;
	/** The text box for server name input */
	private static JTextField serverNameInput;
	/** The port number */
	private static int portNum;
	/** The GameView object */
	private static GameView gameView;
	/** The GameModel object */
	private static GameModel gameModel;
	/** The game controller object */
	private static GameController gameController;
	/** The server name */
	private static String serverName;
	/** The socket for connections */
	private static Socket socket;
	/** buffered reader for information from server */
	private static BufferedReader serverInput;
	/** print stream to send information to server */
	private static PrintStream serverOutput;
	/** get the client id from server */
	private static String clientId;
	/** a counter for the play button for logic purposes */
	private static int game = 0;
	/** To keep track whether we are connected */
	private static boolean connected = false;

	/**
	 * Main function that calls function to create GUI elements
	 * @param args
	 */
	public static void main(String[] args) {
		create();
	} // end main

	/**
	 * Creates the GUI elements
	 */
	private static void create() {
		// create action listener
		ButtonHandler buttonHandler = new ButtonHandler();

		// create the window
		JFrame window = new JFrame("Piccross client - Mostapha");
		window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		// print a message if they try to close from the window button
		window.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				write("Must close the client with the \"End\" button");
			}
		});

		// create the text area and scrollpane
		console = new JTextArea();
		JScrollPane consoleScroll = new JScrollPane(console);
		console.setEditable(false);
		console.setText("");

		// create the buttons and text options
		JLabel userLabel = new JLabel("User:");
		userInput = new JTextField("user", 10);
		JLabel serverLabel = new JLabel("Server:");
		serverNameInput = new JTextField("localhost", 10);
		JLabel portLabel = new JLabel("Port:");
		portInput = new JTextField("1234", 6);
		connect = new JButton("Connect");
		JButton end = new JButton("End");

		JButton newGame = new JButton("New Game");
		JButton drawGame = new JButton("Draw Game");
		JButton sendGame = new JButton("Send Game");
		JButton receiveGame = new JButton("Receive Game");
		JButton sendData = new JButton("Send Data");
		JButton play = new JButton("Play");

		// add listeners
		connect.addActionListener(buttonHandler);
		end.addActionListener(buttonHandler);
		newGame.addActionListener(buttonHandler);
		drawGame.addActionListener(buttonHandler);
		sendGame.addActionListener(buttonHandler);
		receiveGame.addActionListener(buttonHandler);
		sendData.addActionListener(buttonHandler);
		play.addActionListener(buttonHandler);

		// add option elements to 2 panels and a main panel
		JPanel info = new JPanel();
		JPanel buttons = new JPanel();
		info.add(userLabel);
		info.add(userInput);
		info.add(serverLabel);
		info.add(serverNameInput);
		info.add(portLabel);
		info.add(portInput);
		info.add(connect);
		info.add(end);

		buttons.add(newGame);
		buttons.add(drawGame);
		buttons.add(sendGame);
		buttons.add(receiveGame);
		buttons.add(sendData);
		buttons.add(play);

		JPanel options = new JPanel();
		options.setLayout(new BorderLayout());
		options.add(info, BorderLayout.NORTH);
		options.add(buttons, BorderLayout.CENTER);

		// set window layout and add elements
		window.setLayout(new BorderLayout());
		window.add(options, BorderLayout.NORTH);
		window.add(consoleScroll, BorderLayout.CENTER);

		// set size and show
		window.setSize(630, 350);
		window.setResizable(false);
		window.setVisible(true);
		window.setLocationRelativeTo(null);
		// window.pack();

		// create game elements
		gameView = new GameView();
		gameModel = new GameModel();
		gameController = new GameController(gameView, gameModel);
	}

	/**
	 * Writes a message to system and GUI console
	 * @param message The message to write
	 */
	private static void write(String message) {
		// write the message to console
		console.append(message + "\n");
		System.out.println(message);
	}

	/**
	 * Attempts to connect to the server
	 * @return Return a boolean for connection status
	 */
	private static boolean connect() {
		// create a socket with port number and server name
		serverName = serverNameInput.getText();

		try {
			// socket for connection
			socket = new Socket(serverName, portNum);
			// buffered reader for information from server
			serverInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			// print stream to send information to server
			serverOutput = new PrintStream(socket.getOutputStream());
			// get the client id from server
			clientId = serverInput.readLine();
			// write we are connected
			write("We are client " + clientId + " connected in server");
			connected = true;
			return true;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			write("Host name is invalid");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			write("Port Number is invalid");
		} catch (Exception e) {
			e.printStackTrace();
			write("Unknown error");
		}
		connected = false;
		return false;
	}

	/**
	 * Attempts to send data to the server
	 * @param protocol Protocol number for communication purposes
	 * @param data The data being sent
	 * @throws IOException Disconnection exception
	 */
	private static void sendData(int protocol, String data) throws IOException {
		// set output to data taken in
		String output = data;
		String input;
		try {
			write("Sending " + output);
			// add necessary format
			output = clientId + "#P" + protocol + "#" + output;
			// send to server
			serverOutput.println(output);
			serverOutput.flush();
			// receive information from server
			input = serverInput.readLine();
			String[] splitInput = input.split("#");
			// write("Server sent: " + input);

			// case structure depending on protocol
			switch (protocol) {
			// cases 0 - 4 not receiving anything
			case 0:
				// end game protocol
				write("Closing connection...");
				break;
			case 1:
			case 2:
			case 3:
				write(data + " sent");
				break;
			case 4:
				// receiving game configuration
				if (splitInput[1].equals("0")) {
					write("Server has no saved game");
				} else {
					gameModel.generateBoard(splitInput[1]);
					write("Received game " + gameModel.getString());
				}
				break;
			case 5:
				break;
			}

			/*
			 * write("Client[" + clientId + "]: ");
			 * 
			 * output = clientId + "#" + output; serverOutput.println(output);
			 */

			serverOutput.flush();
			// socket.close();

		} catch (Exception e) {
			System.out.println(e);
			// set connected to false and tell user we are disconnected
			write("Connection to server lost");
			connected = false;
			connect.setEnabled(true);
		}
	}

	/**
	 * Creates the window to draw a game configuration
	 * @author moabd
	 *
	 */
	private static class DrawGame implements ActionListener {
		/** The dimension of the drawing grid */
		private static int dimension = 5;
		/** The array of buttons */
		private static JButton[][] playButtons;
		/** The panel for the buttons */
		private static JPanel buttonGrid;
		/** The combo box for grid options */
		private static JComboBox options;
		/** The array for configuration string */
		private static int[][] configuration;
		/** The frame for the draw window */
		private static JFrame drawGame;

		/**
		 * creates the GUI elements of the draw window
		 */
		private void drawGame() {
			// create a frame
			drawGame = new JFrame();
			// create a combo box for options
			JPanel sizeSelection = new JPanel();
			JLabel enterSize = new JLabel("Choose board size:");
			String[] sizes = { "2", "3", "4", "5", "6", "7", "8", "9", "10" };
			options = new JComboBox(sizes);
			options.setSelectedIndex(3);
			options.addActionListener(this);
			sizeSelection.add(enterSize);
			sizeSelection.add(options);
			// create array for game configuration
			configuration = new int[dimension][dimension];
			// create a button to save
			JButton save = new JButton("save");
			save.addActionListener(this);

			// create a panel to add the buttons to
			buttonGrid = new JPanel();

			// create the grid
			makeGrid();

			// add components
			drawGame.add(sizeSelection, BorderLayout.NORTH);
			drawGame.add(save, BorderLayout.SOUTH);
			drawGame.add(buttonGrid, BorderLayout.CENTER);

			drawGame.setResizable(false);
			drawGame.setVisible(true);
			drawGame.setLocationRelativeTo(null);
			drawGame.pack();
		}

		/**
		 * Creates a button and sets it up for actions
		 * 
		 * @param name          The name of the button, or the position
		 * @return Returns the created button
		 */
		public JButton createButton(String name) {
			// create button
			JButton button = new JButton();

			// set attributes
			button.setPreferredSize(new Dimension((500 / dimension), (500 / dimension)));
			button.setFocusable(false);
			button.setBackground(Color.white);
			button.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(70, 70, 70)));
			// button.setText(name);

			// set action
			button.addActionListener(this);
			button.setActionCommand(name);
			// return the created button
			return button;
		}

		/**
		 * Creates the button grid depending on the selected dimension
		 */
		private void makeGrid() {
			// create an array for all the buttons, size of grid
			playButtons = new JButton[dimension][dimension];
			buttonGrid.setLayout(new GridLayout(dimension, dimension));

			// set amount of names
			String[] buttonNames = new String[dimension * dimension];
			// loop for grid size and create buttons
			int i = 0;
			int row = 0;
			int column = 0;
			for (i = 0; i < (dimension * dimension); i++) {
				// reset column after reaches gridSize
				if (column >= dimension) {
					column = 0;
					row++;
				}

				// create button name/action
				buttonNames[i] = (column + 1) + "," + (row + 1);

				// create and add button to array list
				playButtons[column][row] = createButton(buttonNames[i]);

				// thicker border for outside edge
				if (column == 0 && row == 0) {
					// top left corner
					playButtons[column][row]
							.setBorder(BorderFactory.createMatteBorder(4, 4, 2, 2, new Color(70, 70, 70)));
				} else if (column == dimension - 1 && row == 0) {
					// top right corner
					playButtons[column][row]
							.setBorder(BorderFactory.createMatteBorder(4, 2, 2, 4, new Color(70, 70, 70)));
				} else if (column == 0 && row == dimension - 1) {
					// bottom left corner
					playButtons[column][row]
							.setBorder(BorderFactory.createMatteBorder(2, 4, 4, 2, new Color(70, 70, 70)));
				} else if (column == dimension - 1 && row == dimension - 1) {
					// bottom right corner
					playButtons[column][row]
							.setBorder(BorderFactory.createMatteBorder(2, 2, 4, 4, new Color(70, 70, 70)));
				} else if (column == 0) {
					// left edge
					playButtons[column][row]
							.setBorder(BorderFactory.createMatteBorder(2, 4, 2, 2, new Color(70, 70, 70)));
				} else if (row == 0) {
					// top edge
					playButtons[column][row]
							.setBorder(BorderFactory.createMatteBorder(4, 2, 2, 2, new Color(70, 70, 70)));
				} else if (column == dimension - 1) {
					// right edge
					playButtons[column][row]
							.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 4, new Color(70, 70, 70)));
				} else if (row == dimension - 1) {
					// bottom edge
					playButtons[column][row]
							.setBorder(BorderFactory.createMatteBorder(2, 2, 4, 2, new Color(70, 70, 70)));
				}

				// add to panel
				buttonGrid.add(playButtons[column][row]);

				column++;
			}

		}

		/**
		 * Creates the configuration string based on the selected buttons
		 */
		private void createString() {
			String config = "";
			// loop through config array and create configuration string
			int x = 0;
			int y = 0;
			for (y = 0; y < dimension; y++) {
				for (x = 0; x < dimension; x++) {
					// create the string depending on which buttons are selected
					if (configuration[x][y] == 0) {
						config += "0";
					} else {
						config += "1";
					}
				}
				// add comma unless it is the last row
				if (y < dimension - 1) {
					config += ",";
				}
			}
			// change the game config string
			gameModel.generateBoard(config);
		}

		/**
		 * Listens to actions on the items and performs functions depending on item selected
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			// if they choose a new dimension re-draw the grid
			if (e.getActionCommand().equals("comboBoxChanged")) {
				// set new dimension and redraw
				dimension = Integer.parseInt(options.getSelectedItem().toString());
				buttonGrid.removeAll();
				makeGrid();
				buttonGrid.revalidate();
				buttonGrid.repaint();
				drawGame.pack();
				configuration = new int[dimension][dimension];
			} else if (e.getActionCommand().equals("save")) {
				// if they click save set the game string, print and close
				createString();
				drawGame.setVisible(false);
				drawGame.dispose();
				write("New game " + gameModel.getString());
			} else {
				// otherwise change the game configuration string
				String[] coordinate = e.getActionCommand().split(",");
				int x = Integer.parseInt(coordinate[0]) - 1;
				int y = Integer.parseInt(coordinate[1]) - 1;
				// check if button is unselected
				if (configuration[x][y] == 0) {
					// change color and set to selected
					configuration[x][y] = 1;
					playButtons[x][y].setBackground(new Color(97, 197, 255));
				} else {
					// change color and set to unselected
					configuration[x][y] = 0;
					playButtons[x][y].setBackground(Color.WHITE);
				}
			}
		}
	}

	/**
	 * Inner action listener class for managing the buttons
	 * 
	 * @author mos
	 * @version 1.0
	 * @since Java 16
	 * @see GameServer
	 */
	private static class ButtonHandler implements ActionListener {
		/**
		 * This is called when one of the buttons is clicked, it will execute a function
		 * depending on the button used
		 * 
		 * @param e The event object with the event information
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			// store which button was pressed
			String button = e.getActionCommand();
			// case structure to do different things depending on the button
			switch (button) {
			// check connection first with each case
			case "Connect":
				// get the port value
				String input = portInput.getText();
				write("Connecting to server...");
				// validate input
				if (input.length() > 5 || input.isBlank() || !input.matches("[0-9]+")) {
					write("Port must be an integer number from 0 to 66535, try again");
				} else if (Integer.parseInt(input) < 0 || Integer.parseInt(input) > 66535) {
					write("Port must be an integer number from 0 to 66535, try again");
				} else if (userInput.getText().equals("")){
					write("Enter a username, try again");
				}else {
					// if it is valid establish server on port
					portNum = Integer.parseInt(input);
					try {
						connected = connect();
						// if we are connected act accordingly
						if (connected == true) {
							// send our name
							sendData(2, userInput.getText());
							// set connect button to not be enabled
							connect.setEnabled(false);
						} else {
							write("We are not connected to a server");
						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				break;
			case "End":
				try {
					// send end protocol
					if (connected == true) {
						sendData(0, "0");
						System.exit(0);
					} else {
						System.exit(0);
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					System.exit(0);
				}
				break;
			case "New Game":
				gameModel.generateString();
				
				write("New game " + gameModel.getString());
				break;
			case "Draw Game":
				// create a new game based on their selections
				DrawGame draw = new DrawGame();
				draw.drawGame();
				break;
			case "Send Game":
				// send the config string if it is not empty
				if (gameModel.getString().equals("0")) {
					write("No game stored, create a new game or receive one first");
				} else {
					// send the configuration
					try {
						if (connected == true) {
							sendData(1, gameModel.getString());
						} else {
							write("We are not connected to a server");
						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				break;
			case "Receive Game":
				// send a request for a configuration
				try {
					if (connected == true) {
						sendData(4, "0");
						write("Current game is " + gameModel.getString());
					} else {
						write("We are not connected to a server");
					}
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				break;
			case "Send Data":
				try {
					if (connected == true) {
						sendData(3, gameController.returnInfo());
					} else {
						write("We are not connected to a server");
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				break;
			case "Play":
				// start the game if we have a configuration
				if (gameModel.getString().equals("0")) {
					write("No game stored, create a new game or receive one first");
				} else {
					if (game > 0) {
						int dimension = gameModel.getDimension();
						String tempGameConfig = gameModel.getString();
						
						// reset view and model
						gameView.reset();
						gameModel.reset();
						gameController.resetController();
						
						// set dimensions
						gameView.setDimension(dimension);
						gameModel.setDimension(dimension);
						gameController.setDimension(dimension);

						// reset play area
						gameController.createNewGame();
						
						// create data for game logic
						gameModel.generateBoard(tempGameConfig);

						// setup hints
						gameController.setupHints();
						gameView.setVisible();
						
					} else {
						gameController.startGame(gameModel.getString());
					}
					write("Starting game " + gameModel.getString());		
					game++;
				}
				break;
			}
		}

	} // end button handler class

}
