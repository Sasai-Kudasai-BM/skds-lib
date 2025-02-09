package net.skds.lib2.io.json.test;

import java.util.LinkedList;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.skds.lib2.io.json.JsonUtils;
import net.skds.lib2.io.json.annotation.SkipSerialization;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;
import net.skds.lib2.io.json.codec.typed.ConfigType;
import net.skds.lib2.io.json.codec.typed.TypedConfig;

@SuppressWarnings("unused")
public abstract class JsonTestYup implements TypedConfig {

	public static void test(JsonCodecRegistry registry) {
		Map<String, YupCT> yupMap = Map.of(
				y1.key, y1,
				y2.key, y2,
				y3.key, y3
		);

		JsonUtils.addTypedAdapter(JsonTestYup.class, yupMap);
		//JsonUtils.getFancyRegistry().getCodec(Yup.Yup0Error.class);
		//JsonUtils.getFancyRegistry().getCodec(Yup.Yup0.class);
		//JsonUtils.getFancyRegistry().getCodec(Yup.Yup1.class);
		//JsonUtils.getFancyRegistry().getCodec(Yup.Yup2.class);
		//JsonUtils.getFancyRegistry().getCodec(Yup.Yup3.class);

		JsonTestYup.Yup0 yup0 = new JsonTestYup.Yup0();
		String yupRead = JsonUtils.toJson(yup0);
		System.out.println(yupRead);
		yup0 = JsonUtils.parseJson(yupRead, yup0.getClass());
		System.out.println(yup0);

		JsonTestYup.Yup0Error yup0Error = new JsonTestYup.Yup0Error();
		String yup0ErrorRead = JsonUtils.toJson(yup0Error);
		System.out.println(yup0ErrorRead);
		yup0Error = JsonUtils.parseJson(yup0ErrorRead, yup0Error.getClass());
		System.out.println(yup0Error);

		Pizdun p = new Pizdun();
		p.yup.add(new JsonTestYup.Yup0());
		p.yup.add(new JsonTestYup.Yup0Error());
		p.yup.add(new JsonTestYup.Yup1());
		p.yup.add(new JsonTestYup.Yup1());
		p.yup.add(new JsonTestYup.Yup2());
		p.yup.add(new JsonTestYup.Yup3());
		String yupJson = JsonUtils.toJson(p);
		System.out.println(yupJson);
		System.out.println(JsonUtils.parseJson(yupJson, Pizdun.class));

		//JsonUtils.saveJson(new File("yup.json"), p);
	}

	private static final YupCT y0 = new YupCT(JsonTestYup.Yup0.class, "e0");
	private static final YupCT y0Error = new YupCT(JsonTestYup.Yup0Error.class, "e0Error");
	private static final YupCT y1 = new YupCT(JsonTestYup.Yup1.class, "e1");
	private static final YupCT y2 = new YupCT(JsonTestYup.Yup2.class, "e2");
	private static final YupCT y3 = new YupCT(JsonTestYup.Yup3.class, "e3");

	@AllArgsConstructor
	private static class YupCT implements ConfigType<JsonTestYup> {

		final Class<? extends JsonTestYup> tClass;
		final String key;

		@SuppressWarnings("unchecked")
		@Override
		public Class<JsonTestYup> getTypeClass() {
			return (Class<JsonTestYup>)tClass;
		}

		@Override
		public String keyName() {
			return key;
		}
	}

	@NoArgsConstructor
	@ToString
	private static class Pizdun extends PizdunAss {
		private String s = "s";
		private transient String s2 = "s2";

		private LinkedList<JsonTestYup> yup = new LinkedList<>();
	}

	private static class PizdunAss {
		private transient String sPizdunAss = "a";
		private transient String s2PizdunAss = "b";
	}


	transient int arab = 0;

	//@DefaultJsonCodec(YupJsonAdapter0.class)
	@ToString
	private static class Yup0 extends JsonTestYup {

		JsonTestYup ield_0 = new JsonTestYup.Yup1();
		JsonTestYup ield_0Crash = new JsonTestYup.Yup0Error();

		@Override
		public ConfigType<?> getConfigType() {
			return y0;
		}
	}

	//@DefaultJsonCodec(YupJsonAdapter0Error.class)
	@ToString
	private static class Yup0Error extends JsonTestYup {

		JsonTestYup errorField_0 = new JsonTestYup.Yup1();

		@Override
		public ConfigType<?> getConfigType() {
			return y0Error;
		}
	}

	/*
	private static class YupJsonAdapter0 extends JsonReflectiveBuilderCodec<Yup> {

		public YupJsonAdapter0(Type type, JsonCodecRegistry registry) {
			super(type, YupErrorBuilder.class, registry);
		}

		private static class YupErrorBuilder implements JsonDeserializeBuilder<Yup> {

			Yup ield_0 = new Yup.Yup1();
			Yup errorField_0 = new Yup.Yup1();

			@Override
			public Yup build() {
				return new Yup0();
			}
		}
	}



	private static class YupJsonAdapter0Error extends JsonReflectiveBuilderCodec<Yup> {

		public YupJsonAdapter0Error(Type type, JsonCodecRegistry registry) {
			super(type, YupErrorBuilder.class, registry);
		}

		private static class YupErrorBuilder implements JsonDeserializeBuilder<Yup> {

			Yup ield_0 = new Yup.Yup1();
			Yup errorField_0 = new Yup.Yup1();

			@Override
			public Yup build() {
				return new Yup0Error();
			}
		}
	}
	 */

	@ToString
	private static class Yup1 extends JsonTestYup {
		@SkipSerialization(defaultInt = 3)
		int sex = 1;

		@Override
		public ConfigType<?> getConfigType() {
			return y1;
		}
	}

	private static class Yup2 extends JsonTestYup {
		int sex2 = 2;

		@Override
		public ConfigType<?> getConfigType() {
			return y2;
		}
	}

	private static class Yup3 extends JsonTestYup {

		@Override
		public ConfigType<?> getConfigType() {
			return y3;
		}
	}
}
