package net.skds.lib2.io.codec.array;

import net.skds.lib2.io.codec.AbstractCodec;
import net.skds.lib2.io.codec.CodecRegistry;
import net.skds.lib2.io.codec.UniversalReader;
import net.skds.lib2.io.codec.UniversalWriter;
import net.skds.lib2.io.exception.ParseException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Base64;

public class ByteArrayB64Codec extends AbstractCodec<byte[]> {

	public ByteArrayB64Codec(Type type, CodecRegistry registry) {
		super(type, registry);
	}

	@Override
	public byte[] read(UniversalReader reader) throws IOException {
		switch (reader.nextEntryType()) {
			case null -> {
				reader.skipNull();
				return null;
			}
			case BEGIN_LIST -> {
				return reader.readByteArray();
			}
			case STRING -> {
				return Base64.getDecoder().decode(reader.readString());
			}
			default -> throw new ParseException("Unexpected token " + reader.nextEntryType());
		}
	}

	@Override
	public void write(byte[] value, UniversalWriter writer) throws IOException {
		if (value == null) {
			writer.writeNull();
			return;
		}
		writer.writeString(Base64.getEncoder().encodeToString(value));
	}

	@Override
	public String valueAsKeyString(byte[] val) {
		if (val == null) return "null";

		return Base64.getEncoder().encodeToString(val);
	}

	@Override
	public byte[] stringKeyToValue(String key) throws IOException {
		if (key.equals("null")) return null;
		return Base64.getDecoder().decode(key);
	}
}
