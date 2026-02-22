package net.skds.lib2.io.codec.typed;

import net.skds.lib2.io.codec.*;
import net.skds.lib2.io.exception.ParseException;
import net.skds.lib2.io.sosison.SosisonEntryType;

import java.io.IOException;
import java.lang.reflect.Type;

public class TypedEnumAdapter<CT, E extends Enum<E> & ConfigEnumType<CT>> extends AbstractCodec<CT> {

	private final Class<E> typeClass;

	@SuppressWarnings("unchecked")
	public <VE extends Enum<VE> & ConfigEnumType<? extends CT>> TypedEnumAdapter(Type type, Class<VE> typeClass, CodecRegistry registry) {
		super(type, registry);
		this.typeClass = (Class<E>) typeClass;
		for (E e : this.typeClass.getEnumConstants()) {
			if (!TypedConfig.class.isAssignableFrom(e.getTypeClass())) {
				throw new IllegalStateException(e.getTypeClass() + " is not implementing " + TypedConfig.class);
			}
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public final void write(CT value, UniversalWriter writer) throws IOException {
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
		E type = (E) tc.getConfigType();
		writer.writeName(type.keyName());
		UniversalSerializer<CT> serializer = this.registry.getSerializer(type.getTypeClass());
		if (serializer == this) {
			throw new ParseException("recursive call serializer for " + type.getTypeClass());
		}
		serializer.write(value, writer);
		writer.endObject();
	}

	@SuppressWarnings("unchecked")
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
		if (deserializer == this) {
			throw new ParseException("recursive call deserializer for " + type.getTypeClass());
		}
		CT value = deserializer.read(reader);
		((TypedConfig<E>) value).setConfigType(type);
		reader.endObject();
		if (value instanceof PostDeserializeCall jpi) {
			jpi.postDeserialized();
		}
		return value;
	}

}
