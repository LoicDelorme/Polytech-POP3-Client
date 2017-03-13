package fr.polytech.pop3.client.ui;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;

import fr.polytech.pop3.client.impl.Pop3Client;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * This class represents a POP 3 command prompter controller.
 *
 * @author DELORME Loïc
 * @since 1.0.0
 */
public class Pop3CommandPrompterController implements Initializable, Pop3CommandObservable {

	/**
	 * The hostname input text field.
	 */
	@FXML
	private TextField hostnameInputTextField;

	/**
	 * The port input text field.
	 */
	@FXML
	private TextField portInputTextField;

	/**
	 * The connect button.
	 */
	@FXML
	private Button connectButton;

	/**
	 * The connected circle.
	 */
	@FXML
	private Circle connectedCircle;

	/**
	 * The output text area.
	 */
	@FXML
	private TextArea ouputTextArea;

	/**
	 * The command input text field.
	 */
	@FXML
	private TextField commandInputTextField;

	/**
	 * The send command button.
	 */
	@FXML
	private Button sendCommandButton;

	/**
	 * The POP 3 client.
	 */
	private Pop3Client pop3Client;

	/**
	 * The is connected flag.
	 */
	private BooleanProperty isConnected;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.isConnected = new SimpleBooleanProperty(false);

		this.hostnameInputTextField.disableProperty().bind(this.isConnected);
		this.portInputTextField.disableProperty().bind(this.isConnected);
		this.connectButton.disableProperty().bind(this.hostnameInputTextField.textProperty().isEmpty().or(this.portInputTextField.textProperty().isEmpty()).or(this.isConnected));
		this.commandInputTextField.disableProperty().bind(this.isConnected.not());
		this.sendCommandButton.disableProperty().bind(this.isConnected.not().or(this.commandInputTextField.textProperty().isEmpty()));

		this.isConnected.addListener((object, oldValue, newValue) -> {
			this.connectedCircle.setFill(newValue ? Color.LIMEGREEN : Color.RED);
		});
		this.connectButton.setOnAction(event -> onConnectAction());
		this.commandInputTextField.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				onCommandAction("\n");
			}
		});
		this.sendCommandButton.setOnAction(event -> onCommandAction("\n"));
	}

	/**
	 * The on connect action.
	 */
	private void onConnectAction() {
		try {
			final String hostname = this.hostnameInputTextField.getText().trim();
			final int port = Integer.parseInt(this.portInputTextField.getText().trim());

			this.pop3Client = new Pop3Client(hostname, port, this);
			this.isConnected.set(true);

			this.ouputTextArea.clear();
		} catch (Exception e) {
			final Writer writer = new StringWriter();
			e.printStackTrace(new PrintWriter(writer));

			this.ouputTextArea.appendText(writer.toString() + "\r\n");
		}
	}

	/**
	 * The on command action.
	 * 
	 * @param additionnalCharacter
	 *            The additionnal character to add at the end of the command.
	 */
	private void onCommandAction(String additionnalCharacter) {
		final String typedCommand = this.commandInputTextField.getText().trim();
		if (typedCommand.startsWith("/digest")) {
			try {
				final String input = typedCommand.split(" ")[1];
				this.ouputTextArea.appendText("[DIGEST] " + input + "\r\n");

				final byte[] digest = MessageDigest.getInstance("MD5").digest(input.getBytes());
				final StringBuilder footprint = new StringBuilder();
				for (byte b : digest) {
					footprint.append(String.format("%02x", b));
				}

				this.commandInputTextField.clear();
				this.ouputTextArea.appendText("[DIGEST] " + footprint.toString() + "\r\n\r\n");
			} catch (NoSuchAlgorithmException e) {
				final Writer writer = new StringWriter();
				e.printStackTrace(new PrintWriter(writer));

				this.ouputTextArea.appendText(writer.toString());
			}
		} else {
			final String formattedTypedCommand = typedCommand + additionnalCharacter;

			this.commandInputTextField.clear();
			this.ouputTextArea.appendText("[CLIENT] " + formattedTypedCommand);
			notifyCommandResult(this.pop3Client.sendCommand(formattedTypedCommand));

			if (typedCommand.equals("QUIT")) {
				this.isConnected.set(false);
			}
		}
	}

	@Override
	public void notifyCommandResult(String commandResult) {
		this.ouputTextArea.appendText("[SERVER] " + commandResult + "\n");
	}
}