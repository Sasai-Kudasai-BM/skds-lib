package net.skds.lib2.io.codec.typed;

import net.skds.lib2.io.codec.*;
import net.skds.lib2.io.exception.ParseException;
import net.skds.lib2.io.sosison.SosisonEntryType;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

public class TypedMapAdapter<CT> extends AbstractCodec<CT> {

	private final Map<String, ? extends ConfigType<?>> typeMap;

	public TypedMapAdapter(Type type, Map<String, ? extends ConfigType<?>> typeMap, CodecRegistry registry) {
		super(type, registry);
		this.typeMap = typeMap;
		for (ConfigType<?> e : this.typeMap.values()) {
			if (!TypedConfig.class.isAssignableFrom(e.getTypeClass())) {
				throw new IllegalStateException(e.getTypeClass() + " is not implementing " + TypedConfig.class);
			}
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void write(CT value, UniversalWriter writer) throws IOException {
		//System.out.println(((FormattedJsonWriterImpl)writer).getOutput());
		if (value == null) {
			writer.writeNull();
			return;
		}
		if (!(value instanceof TypedConfig<?> tc)) {
			throw new UnsupportedOperationException("Value \"" + value + "\" is not a TypedConfig");
		}
		if (value instanceof PreSerializeCall jps) {
			jps.preSerialize();
		}
		writer.beginObject();
		ConfigType<CT> type = (ConfigType<CT>) tc.getConfigType();
		writer.writeName(type.keyName());
		UniversalSerializer<CT> serializer = this.registry.getSerializer(type.getTypeClass());
		if (serializer == this) {
			throw new ParseException("recursive call serializer for " + type.getTypeClass());
		}
		serializer.write((CT) value, writer);
		writer.endObject();
	}

	@Override
	@SuppressWarnings("unchecked")
	public CT read(UniversalReader reader) throws IOException {
		if (reader.nextEntryType() == SosisonEntryType.NULL) {
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
			UniversalDeserializer<CT> deserializer = this.registry.getDeserializer(type.getTypeClass());
			if (deserializer == this) {
				throw new ParseException("recursive call deserializer for " + type.getTypeClass());
			}
			CT value = deserializer.read(reader);
			((TypedConfig<ConfigType<CT>>) value).setConfigType(type);
			reader.endObject();
			if (value instanceof PostDeserializeCall jpi) {
				jpi.postDeserialized();
			}
			return value;
		}
		return null;
	}
}

