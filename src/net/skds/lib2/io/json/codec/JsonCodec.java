package net.skds.lib2.io.json.codec;

import net.skds.lib2.io.CharInput;
import net.skds.lib2.io.Codec;
import net.skds.lib2.io.StringCharInput;
import net.skds.lib2.io.StringCharOutput;
import net.skds.lib2.io.json.FormattedJsonWriterImpl;
import net.skds.lib2.io.json.JsonReader;
import net.skds.lib2.io.json.JsonReaderImpl;
import net.skds.lib2.io.json.JsonWriter;

import java.io.IOException;

public abstract class JsonCodec<T> implements Codec<T, JsonWriter, JsonReader> {

	protected final JsonCodecRegistry registry;

	public JsonCodec(JsonCodecRegistry registry) {
		this.registry = registry;
	}

	public T parse(CharInput charInput) {
		try {
			return read(new JsonReaderImpl(charInput));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public T parse(String json) {
		try {
			return read(new JsonReaderImpl(new StringCharInput(json))); // TODO
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String toJson(T value) {
		try {
			StringCharOutput co = new StringCharOutput();
			write(value, new FormattedJsonWriterImpl(co, "\t")); // TODO
			return co.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
