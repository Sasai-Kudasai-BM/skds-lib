package net.skds.lib2.io.json.exception;

import java.io.IOException;

public class JsonReadException extends IOException {

	public JsonReadException(String message) {
		super(message);
	}

	public JsonReadException(Throwable cause) {
		super(cause);
	}
}
