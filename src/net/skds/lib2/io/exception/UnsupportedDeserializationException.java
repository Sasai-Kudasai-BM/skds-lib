package net.skds.lib2.io.exception;

import java.lang.reflect.Type;

public class UnsupportedDeserializationException extends UnsupportedOperationException {

	public UnsupportedDeserializationException(Type codecType) {
		super("Deserialization is not supported for \"" + codecType + "\"");
	}
}
