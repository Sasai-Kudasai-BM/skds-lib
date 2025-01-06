package net.skds.lib2.io.json.codec;

import net.skds.lib2.io.CharInput;
import net.skds.lib2.io.Deserializer;
import net.skds.lib2.io.StringCharInput;
import net.skds.lib2.io.json.JsonReader;
import net.skds.lib2.io.json.elements.JsonElement;

import java.io.IOException;

public interface JsonDeserializer<T> extends Deserializer<T, JsonReader>, JsonRegistryGetter {
	
	default T parse(CharInput charInput) {
		try {
			return read(getRegistry().createReader(charInput));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	default T parse(String json) {
		try {
			return read(getRegistry().createReader(new StringCharInput(json)));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	default T parse(JsonElement json) {
		try {
			return read(getRegistry().createReader(new StringCharInput(json.toString()))); // TODO
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
