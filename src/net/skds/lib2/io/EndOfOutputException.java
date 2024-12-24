package net.skds.lib2.io;

import java.io.IOException;

public class EndOfOutputException extends IOException {

	public EndOfOutputException(String message) {
		super(message);
	}
}
