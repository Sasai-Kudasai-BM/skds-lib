package net.skds.lib2.io.exception;

import java.lang.reflect.Type;

public class UnsupportedJsonException extends UnsupportedOperationException {

	public UnsupportedJsonException(Type codecType) {
		super("Serialization is not supported for \"" + codecType + "\"");
	}
}
