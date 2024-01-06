/*
 * File name: GameServer.java
 * Author: Mostapha A
 * Purpose: Creates and Manages a game server
 * Class list: ButtonHandler, CheckBoxHandler, Connection
 */

package piccross;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * GameServer class that sets up a piccross server
 * 
 * @author mos
 * @version 1.0
 * @see piccross package
 * @since Java 16
 */
public class GameServer implements Runnable {
	/** The input box for port number */
	private static JTextField portInput;
	/** The button to execute */
	private static JButton execute;
	/** The text area for all the information to be printed */
	private static JTextArea console;
	/** The port number */
	private static int portNum;
	/** The socket for the connection */
	private static Socket sock;
	/** The current client and total clients */
	static int nclient = 0, nclients = 0;
	/** The server socket for the connections */
	static ServerSocket servsock;
	/** The finalize checkbox */
	private static JCheckBox finalize;
	/** The button to print results */
	private static JButton results;
	/** The current game configuration */
	static String gameConfig = "0";
	/** The array of all client information */
	private static ArrayList<String[]> allInfo = new ArrayList<String[]>();

	/**
	 * Main function that calls function to create the GUI
	 * @param args
	 */
	public static void main(String[] args) {
		// call the function that creates the gui
		create();
	}

	/**
	 * Creates GUI elements
	 */
	private static void create() {
		// create action listener
		ButtonHandler buttonHandler = new ButtonHandler();

		// create the window
		JFrame window = new JFrame("Piccross server - Mostapha");
		window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		// print a message if they try to close from the window button
		window.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				write("Must close the server with the \"End\" button");
			}
		});

		// create the text area and scrollpane
		console = new JTextArea();
		JScrollPane consoleScroll = new JScrollPane(console);
		console.setEditable(false);
		console.setText("");

		// create the buttons and text options
		JLabel portLabel = new JLabel("Port:");
		portInput = new JTextField("1234", 6);
		execute = new JButton("Execute");
		results = new JButton("Results");
		results.setEnabled(false);
		JButton end = new JButton("End");
		finalize = new JCheckBox("Finalize");

		execute.addActionListener(buttonHandler);
		results.addActionListener(buttonHandler);
		end.addActionListener(buttonHandler);
		finalize.addItemListener(new CheckBoxHandler());

		// add option elements to a panel
		JPanel options = new JPanel();
		options.add(portLabel);
		options.add(portInput);
		options.add(execute);
		options.add(results);
		options.add(finalize);
		options.add(end);

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
	}

	/**
	 * Writes a message to the system console and GUI console
	 * @param message The message to write
	 */
	private static void write(String message) {
		// write the message to console
		console.append(message + "\n");
		System.out.println(message);
	}

	/** Attempts to connect with a new thread */
	public static void connect() throws IOException {
		try {
			servsock = new ServerSocket(portNum);
			Thread newThread = new Thread(new GameServer());
			newThread.start();
			write("Server on " + InetAddress.getLocalHost() + " port " + portNum);
			// grey out execute button
			execute.setEnabled(false);
			results.setEnabled(true);
		} catch (Exception e) {
			System.out.println(e);
			write("Port Number in use try again");
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
			case "Execute":
				// get the port value
				String input = portInput.getText();

				// validate input
				if (input.length() > 5 || input.isBlank() || !input.matches("[0-9]+")) {
					write("Port must be an integer number from 0 to 66535, try again");
				} else if (Integer.parseInt(input) < 0 || Integer.parseInt(input) > 66535) {
					write("Port must be an integer number from 0 to 66535, try again");
				} else {
					// if it is valid establish server on port
					portNum = Integer.parseInt(input);
					try {
						connect();

					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

				break;
			case "Results":
				// print info
				printInfo();
				break;
			case "End":
				// close server
				try {
					if (nclients > 0)
					sock.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				System.exit(0);
				break;
			}
		}

	} // end button handler class

	/**
	 * Inner item listener class for managing the finalize check box
	 * 
	 * @author mos
	 * @version 1.0
	 * @since Java 16
	 * @see GameServer
	 */
	private static class CheckBoxHandler implements ItemListener {
		/**
		 * This is called when the finalize check box is changed
		 * 
		 * @param e The event object with the event information
		 */
		@Override
		public void itemStateChanged(ItemEvent e) {
			// print message depending on status
			if (finalize.isSelected() == true) {
				write("Server will close when there are no more connections");
				if (nclients == 0) {
					System.exit(0);
				}
			} else {
				write("Server will remain open when there are no more connections");
			}

		}
	}// end check box handler class

	/**
	 * Prints the information in the array of client information
	 */
	public static void printInfo() {
		// loop through our clients and print information
		String scoreTime;
		// if we have no clients write so
		if (allInfo.size() == 0) {
			write("No clients connected");
		}
		for (String[] client : allInfo) {
			// if they have no score and time print info accordingly
			if (Integer.parseInt(client[1]) == 0) {
				scoreTime = "has not finished a game";
			} else {
				scoreTime = "last played game took " + client[1] + " seconds and scored " + client[2] + " points";
			}
			// write each clients information
			write("Client " + client[3] + " (" + client[0] + ") " + scoreTime);
		}
	}

	/**
	 * The method that will run with a new thread, attempts to connect to a client
	 */
	public void run() {
		// loop indefinitely
		for (;;) {
			// try to establish a connection
			try {
				// new client
				sock = servsock.accept();
				nclient += 1;
				nclients += 1;
				write("Connecting " + sock.getInetAddress() + " in port " + sock.getPort());
			} catch (IOException ioe) {
				System.out.println(ioe);
			}
			// new object that recieves info from client
			Connection clientConnection = new Connection(sock, nclient);
			clientConnection.start();
		}
	}

	/**
	 * The class that will take in all input and handle communications
	 * @author moabd
	 *
	 */
	class Connection extends Thread {
		/** Socket for connection */
		Socket sock;
		/** The unique client number */
		Integer clientid;
		/** The clients name */
		String clientName;
		/** A descriptor of the information received */
		String infoType;
		/** An array for the clients information */
		String[] clientInfo = new String[4];

		/**
		 * The constructor of the class
		 * @param socket Previously established socket
		 * @param nclient The unique client number
		 */
		public Connection(Socket socket, int nclient) {
			// receive the socket previously established a connection with
			sock = socket;
			clientid = nclient;
		}

		/**
		 * Handles all communications
		 */
		public void run() {
			String clientData;
			String returnInfo;
			PrintStream clientOutput = null;
			try {
				// set the stream we will be printing to
				clientOutput = new PrintStream(sock.getOutputStream());
				// set the reader we will be receiving from
				BufferedReader clientInput = new BufferedReader(new InputStreamReader(sock.getInputStream()));
				// send the client their client number
				clientOutput.println(clientid);
				// receive first info back
				clientData = clientInput.readLine();

				// initialize our client info array
				clientInfo[0] = "No name recorded";
				clientInfo[1] = "0";
				clientInfo[2] = "0";
				clientInfo[3] = clientid.toString();
				// store our client in all information array
				allInfo.add(clientInfo);

				// loop until end protocol
				while (!clientData.equals(clientid + "#P0#0")) {
					// default return value
					returnInfo = "0";
					// case structure depending on input
					String[] input = clientData.split("#");
					switch (input[1]) {
					case "P1":
						// receiving game configuration
						gameConfig = input[2];
						infoType = "a game configuration (" + gameConfig + ")";
						break;
					case "P2":
						// receiving user name
						clientInfo[0] = input[2];
						clientName = input[2];
						infoType = "their name";
						break;
					case "P3":
						// receiving time and score
						clientInfo[1] = input[2];
						clientInfo[2] = input[3];
						infoType = "their time (" + clientInfo[1] + ") and score (" + clientInfo[2] + ")";
						break;
					case "P4":
						// receiving a request for a game configuration
						returnInfo = gameConfig;
					default:

						break;
					}
					// write to console what was received
					write("Client " + clientid + " (" + clientName + ") sent " + infoType);
					// send something back
					clientOutput.println(clientid + "#" + returnInfo);
					
					// update info array
					int i = 0;
					for(i = 0; i < allInfo.size(); i++) {
						if (allInfo.get(i)[3].equals(clientid.toString())) {
							allInfo.set(i, clientInfo);
							break;
						}
					}

					// flush and receive next input
					clientOutput.flush();
					clientData = clientInput.readLine();
				}
				// if they sent end protocol
				write("Disconnecting client " + clientid + " (" + clientName + ") at " + sock.getInetAddress());
				clientOutput.println(clientid + "#Closing Connection");
				// close socket
				sock.close();
				
				// decrease client number
				nclients -= 1;
				write("There are " + nclients + " clients connected");
				
				// if finalize is checked and there are no clients automatically close the
				// server
				if (finalize.isSelected() && nclients == 0) {
					write("Closing server...");
					System.exit(0);
				}
			} catch (IOException ioe) {
				System.out.println(ioe);
			}
		}
	}
}
