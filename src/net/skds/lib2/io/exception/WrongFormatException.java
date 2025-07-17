package net.skds.lib2.io.exception;

import java.io.IOException;

public class WrongFormatException extends IOException {
	public WrongFormatException() {
	}

	public WrongFormatException(String message) {
		super(message);
	}

	public WrongFormatException(String message, Throwable cause) {
		super(message, cause);
	}

	public WrongFormatException(Throwable cause) {
		super(cause);
	}
}
