package fr.polytech.pop3.client.ui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import fr.polytech.pop3.client.Pop3Client;
import fr.polytech.pop3.client.Pop3CommandObservable;
import fr.polytech.pop3.client.Pop3CommandObserver;
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
	 * The POP 3 command observer.
	 */
	private Pop3CommandObserver pop3CommandObserver;

	/**
	 * The is connected flag.
	 */
	private BooleanProperty isConnected;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.isConnected = new SimpleBooleanProperty(false);

		this.hostnameInputTextField.disableProperty().bind(this.isConnected);
		this.portInputTextField.disableProperty().bind(this.isConnected);

		this.connectButton.disableProperty().bind(this.hostnameInputTextField.textProperty().isEmpty().and(this.portInputTextField.textProperty().isEmpty()).or(this.isConnected));
		this.isConnected.addListener((object, oldValue, newValue) -> {
			this.connectedCircle.setFill(newValue ? Color.LIMEGREEN : Color.RED);
		});

		this.commandInputTextField.disableProperty().bind(this.isConnected.not());
		this.sendCommandButton.disableProperty().bind(this.isConnected.not().and(this.commandInputTextField.textProperty().isEmpty()));

		this.connectButton.setOnAction(event -> onConnectAction());
		this.commandInputTextField.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				onCommandAction();
			}
		});
		this.sendCommandButton.setOnAction(event -> onCommandAction());
	}

	/**
	 * The on connect action.
	 */
	private void onConnectAction() {
		try {
			this.pop3CommandObserver = new Pop3Client(this.hostnameInputTextField.getText().trim(), Integer.parseInt(this.portInputTextField.getText().trim()), this);
		} catch (IOException e) {
			this.ouputTextArea.appendText(e.getMessage());
		}
	}

	/**
	 * The on command action.
	 */
	private void onCommandAction() {
		this.pop3CommandObserver.notifyCommand(this.commandInputTextField.getText());
	}

	@Override
	public void notifyCommandResult(String commandResult) {
		this.ouputTextArea.appendText(commandResult);
	}
}