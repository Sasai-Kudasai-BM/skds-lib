package net.skds.lib2.io.json.exception;

import java.lang.reflect.Type;

public class UnsupportedJsonDeserializationException extends UnsupportedOperationException {
	
	public UnsupportedJsonDeserializationException(Type codecType) {
		super("Deserialization is not supported for \"" + codecType + "\"");
	}
}
