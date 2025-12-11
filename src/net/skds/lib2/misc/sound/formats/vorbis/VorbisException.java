package net.skds.lib2.misc.sound.formats.vorbis;

import java.io.IOException;

public class VorbisException extends IOException {

	public VorbisException() {
	}

	public VorbisException(String message) {
		super(message);
	}

	public VorbisException(String message, Throwable cause) {
		super(message, cause);
	}

	public VorbisException(Throwable cause) {
		super(cause);
	}
}
