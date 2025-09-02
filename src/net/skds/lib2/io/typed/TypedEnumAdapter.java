package net.skds.lib2.io.codec.typed;

import net.skds.lib2.io.codec.*;
import net.skds.lib2.io.sosison.SosisonEntryType;

import java.io.IOException;
import java.lang.reflect.Type;

public class TypedEnumAdapter<CT, E extends Enum<E> & ConfigEnumType<CT>> extends AbstractCodec<CT> {

	private final Class<E> typeClass;

	@SuppressWarnings("unchecked")
	public <VE extends Enum<VE> & ConfigEnumType<? extends CT>> TypedEnumAdapter(Type type, Class<VE> typeClass, CodecRegistry registry) {
		super(type, registry);
		this.typeClass = (Class<E>) typeClass;
	}

	@Override
	@SuppressWarnings("unchecked")
	public final void write(CT value, UniversalWriter writer) throws IOException {
		if (value == null) {
			writer.writeNull();
			return;
		}
		if (!(value instanceof TypedConfig tc)) {
			throw new UnsupportedOperationException("Value \"" + value + "\" is not a TypedConfig");
		}
		if (value instanceof PreSerializeCall jps) {
			jps.preSerialize();
		}
		writer.beginObject();
		E type = (E) tc.getConfigType();
		writer.writeName(type.keyName());
		UniversalSerializer<CT> serializer = this.registry.getSerializer(type.getTypeClass());
		serializer.write(value, writer);
		writer.endObject();
	}

	@Override
	public final CT read(UniversalReader reader) throws IOException {
		if (reader.nextEntryType() == SosisonEntryType.NULL) {
			reader.skipNull();
			return null;
		}
		reader.beginObject();
		String typeName = reader.readName();
		E type = Enum.valueOf(typeClass, typeName);
		UniversalDeserializer<CT> deserializer = this.registry.getDeserializer(type.getTypeClass());
		CT value = deserializer.read(reader);
		reader.endObject();
		if (value instanceof PostDeserializeCall jpi) {
			jpi.postDeserialized();
		}
		return value;
	}

}
