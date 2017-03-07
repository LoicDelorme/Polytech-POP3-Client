package fr.polytech.pop3.client;

import java.io.BufferedReader;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents a POP 3 client.
 *
 * @author DELORME Loïc
 * @since 1.0.0
 */
public class Pop3Client extends Thread implements Pop3CommandObserver {

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
	private final DataOutput outputStream;

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
		super();
		this.socket = new Socket(serverHost, serverPort);
		this.inputStream = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		this.outputStream = new DataOutputStream(this.socket.getOutputStream());
		this.pop3CommandObservable = pop3CommandObservable;
	}

	@Override
	public void notifyCommand(String command) {
		try {
			this.outputStream.writeBytes(command.trim());
			LOGGER.log(Level.INFO, command);

			final String commandResult = this.inputStream.readLine();
			LOGGER.log(Level.INFO, commandResult);

			this.pop3CommandObservable.notifyCommandResult(commandResult.trim());
		} catch (IOException e) {
			this.pop3CommandObservable.notifyCommandResult(e.getMessage());
		}
	}
}