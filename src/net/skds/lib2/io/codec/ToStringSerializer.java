package net.skds.lib2.io.codec;

import java.io.IOException;
import java.lang.reflect.Type;

public class ToStringSerializer implements UniversalSerializer<Object> {
	private CodecRegistry registry;

	public ToStringSerializer(Type type, CodecRegistry registry) {
		this.registry = registry;
	}

	@Override
	public void write(Object value, UniversalWriter writer) throws IOException {
		if (value == null) {
			writer.writeNull();
		} else {
			writer.writeString(valueAsKeyString(value));
		}
	}

	@Override
	public CodecRegistry getRegistry() {
		return this.registry;
	}
}
