package jgodara.repme.common.exceptions;

public class PageNotFoundException extends CriticalException {

	private static final long serialVersionUID = 1L;

	/**
	 * Construct a new exception with the specified detail message and cause.
	 * 
	 * @param message
	 *            the detail message
	 * @param cause
	 *            the cause
	 */
	public PageNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Construct a new exception with the specified detail message.
	 * 
	 * @param message
	 *            the detail message.
	 */
	public PageNotFoundException(String message) {
		super(message);
	}

	/**
	 * Construct a new exception with the specified cause and a detail message
	 * of (cause==null ? null : cause.toString())
	 * 
	 * @param cause
	 */
	public PageNotFoundException(Throwable cause) {
		super(cause);
	}

}
