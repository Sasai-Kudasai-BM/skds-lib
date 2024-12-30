package net.skds.lib2.io.json.codec;

import net.skds.lib2.io.CharInput;
import net.skds.lib2.io.Codec;
import net.skds.lib2.io.StringCharInput;
import net.skds.lib2.io.StringCharOutput;
import net.skds.lib2.io.json.JsonReader;
import net.skds.lib2.io.json.JsonWriter;

import java.io.IOException;

public abstract class JsonCodec<T> implements Codec<T, JsonWriter, JsonReader> {

	protected final JsonCodecRegistry registry;

	public JsonCodec(JsonCodecRegistry registry) {
		this.registry = registry;
	}

	public String valueAsKeyString(T val) {
		return String.valueOf(val);
	}

	public T parse(CharInput charInput) {
		try {
			return read(registry.createReader(charInput));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public T parse(String json) {
		try {
			return read(registry.createReader(new StringCharInput(json)));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String toJson(T value) {
		try {
			StringCharOutput co = new StringCharOutput();
			write(value, registry.createWriter(co));
			return co.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
