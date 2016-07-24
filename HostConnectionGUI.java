import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/* Server Application that interacts with a client application (Android ConnectToHost). */

public class HostConnectionGUI {

	ServerSocket serverSocket;
	Socket socket;
	PrintWriter outputStream;
	BufferedReader inputStream;

	private int port;
	Scanner inputStream2;
	
	char keysTyped;
	String keysTypedStr = ""; // *** Must be instantiated, otherwise the first char will be considered null!
	
	JTextArea textArea;
	
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		// Instantiate class.
		HostConnectionGUI classObj = new HostConnectionGUI();
		
		// Instantiate JPanel (GUI) inner class.
		// * This invokes the default constructor, which runs the GUI element. // No need to run a particular ServerGUI method. 
		HostConnectionGUI.ServerGUI innerClassObj = classObj.new ServerGUI();
		
		// Call first networking method using class object/instance.
		classObj.openSocket();
	}
	
	
	// Open Socket.
	private void openSocket() throws IOException, ClassNotFoundException {
		port = 5501;
		try {
			serverSocket = new ServerSocket(port);
			System.out.println("Server Socket Open.");
		}
		catch (IOException ioExcep) {
			System.err.println("Input/Output Exception occurred" + ioExcep);
		}
		
		acceptConnection();
	}
		
	// Listen and accept connection.
	private void acceptConnection() throws IOException, ClassNotFoundException {
		try {
			socket = serverSocket.accept();
			System.out.println("Server Socket ready to accept Client connection.");
		} 
		catch (IOException ioExcep) {
			System.out.println(ioExcep);
		}
		
		ioStreams();
	}
	
	
	// Open output and input streams.
	private void ioStreams() throws IOException, ClassNotFoundException {
		// Open Output Stream.
		outputStream = new PrintWriter(socket.getOutputStream(), true);
		
		// Open Input Stream.
		inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		inputStream2 = new Scanner(socket.getInputStream());
		
		System.out.println("I/O Streams Open.");
	}

		
	// Close I/O Streams and Socket.
	private void close() throws IOException {
		outputStream.close();
		inputStream.close();
		inputStream2.close();
		socket.close();
		serverSocket.close();
		System.out.println("Connection closed");
		
		System.exit(1);
	}
	
	
	
	private class ServerGUI extends JPanel implements ActionListener, KeyListener {
		
		private JFrame frame;
		private JButton button1;
		private JTextField textField;
		
		ServerGUI() {
			// Creation of JFrame object.
			frame = new JFrame("Server");
			
			// Frame Layout management.
			FlowLayout flowLayout = new FlowLayout();
			//frame.setLayout(flowLayout); // Places JPanel components in a row.
			
			// frame window height and width variables.
			int frameWidth = 500;
			int frameHeight = 500;
			
			// Instantiate frame components.
			button1 = new JButton("Broadcast Message");
			JButton button2 = new JButton("Receive Message");
			JButton button3 = new JButton("Close Connection");
			textField = new JTextField();
			textArea = new JTextArea();
			
			// Button layout management.
			JPanel buttonCentre = new JPanel(new FlowLayout(FlowLayout.CENTER));
			buttonCentre.add(button2);
			buttonCentre.add(button1);
			buttonCentre.add(button3);
			
			// Adding components to the frame window + Utilising BorderLayout for layout managing.
			frame.add(this, BorderLayout.CENTER); // "this" refers to the object of this JPanel class. Equivalent to -> ServerGUI obj = new ServerGUI();
			frame.add(buttonCentre, BorderLayout.SOUTH);
			frame.add(textField, BorderLayout.NORTH);
			frame.add(textArea);
			
			// Shift the position of the frame towards the centre, instead of the top-most position.
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			int screenWidth = (int) screenSize.getWidth()/3;
			int screenHeight = (int) screenSize.getHeight()/3;
			frame.setLocation(screenWidth, screenHeight);
			
			// Setup of Frame window.
			frame.setSize(frameWidth, frameHeight); // Set frame size parameters - width & height.
			frame.setVisible(true);
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // When frame closes, it exits - Preventing dialog from reappearing once app window is closed.
			
			// Adding ActionListener to Button1 - where the action executed upon button-press is handled. // "actionPerformed" handles button1 action event.
			button1.addActionListener(this);
			
			// Handling the event of interacting with Button 2.
			button2.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// Display Client input on JPanel
					String clientInput;
					if ( (clientInput = inputStream2.nextLine()) != null ) { 
						textArea.insert("\n" + clientInput, 0);
					}
					
				}
			});
			
			button3.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed (ActionEvent e) {
					try {
						close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			});
			
			// Allowing KeyListener interface to receive key (keyboard) events from JTextField (text editor).
			textField.addKeyListener(this);
			 
		}

		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);	
		}

		
		// "actionPerformed" - callback method for "ActionListener" interface, it is invoked with components that are added to the ActionListner using "addActionListener".
		// * In this case, this method is invoked by Button1.
		@Override
		public void actionPerformed(ActionEvent e) {	
			// Print user input in the console.
			System.out.println(keysTypedStr);
			// Write message to Client.
			outputStream.println(keysTypedStr);
			// *** String storing message must be emptied after broadcasting message, otherwise previous messages will be appended to current message! 
			keysTypedStr = "";
		}
		

		// "keyTyped" - callback method for KeyListener interface. // It handles the events of keyboard keys being typed.
		@Override
		public void keyTyped(KeyEvent e) {				
			keysTyped = e.getKeyChar();
			keysTypedStr += keysTyped;
		}
		
		@Override
		public void keyPressed(KeyEvent e) {
		}

		@Override
		public void keyReleased(KeyEvent e) {			
		}				
		
	}
	
	
}
