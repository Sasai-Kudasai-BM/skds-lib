package net.skds.lib2.network.exception;

public class WrongSideException extends IllegalStateException {
	public WrongSideException() {
	}

	public WrongSideException(String s) {
		super(s);
	}

	public WrongSideException(String message, Throwable cause) {
		super(message, cause);
	}

	public WrongSideException(Throwable cause) {
		super(cause);
	}
}
