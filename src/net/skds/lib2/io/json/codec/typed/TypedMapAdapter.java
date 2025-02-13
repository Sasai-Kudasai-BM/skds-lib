package net.skds.lib2.io.json.codec.typed;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

import net.skds.lib2.io.json.JsonEntryType;
import net.skds.lib2.io.json.JsonPostDeserializeCall;
import net.skds.lib2.io.json.JsonPreSerializeCall;
import net.skds.lib2.io.json.JsonReader;
import net.skds.lib2.io.json.JsonWriter;
import net.skds.lib2.io.json.codec.AbstractJsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;
import net.skds.lib2.io.json.codec.JsonDeserializer;
import net.skds.lib2.io.json.codec.JsonSerializer;

public class TypedMapAdapter<CT> extends AbstractJsonCodec<CT> {

	private Map<String, ? extends ConfigType<?>> typeMap;

	public TypedMapAdapter(Type type, Map<String, ? extends ConfigType<?>> typeMap, JsonCodecRegistry registry) {
		super(type, registry);
		this.typeMap = typeMap;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void write(CT value, JsonWriter writer) throws IOException {
		//System.out.println(((FormattedJsonWriterImpl)writer).getOutput());
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
		ConfigType<CT> type = (ConfigType<CT>) tc.getConfigType();
		writer.writeName(type.keyName());
		JsonSerializer<CT> serializer = this.registry.getSerializer(type.getTypeClass());
		serializer.write((CT) value, writer);
		writer.endObject();
	}

	@Override
	@SuppressWarnings("unchecked")
	public CT read(JsonReader reader) throws IOException {
		if (reader.nextEntryType() == JsonEntryType.NULL) {
			reader.skipNull();
			return null;
		}
		reader.beginObject();
		String typeName = reader.readName();
		ConfigType<CT> type = (ConfigType<CT>) typeMap.get(typeName);
		if (type == null) {
			reader.skipValue();
			reader.endObject();
		} else {
			JsonDeserializer<CT> deserializer = this.registry.getDeserializer(type.getTypeClass());
			CT value = deserializer.read(reader);
			reader.endObject();
			if (value instanceof JsonPostDeserializeCall jpi) {
				jpi.postDeserializedJson();
			}
			return value;
		}
		return null;
	}
}

