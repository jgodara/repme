package jgodara.repme.common.exceptions;

public class CriticalException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Construct a new exception with the specified detail message.
	 * 
	 * @param message
	 *            the detail message.
	 */
	public CriticalException(String message) {
		super(message);
	}

	/**
	 * Construct a new exception with the specified cause and a detail message of
	 * (cause==null ? null : cause.toString())
	 * 
	 * @param cause
	 */
	public CriticalException(Throwable cause) {
		super(cause);
	}

	/**
	 * Construct a new exception with the specified detail message and cause.
	 * 
	 * @param message
	 *            the detail message
	 * @param cause
	 *            the cause
	 */
	public CriticalException(String message, Throwable cause) {
		super(message, cause);
	}
}