package net.skds.lib2.io.json.codec;

import net.skds.lib2.io.Serializer;
import net.skds.lib2.io.StringCharOutput;
import net.skds.lib2.io.json.JsonWriter;

import java.io.IOException;

public interface JsonSerializer<T> extends Serializer<T, JsonWriter>, JsonRegistryGetter {
	
	default String valueAsKeyString(T val) {
		return String.valueOf(val);
	}

	default String toJson(T value) {
		try {
			StringCharOutput co = new StringCharOutput();
			write(value, getRegistry().createWriter(co));
			return co.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
