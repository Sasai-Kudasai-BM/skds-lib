package net.skds.tests.json;

import lombok.CustomLog;
import net.skds.lib2.io.codec.*;
import net.skds.lib2.io.codec.CodecFactory.MapJsonFactory;
import net.skds.lib2.io.codec.typed.ConfigEnumType;
import net.skds.lib2.io.codec.typed.ConfigType;
import net.skds.lib2.io.codec.typed.TypedEnumAdapter;
import net.skds.lib2.io.codec.typed.TypedMapAdapter;
import net.skds.lib2.io.json.JsonCodecOptions;
import net.skds.lib2.utils.AnsiEscape;
import net.skds.lib2.utils.logger.SKDSLogger;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@CustomLog
public class JsonTest {

	public static Map<String, JsonTestRun> createRuns() {
		List<JsonTestRun> runs = new ArrayList<>();
		//runs.add(new JsonTestRun("classHack", JsonTestClassHack::test));

		runs.add(new JsonTestRun("amogus", JsonTestAmogus::test));
		runs.add(new JsonTestRun("record", JsonTestRecord::test));
		runs.add(new JsonTestRun("yup", JsonTestYup::test));
		runs.add(new JsonTestRun("dg", JsonTestDg::test));
		runs.add(new JsonTestRun("collections", JsonTestCollections::test));
		runs.add(new JsonTestRun("simple", JsonTestSimple::test));
		runs.add(new JsonTestRun("vec3", JsonTestVec3::test));
		runs.add(new JsonTestRun("shape", JsonTestShapes::test));
		runs.add(new JsonTestRun("replaceDefault", JsonTestReplaceDefault::test));
		runs.add(new JsonTestRun("nest", JsonTestNest::test));
		runs.add(new JsonTestRun("escape", JsonTestEscape::test));
		runs.add(new JsonTestRun("emptyTyped", JsonTestEmptyTyped::test));
		runs.add(new JsonTestRun("null", JsonTestNull::test));
		runs.add(new JsonTestRun("mapKeyValue", JsonTestMap::test));

		Map<String, JsonTestRun> map = new LinkedHashMap<>();

		for (JsonTestRun jsonTestRun : runs) {
			map.put(jsonTestRun.key, jsonTestRun);
		}

		return map;
	}

	public record JsonTestRun(String key, Consumer<JsonTestRegistry> function) {

		private static final String SPLIT;

		static {
			String split = AnsiEscape.MAGENTA.sequence + "#";
			split = split + AnsiEscape.GREEN.sequence + "=".repeat(40) + split;
			SPLIT = split;
		}

		public void run(JsonTestRegistry registry) {
			log.info(this.key);
			this.function.accept(registry);
			log.info(SPLIT);
		}
	}

	public static class JsonTestRegistry extends CodecRegistry {

		private static final UniversalCodecOptions OPTIONS;

		static {
			UniversalCodecOptions options = new UniversalCodecOptionsImpl();
			options.setCapabilityVersion(JsonCodecOptions.JsonCapabilityVersion.JSON_WITH_COMMENTS);
			options.setDecorationType(UniversalCodecOptions.DecorationType.FANCY);
			OPTIONS = options;
		}

		private final MapJsonFactory map;

		public JsonTestRegistry() {
			this(OPTIONS);
		}

		public JsonTestRegistry(UniversalCodecOptions options) {
			this(options, CodecFactory.newMapFactory());
		}

		public JsonTestRegistry(UniversalCodecOptions options, MapJsonFactory map) {
			super(options, map);
			this.map = map;
		}

		public final void addFactory(Type type, CodecFactory factory) {
			this.map.addFactory(type, factory);
		}

		public final <CT, E extends Enum<E> & ConfigEnumType<CT>> void addTypedAdapter(Class<CT> type, Class<E> typeClass) {
			this.map.addFactory(type, (t, r) -> new TypedEnumAdapter<>(t, typeClass, r));
		}

		public final <CT> void addTypedAdapter(Class<CT> type, Map<String, ? extends ConfigType<?>> typeMap) {
			this.map.addFactory(type, (t, r) -> new TypedMapAdapter<>(t, typeMap, r));
		}

		@SuppressWarnings("unchecked")
		public final String toJson(Object object) {
			if (object == null) {
				return "null";
			}
			Class<Object> type = (Class<Object>) object.getClass();
			return this.getSerializer(type).toJson(object);
		}

		public final <T> T parseJson(String json, Class<T> type) {
			try {
				UniversalDeserializer<T> deserializer = this.getDeserializer(type);
				return deserializer.parse(json);
			} catch (Exception e) {
				e.printStackTrace(System.err);
			}
			return null;
		}
	}

	public static void main(String[] args) throws IOException, URISyntaxException {
		SKDSLogger.replaceOuts();

		JsonTestRegistry registry = new JsonTestRegistry();

		Map<String, JsonTestRun> runs = createRuns();

		if (args.length != 0) {
			List<String> list = new ArrayList<>(runs.keySet());
			for (String string : args) {
				list.remove(string);
			}
			for (String string : list) {
				runs.remove(string);
			}
		}

		for (JsonTestRun entry : runs.values()) {
			entry.run(registry);
		}

		//throw new RuntimeException(new Exception("seaxe"));

		/*Demo3dShapeHolder shaper = new Demo3dShapeHolder() {
			@Override
			public void addShape(Demo3dShape shape) {
			}
		};
		Demo3dFrameExample.initDefault(shaper);*/
	}
}
