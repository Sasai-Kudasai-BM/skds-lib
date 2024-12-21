package net.skds.lib2.io.json.codec;

import net.skds.lib2.io.CharInput;
import net.skds.lib2.io.Codec;
import net.skds.lib2.io.StringCharInput;
import net.skds.lib2.io.json.JsonReader;
import net.skds.lib2.io.json.JsonReaderImpl;
import net.skds.lib2.io.json.JsonWriter;

import java.io.IOException;

public interface JsonCodec<T> extends Codec<T, JsonWriter, JsonReader> {

	//default String toJson(T value) {
	//
	//}

	default T parse(CharInput charInput) {
		try {
			return deserialize(new JsonReaderImpl(charInput));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	default T parse(String json) {
		try {
			return deserialize(new JsonReaderImpl(new StringCharInput(json)));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
