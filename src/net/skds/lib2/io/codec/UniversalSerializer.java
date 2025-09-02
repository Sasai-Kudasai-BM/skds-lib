package net.skds.lib2.io.codec;

import net.skds.lib2.io.ByteArrayExtendedDataOutput;
import net.skds.lib2.io.chars.StringCharOutput;

import java.io.IOException;

public interface UniversalSerializer<T> extends Serializer<T, UniversalWriter>, CodecRegistryGetter {

	default String valueAsKeyString(T val) {
		return String.valueOf(val);
	}

	default String toJson(T value) {
		try {
			StringCharOutput co = new StringCharOutput();
			UniversalWriter writer = getRegistry().createJsonWriter(co);
			write(value, writer);
			return co.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
