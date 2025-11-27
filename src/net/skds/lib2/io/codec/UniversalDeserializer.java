package net.skds.lib2.io.codec;

import net.skds.lib2.io.chars.CharInput;
import net.skds.lib2.io.chars.StringCharInput;
import net.skds.lib2.io.json.JsonReaderImpl;
import net.skds.lib2.io.json.elements.JsonElement;

import java.io.IOException;

public interface UniversalDeserializer<T> extends Deserializer<T, UniversalReader>, CodecRegistryGetter {

	default T stringKeyToValue(String key) throws IOException {
		return read(new JsonReaderImpl(new StringCharInput(key), this.getRegistry()));
	}

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
			return read(getRegistry().createReader(json));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
