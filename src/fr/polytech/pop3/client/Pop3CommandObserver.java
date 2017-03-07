package fr.polytech.pop3.client;

/**
 * This interface represents a POP 3 command observer.
 *
 * @author DELORME Loïc
 * @since 1.0.0
 */
public interface Pop3CommandObserver {

	/**
	 * Notify a command has been typed.
	 * 
	 * @param command
	 *            The typed command.
	 */
	public void notifyCommand(String command);
}