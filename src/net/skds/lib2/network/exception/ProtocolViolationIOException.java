package net.skds.lib2.network.exception;

import java.io.IOException;

public class ProtocolViolationIOException extends IOException {

	public ProtocolViolationIOException() {
	}

	public ProtocolViolationIOException(String message) {
		super(message);
	}

	public ProtocolViolationIOException(String message, Throwable cause) {
		super(message, cause);
	}

	public ProtocolViolationIOException(Throwable cause) {
		super(cause);
	}
}
