package net.skds.lib2.io.json.exception;

import java.lang.reflect.Type;

public class UnsupportedJsonSerializationException extends UnsupportedOperationException {
	
	public UnsupportedJsonSerializationException(Type codecType) {
		super("Serialization is not supported for \"" + codecType + "\"");
	}
}
