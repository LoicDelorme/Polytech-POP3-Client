package fr.polytech.pop3.client.impl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

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
		final SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		final SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(serverHost, serverPort);
		sslSocket.setEnabledCipherSuites(Arrays.stream(sslSocketFactory.getSupportedCipherSuites()).filter(cipher -> cipher.contains("anon")).toArray(size -> new String[size]));

		this.socket = sslSocket;
		this.inputStream = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		this.outputStream = new DataOutputStream(this.socket.getOutputStream());
		this.pop3CommandObservable = pop3CommandObservable;

		Platform.runLater(() -> this.pop3CommandObservable.notifyCommandResult(readCommandResult()));
	}

	/**
	 * Send a command.
	 * 
	 * @param command
	 *            The command.
	 * @return The command result.
	 */
	public String sendCommand(String command) {
		writeCommand(command);
		return readCommandResult();
	}

	/**
	 * Write a command.
	 * 
	 * @param command
	 *            The command to write.
	 */
	private void writeCommand(String command) {
		try {
			this.outputStream.writeBytes(command);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "[CLIENT] Failed to write command", e);
		}
	}

	/**
	 * Read a command result.
	 * 
	 * @return The command result.
	 */
	private String readCommandResult() {
		final StringBuilder result = new StringBuilder();
		try {
			String data = null;
			while ((data = this.inputStream.readLine()) != null && !data.equals("")) {
				result.append(data);
				result.append("\r\n");
			}
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "[CLIENT] Failed to read command result", e);
		}

		return result.toString();
	}
}