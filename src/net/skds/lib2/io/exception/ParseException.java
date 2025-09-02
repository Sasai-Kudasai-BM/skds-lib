package net.skds.lib2.io.exception;

import java.io.IOException;

public class ParseException extends IOException {

	public ParseException(String message) {
		super(message);
	}

	public ParseException(Throwable cause) {
		super(cause);
	}

	public ParseException() {
	}

	public ParseException(String message, Throwable cause) {
		super(message, cause);
	}
}
