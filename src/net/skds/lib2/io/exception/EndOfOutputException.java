package net.skds.lib2.io.exception;

import java.io.IOException;

public class EndOfOutputException extends IOException {

	public EndOfOutputException(String message) {
		super(message);
	}
}
