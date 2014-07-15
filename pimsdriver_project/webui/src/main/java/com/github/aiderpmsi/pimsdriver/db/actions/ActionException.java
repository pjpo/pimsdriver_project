package com.github.aiderpmsi.pimsdriver.db.actions;

public class ActionException extends Exception {

	private static final long serialVersionUID = -2801140721920798105L;

	public ActionException() {
	}

	public ActionException(String message) {
		super(message);
	}

	public ActionException(Throwable cause) {
		super(cause);
	}

	public ActionException(String message, Throwable cause) {
		super(message, cause);
	}

	public ActionException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
