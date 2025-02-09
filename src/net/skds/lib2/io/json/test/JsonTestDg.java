package net.skds.lib2.io.json.test;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.skds.lib2.io.json.JsonUtils;
import net.skds.lib2.io.json.annotation.DefaultJsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;
import net.skds.lib2.io.json.codec.JsonDeserializeBuilder;
import net.skds.lib2.io.json.codec.JsonReflectiveBuilderCodec;
import net.skds.lib2.io.json.codec.typed.ConfigType;
import net.skds.lib2.io.json.codec.typed.TypedConfig;

@SuppressWarnings("unused")
public abstract class JsonTestDg<T> implements TypedConfig {

	private static final DgAdapter<?> dg0 = new DgAdapter<>(Dg0.TYPE, Dg0.class);
	private static final DgAdapter<?> dg1 = new DgAdapter<>(Dg1.TYPE, Dg1.class);
	private static final DgAdapter<?> dg2 = new DgAdapter<>(Dg2.TYPE, Dg2.class);
	private static final DgAdapter<?> dg3 = new DgAdapter<>(Dg3.TYPE, Dg3.class);
	private static final Map<String, DgAdapter<?>> dgMap = Map.of(
			dg0.keyName, dg0,
			dg1.keyName, dg1,
			dg2.keyName, dg2,
			dg3.keyName, dg3
	);

	public static void test(JsonCodecRegistry registry) {
		JsonUtils.addTypedAdapter(JsonTestDg.class, dgMap);

		DgList dgList = new DgList(Arrays.asList(new Dg1(), new Dg2(), new Dg3()));

		System.out.println(JsonUtils.toJson(new Dg0()));
		System.out.println(JsonUtils.toJson(dgList));
		System.out.println(JsonUtils.toJson(new Dg4()));
		System.out.println(JsonUtils.toJson(new Dg5(new Dg1("inside"))));

		//Type t = DgAdapter.class.getGenericInterfaces()[0];
		//System.out.println(t);
		//System.out.println(DgAdapter.class.getGenericSuperclass());
		//JsonUtils.saveJson(new File("dg.json"), dgList);
	}

	@NoArgsConstructor
	@AllArgsConstructor
	private static class DgList {
		private List<JsonTestDg> list;
	}

	private transient String value;
	protected final transient ConfigType<?> configType = dgMap.get(keyName());

	protected JsonTestDg(String value) {
		this.value = value;
	}

	@Override
	public final ConfigType<?> getConfigType() {
		return this.configType;
	}

	protected abstract String keyName();

	private static class Dg0 extends JsonTestDg<Integer> {
		static final String TYPE = "dg0";

		@SuppressWarnings("rawtypes")
		private final JsonTestDg dgError = new Dg1();

		public Dg0() {
			super("error");
		}

		@Override
		protected String keyName() {
			return TYPE;
		}
	}

	private static class Dg1 extends JsonTestDg<Integer> {
		static final String TYPE = "dg1";

		public Dg1() {
			super("");
		}

		public Dg1(String value) {
			super(value);
		}

		@Override
		protected String keyName() {
			return TYPE;
		}
	}

	@DefaultJsonCodec(Dg2.Dg2JsAdapter.class)
	private static class Dg2 extends JsonTestDg<String> {
		static final String TYPE = "dg2";
		private int a1 = 5;

		public Dg2() {
			super("");
		}

		public Dg2(String value) {
			super(value);
		}

		@Override
		protected String keyName() {
			return TYPE;
		}

		private static class Dg2JsAdapter extends JsonReflectiveBuilderCodec<Dg2JsAdapter.Dg2JsData> {

			public Dg2JsAdapter(Type type, JsonCodecRegistry registry) {
				super(type, Dg2JsData.class, registry);
			}

			private static class Dg2JsData implements JsonDeserializeBuilder<Dg2> {

				@Override
				public Dg2 build() {
					Dg2 dg2 = new Dg2("a");
					dg2.a1 *= -1;
					return dg2;
				}
			}
		}
	}

	private static class Dg3 extends JsonTestDg<Boolean> {
		static final String TYPE = "dg3";
		private int a2 = 5;
		private int a3 = 5;

		public Dg3() {
			super("");
		}

		public Dg3(String value) {
			super(value);
		}

		@Override
		protected String keyName() {
			return TYPE;
		}
	}

	private static class Dg4 {
		@SuppressWarnings("rawtypes")
		private JsonTestDg field = new Dg1("inside");
	}

	private record Dg5(@SuppressWarnings("rawtypes") JsonTestDg dg) {}

	@AllArgsConstructor
	public static class DgAdapter<CT> implements ConfigType<CT> {

		private final String keyName;
		@Getter(onMethod_ = {@Override})
		private final Class<CT> typeClass;

		@Override
		public final String keyName() {
			return this.keyName;
		}
	}
}
