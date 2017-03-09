package fr.polytech.pop3.client.impl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.polytech.pop3.client.ui.Pop3CommandObservable;
import javafx.application.Platform;

/**
 * This class represents a POP 3 client.
 *
 * @author DELORME Loïc
 * @since 1.0.0
 */
public class Pop3Client {

	/**
	 * The logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(Pop3Client.class.getName());

	/**
	 * The socket.
	 */
	private final Socket socket;

	/**
	 * The input stream.
	 */
	private final BufferedReader inputStream;

	/**
	 * The output stream.
	 */
	private final DataOutputStream outputStream;

	/**
	 * The POP 3 command observable.
	 */
	private final Pop3CommandObservable pop3CommandObservable;

	/**
	 * Create a POP 3 client.
	 * 
	 * @param serverHost
	 *            The server host.
	 * @param serverPort
	 *            The server port.
	 * @param pop3CommandObservable
	 *            The POP 3 command observable.
	 * @throws IOException
	 *             If an error occurs.
	 */
	public Pop3Client(String serverHost, int serverPort, Pop3CommandObservable pop3CommandObservable) throws IOException {
		this.socket = new Socket(serverHost, serverPort);
		this.inputStream = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		this.outputStream = new DataOutputStream(this.socket.getOutputStream());
		this.pop3CommandObservable = pop3CommandObservable;

		final String welcomingMessage = read();
		Platform.runLater(() -> this.pop3CommandObservable.notifyCommandResult(welcomingMessage));
	}

	/**
	 * Send a command.
	 * 
	 * @param command
	 *            The command.
	 * @return The command result.
	 */
	public String sendCommand(String command) {
		write(command);
		return read();
	}

	/**
	 * The write method.
	 * 
	 * @param command
	 *            The command to write.
	 */
	private void write(String command) {
		try {
			this.outputStream.writeBytes(command + "\n");
			LOGGER.log(Level.INFO, command);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Failed to write command", e);
		}
	}

	/**
	 * The read method.
	 * 
	 * @return The read String.
	 */
	private String read() {
		final StringBuilder result = new StringBuilder();
		try {
			String data;
			while (((data = this.inputStream.readLine()) != null) && (!data.equals(""))) {
				result.append(data);
				result.append("\n");
			}

		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Failed to read command result", e);
		}

		return result.toString();
	}
}