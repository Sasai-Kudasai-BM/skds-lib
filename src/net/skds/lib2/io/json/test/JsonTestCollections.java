package net.skds.lib2.io.json.test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.skds.lib2.io.json.JsonEntryType;
import net.skds.lib2.io.json.JsonReader;
import net.skds.lib2.io.json.JsonUtils;
import net.skds.lib2.io.json.JsonWriter;
import net.skds.lib2.io.json.annotation.DefaultJsonCodec;
import net.skds.lib2.io.json.codec.AbstractJsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;

public class JsonTestCollections {
	public static void test(JsonCodecRegistry registry) {
		List<String> list = new ArrayList<>();
		list.add("null");
		list.add("1");

		System.out.println(JsonUtils.toJson(list));
		System.out.println(JsonUtils.toJson(new Obj2BoolMapHolder1()));
		System.out.println(JsonUtils.toJson(new HashMap<String, Boolean>()));

		System.out.println(JsonUtils.toJson(new Obj2BoolMapHolder2()));
		System.out.println(JsonUtils.toJson(new Obj2BoolMap<String>()));

		DefaultCodecExtendsCollection defCollectionCodec = new DefaultCodecExtendsCollection();
		defCollectionCodec.add("a");
		defCollectionCodec.add("b");
		defCollectionCodec.add("c");
		String defCollectionCodecJson = JsonUtils.toJson(defCollectionCodec);
		System.out.println(defCollectionCodecJson);
		JsonUtils.parseJson(defCollectionCodecJson, DefaultCodecExtendsCollection.class);
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

	@DefaultJsonCodec(DefaultCodecExtendsCollectionCodec.class)
	private static class DefaultCodecExtendsCollection extends ArrayList<String> {
		private int version = 10;
	}

	private static final class DefaultCodecExtendsCollectionCodec extends AbstractJsonCodec<DefaultCodecExtendsCollection> {

		public DefaultCodecExtendsCollectionCodec(Type type, JsonCodecRegistry registry) {
			super(type, registry);
			System.out.println("create " + type);
		}

		@Override
		public DefaultCodecExtendsCollection read(JsonReader reader) throws IOException {
			System.out.println("read");
			DefaultCodecExtendsCollection value = new DefaultCodecExtendsCollection();
			reader.beginObject();
			while (reader.nextEntryType() != JsonEntryType.END_OBJECT) {
				String name = reader.readName();
				if (name.equals("version")) {
					value.version = reader.readInt();
				}
				if (name.equals("values")) {
					reader.beginArray();
					while (reader.nextEntryType() != JsonEntryType.END_ARRAY) {
						value.add(reader.readString());
					}
					reader.endArray();
				}
			}
			reader.endObject();
			return value;
		}

		@Override
		public void write(DefaultCodecExtendsCollection value, JsonWriter writer) throws IOException {
			System.out.println("write");
			writer.beginObject();

			writer.writeInt("version", value.version);

			writer.beginArray("values");
			for (String string : value) {
				writer.writeString(string);
			}
			writer.endArray();

			writer.endObject();
		}
	}

}
