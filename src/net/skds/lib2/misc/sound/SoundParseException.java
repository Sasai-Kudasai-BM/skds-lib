package net.skds.lib2.misc.sound;

import java.io.IOException;

public class SoundParseException extends IOException {

	public SoundParseException() {
		super();
	}

	public SoundParseException(String message) {
		super(message);
	}

	public SoundParseException(String message, Throwable cause) {
		super(message, cause);
	}

	public SoundParseException(Throwable cause) {
		super(cause);
	}
}
