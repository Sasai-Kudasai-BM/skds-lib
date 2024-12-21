package net.skds.lib2.io;

import java.io.IOException;

public class EndOfInputException extends IOException {

	public EndOfInputException(String message) {
		super(message);
	}
}
