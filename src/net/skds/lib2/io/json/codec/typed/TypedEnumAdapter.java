package net.skds.lib2.io.json.codec.typed;

import java.io.IOException;
import java.lang.reflect.Type;

import net.skds.lib2.io.json.JsonEntryType;
import net.skds.lib2.io.json.JsonPostDeserializeCall;
import net.skds.lib2.io.json.JsonPreSerializeCall;
import net.skds.lib2.io.json.JsonReader;
import net.skds.lib2.io.json.JsonWriter;
import net.skds.lib2.io.json.codec.AbstractJsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;
import net.skds.lib2.io.json.codec.JsonDeserializer;
import net.skds.lib2.io.json.codec.JsonSerializer;

public class TypedEnumAdapter<CT, E extends Enum<E> & ConfigEnumType<CT>> extends AbstractJsonCodec<CT> {

	private Class<E> typeClass;
	
	public TypedEnumAdapter(Type type, Class<E> typeClass, JsonCodecRegistry registry) {
		super(type, registry);
		this.typeClass = typeClass;
	}

	@Override
	@SuppressWarnings("unchecked")
	public final void write(CT value, JsonWriter writer) throws IOException {
		if (value == null) {
			writer.writeNull();
			return;
		}
		if (!(value instanceof TypedConfig tc)) {
			throw new UnsupportedOperationException("Value \"" + value + "\" is not a TypedConfig");
		}
		if (value instanceof JsonPreSerializeCall jps) {
			jps.preSerializeJson();
		}
		writer.beginObject();
		E type = (E) tc.getConfigType();
		writer.writeName(type.keyName());
		JsonSerializer<CT> serializer = this.registry.getSerializer(type.getTypeClass());
		serializer.write(value, writer);
		writer.endObject();
	}

	@Override
	public final CT read(JsonReader reader) throws IOException {
		if (reader.nextEntryType() == JsonEntryType.NULL) {
			reader.skipNull();
			return null;
		}
		reader.beginObject();
		String typeName = reader.readName();
		E type = Enum.valueOf(typeClass, typeName);
		JsonDeserializer<CT> deserializer = this.registry.getDeserializer(type.getTypeClass());
		CT value = deserializer.read(reader);
		reader.endObject();
		if (value instanceof JsonPostDeserializeCall jpi) {
			jpi.postDeserializedJson();
		}
		return value;
	}
	
}
