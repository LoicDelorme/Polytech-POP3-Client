package fr.polytech.pop3.client.ui;

/**
 * This interface represents a POP 3 command observable.
 *
 * @author DELORME Loïc
 * @since 1.0.0
 */
public interface Pop3CommandObservable {

	/**
	 * Notify a command result.
	 * 
	 * @param commandResult
	 *            The command result.
	 */
	public void notifyCommandResult(String commandResult);
}