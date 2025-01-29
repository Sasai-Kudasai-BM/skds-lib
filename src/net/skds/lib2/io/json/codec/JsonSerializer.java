package net.skds.lib2.io.json.codec;

import net.skds.lib2.io.chars.StringCharOutput;
import net.skds.lib2.io.codec.Serializer;
import net.skds.lib2.io.json.JsonWriter;

import java.io.IOException;

public interface JsonSerializer<T> extends Serializer<T, JsonWriter>, JsonRegistryGetter {

	default String valueAsKeyString(T val) {
		return String.valueOf(val);
	}

	default String toJson(T value) {
		try {
			StringCharOutput co = new StringCharOutput();
			JsonWriter writer = getRegistry().createWriter(co);
			write(value, writer);
			return co.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
