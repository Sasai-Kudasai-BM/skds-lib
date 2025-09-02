package net.skds.tests.json;

import net.skds.lib2.io.codec.AbstractCodec;
import net.skds.lib2.io.codec.CodecRegistry;
import net.skds.lib2.io.codec.UniversalReader;
import net.skds.lib2.io.codec.UniversalWriter;
import net.skds.lib2.io.codec.annotation.DefaultCodec;
import net.skds.lib2.io.sosison.SosisonEntryType;
import net.skds.tests.json.JsonTest.JsonTestRegistry;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonTestCollections {
	public static void test(JsonTestRegistry registry) {
		List<String> list = new ArrayList<>();
		list.add("null");
		list.add("1");

		System.out.println(registry.toJson(list));
		System.out.println(registry.toJson(new Obj2BoolMapHolder1()));
		System.out.println(registry.toJson(new HashMap<String, Boolean>()));

		System.out.println(registry.toJson(new Obj2BoolMapHolder2()));
		System.out.println(registry.toJson(new Obj2BoolMap<String>()));

		DefaultCodecExtendsCollection defCollectionCodec = new DefaultCodecExtendsCollection();
		defCollectionCodec.add("a");
		defCollectionCodec.add("b");
		defCollectionCodec.add("c");
		String defCollectionCodecJson = registry.toJson(defCollectionCodec);
		System.out.println(defCollectionCodecJson);
		registry.parseJson(defCollectionCodecJson, DefaultCodecExtendsCollection.class);
	}

	private static class Obj2BoolMapHolder1 {
		private Map<String, Boolean> map1 = new HashMap<>();

		public Obj2BoolMapHolder1() {
			map1.put("a", true);
		}
	}

	private static class Obj2BoolMapHolder2 {
		private Obj2BoolMap<String> map2 = new Obj2BoolMap<>();

		public Obj2BoolMapHolder2() {
			map2.put("b", false);
		}
	}

	private static class Obj2BoolMap<T> extends HashMap<T, Boolean> {

	}

	@DefaultCodec(DefaultCodecExtendsCollectionCodec.class)
	private static class DefaultCodecExtendsCollection extends ArrayList<String> {
		private int version = 10;
	}

	private static final class DefaultCodecExtendsCollectionCodec extends AbstractCodec<DefaultCodecExtendsCollection> {

		public DefaultCodecExtendsCollectionCodec(Type type, CodecRegistry registry) {
			super(type, registry);
			System.out.println("create " + type);
		}

		@Override
		public DefaultCodecExtendsCollection read(UniversalReader reader) throws IOException {
			System.out.println("read");
			DefaultCodecExtendsCollection value = new DefaultCodecExtendsCollection();
			reader.beginObject();
			while (reader.nextEntryType() != SosisonEntryType.END_OBJECT) {
				String name = reader.readName();
				if (name.equals("version")) {
					value.version = reader.readInt();
				}
				if (name.equals("values")) {
					reader.beginList();
					while (reader.nextEntryType() != SosisonEntryType.END_LIST) {
						value.add(reader.readString());
					}
					reader.endList();
				}
			}
			reader.endObject();
			return value;
		}

		@Override
		public void write(DefaultCodecExtendsCollection value, UniversalWriter writer) throws IOException {
			System.out.println("write");
			writer.beginObject();

			writer.writeLong("version", value.version);

			writer.beginList("values");
			for (String string : value) {
				writer.writeString(string);
			}
			writer.endList();

			writer.endObject();
		}
	}

}
