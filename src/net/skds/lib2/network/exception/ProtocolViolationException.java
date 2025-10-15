package net.skds.lib2.network.exception;

public class ProtocolViolationException extends RuntimeException {

	public ProtocolViolationException() {
	}

	public ProtocolViolationException(String message) {
		super(message);
	}

	public ProtocolViolationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ProtocolViolationException(Throwable cause) {
		super(cause);
	}
}
