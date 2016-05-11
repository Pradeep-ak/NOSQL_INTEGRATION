package com.self;

/**
 * The Class NoSQLException.
 */
public class NoSQLException extends Exception {

	/**
	 * Instantiates a new no sql exception.
	 *
	 * @param pMessage the message
	 * @param pCause the cause
	 * @param pEnableSuppression the enable suppression
	 * @param pWritableStackTrace the writable stack trace
	 */
	public NoSQLException(String pMessage, Throwable pCause, boolean pEnableSuppression, boolean pWritableStackTrace) {
		super(pMessage, pCause, pEnableSuppression, pWritableStackTrace);
	}

	/**
	 * Instantiates a new no sql exception.
	 *
	 * @param pMessage the message
	 * @param pException the exception
	 */
	public NoSQLException(String pMessage, Exception pException) {
		super(pMessage, pException);
	}

	/**
	 * Instantiates a new no sql exception.
	 *
	 * @param pMessage the message
	 */
	public NoSQLException(String pMessage) {
		super(pMessage);
	}

	/**
	 * Instantiates a new no sql exception.
	 *
	 * @param pException the exception
	 */
	public NoSQLException(Exception pException) {
		super(pException);
	}
}
