package net.skds.lib2.io.json.codec;

import java.io.IOException;
import java.lang.reflect.Type;

import lombok.Getter;
import net.skds.lib2.io.json.JsonWriter;

public class JsonToStringSerialiser implements JsonSerializer<Object> {

	@Getter(onMethod_ = @Override)
	private JsonCodecRegistry registry;
	
	public JsonToStringSerialiser(Type type, JsonCodecRegistry registry) {
		this.registry = registry;
	}

	@Override
	public void write(Object value, JsonWriter writer) throws IOException {
		if (value == null) {
			writer.writeNull();
		} else {
			writer.writeString(valueAsKeyString(value));
		}
	}
	
}
