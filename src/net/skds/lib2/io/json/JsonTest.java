package net.skds.lib2.io.json;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.skds.lib2.io.json.annotation.DefaultJsonCodec;
import net.skds.lib2.io.json.annotation.JsonAlias;
import net.skds.lib2.io.json.annotation.SkipSerialization;
import net.skds.lib2.io.json.annotation.TransientComponent;
import net.skds.lib2.io.json.codec.*;
import net.skds.lib2.io.json.codec.typed.ConfigType;
import net.skds.lib2.io.json.codec.typed.TypedConfig;
import net.skds.lib2.io.json.elements.JsonObject;
import net.skds.lib2.mat.*;
import net.skds.lib2.utils.logger.SKDSLogger;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.*;

@SuppressWarnings("unused")
public class JsonTest {


	public static void main(String[] args) throws IOException, URISyntaxException {
		SKDSLogger.replaceOuts();
		String test = """
				  {
				  	"a": "ass",
				  	"b": ["gf", 23, null, "ff"],
				  	"c": null,
				  	"d": 7,
				  	"e": true,
				  	"f": false,
				  	"g": {
				  		"ab\\"oba": 777.7,
				  		"ses": 0x7,
				  		"say": "gex",
				  		"amogus": [1488, "aaa", -.3,],
				  	}
				  }
				""";

		String test2 = """
				  [[
				  	[null, "a", "b", "c", "d", "e"],
				  	["cum", "jizz"]
				  ]]
				""";

		String test3 = """
				  {
				  "3":1,
				  "34":2,
				  "35":3
				  }
				""";
		String test4 = """
				  {
				  }
				""";


		JsonCodecOptions options = new JsonCodecOptions();
		options.setDecorationType(JsonCodecOptions.DecorationType.FANCY);

		JsonCodecRegistry registry = new JsonCodecRegistry(options, null);
		JsonCodec<Amogus> codec = registry.getCodec(Amogus.class);

		Amogus amogus = new Amogus();
		amogus.vec = new Vec3D(0, 100, 0);
		amogus.amogus = new Amogus();
		amogus.amogus.vec = null;
		amogus.amogus.a = 1488;
		amogus.meps = Map.of(1F, amogus.amogus, 2F, new Amogus());
		amogus.anusis = List.of(new Anus() {
			@Override
			public int hashCode() {
				return super.hashCode();
			}
		});
		amogus.lol = List.of(new Amogus(), new Amogus());

		String json = codec.toJson(amogus);
		System.out.println(json);
		Amogus amogus2 = codec.parse(json);
		String json2 = codec.toJson(amogus2);

		System.out.println(json2);

		System.out.println(json2.equals(json));

		System.out.println();

		JsonCodec<A1> cdk = registry.getCodec(A1.class);
		A1 a = cdk.parse(test4);
		System.out.println(cdk.toJson(a));

		System.out.println();

		Map<String, YupCT> yupMap = Map.of(
				y1.key, y1,
				y2.key, y2,
				y3.key, y3
		);

		JsonUtils.addTypedAdapter(Yup.class, yupMap);
		//JsonUtils.getFancyRegistry().getCodec(Yup.Yup0Error.class);
		//JsonUtils.getFancyRegistry().getCodec(Yup.Yup0.class);
		//JsonUtils.getFancyRegistry().getCodec(Yup.Yup1.class);
		//JsonUtils.getFancyRegistry().getCodec(Yup.Yup2.class);
		//JsonUtils.getFancyRegistry().getCodec(Yup.Yup3.class);

		Yup.Yup0 yup0 = new Yup.Yup0();
		String yupRead = JsonUtils.toJson(yup0);
		System.out.println(yupRead);
		yup0 = JsonUtils.parseJson(yupRead, yup0.getClass());
		System.out.println(yup0);

		Yup.Yup0Error yup0Error = new Yup.Yup0Error();
		String yup0ErrorRead = JsonUtils.toJson(yup0Error);
		System.out.println(yup0ErrorRead);
		yup0Error = JsonUtils.parseJson(yup0ErrorRead, yup0Error.getClass());
		System.out.println(yup0Error);

		Pizdun p = new Pizdun();
		p.yup.add(new Yup.Yup0());
		p.yup.add(new Yup.Yup0Error());
		p.yup.add(new Yup.Yup1());
		p.yup.add(new Yup.Yup1());
		p.yup.add(new Yup.Yup2());
		p.yup.add(new Yup.Yup3());
		String yupJson = JsonUtils.toJson(p);
		System.out.println(yupJson);
		System.out.println(JsonUtils.parseJson(yupJson, Pizdun.class));

		//JsonUtils.saveJson(new File("yup.json"), p);

		System.out.println();

		Map<String, DgAdapter<?>> dgMap = Map.of(
				dg1.keyName, dg1,
				dg2.keyName, dg2,
				dg3.keyName, dg3
		);

		JsonUtils.addTypedAdapter(Dg.class, dgMap);

		DgList dgList = new DgList(Arrays.asList(new Dg.Dg1(), new Dg.Dg2(), new Dg.Dg3()));

		System.out.println(JsonUtils.toJson(new Dg.Dg0()));
		System.out.println(JsonUtils.toJson(dgList));
		System.out.println(JsonUtils.toJson(new Dg.Dg4()));
		System.out.println(JsonUtils.toJson(new Dg.Dg5(new Dg.Dg1("inside"))));

		//Type t = DgAdapter.class.getGenericInterfaces()[0];
		//System.out.println(t);
		//System.out.println(DgAdapter.class.getGenericSuperclass());
		//JsonUtils.saveJson(new File("dg.json"), dgList);

		System.out.println();
		List<String> list = new ArrayList<>();
		list.add("null");
		list.add("1");

		System.out.println(JsonUtils.toJson(list));
		System.out.println(JsonUtils.toJson(new Obj2BoolMapHolder1()));
		System.out.println(JsonUtils.toJson(new HashMap<String, Boolean>()));

		System.out.println(JsonUtils.toJson(new Obj2BoolMapHolder2()));
		System.out.println(JsonUtils.toJson(new Obj2BoolMap<String>()));


		JsonObject jsonNullTest = new JsonObject();
		jsonNullTest.putNull("count");
		System.out.println(JsonUtils.toJson(jsonNullTest));

		System.out.println(Integer.valueOf(1).equals(1));

		String vecTest = JsonUtils.toJson(new TestVec3());
		System.out.println(vecTest);
		System.out.println(JsonUtils.parseJson(vecTest, TestVec3.class));

		String prePostTest = JsonUtils.toJson(new PrePostList());
		JsonUtils.parseJson(prePostTest, PrePostList.class);
	}

	static final YupCT y0 = new YupCT(Yup.Yup0.class, "e0");
	static final YupCT y0Error = new YupCT(Yup.Yup0Error.class, "e0Error");
	static final YupCT y1 = new YupCT(Yup.Yup1.class, "e1");
	static final YupCT y2 = new YupCT(Yup.Yup2.class, "e2");
	static final YupCT y3 = new YupCT(Yup.Yup3.class, "e3");

	@AllArgsConstructor
	private static class YupCT implements ConfigType<Yup> {

		final Class<? extends Yup> tClass;
		final String key;

		@SuppressWarnings("unchecked")
		@Override
		public Class<Yup> getTypeClass() {
			return (Class<Yup>) tClass;
		}

		@Override
		public String keyName() {
			return key;
		}
	}

	private static abstract class Yup implements TypedConfig {
		transient int arab = 0;

		//@DefaultJsonCodec(YupJsonAdapter0.class)
		@ToString
		private static class Yup0 extends Yup {

			Yup ield_0 = new Yup.Yup1();
			Yup ield_0Crash = new Yup.Yup0Error();

			@Override
			public ConfigType<?> getConfigType() {
				return y0;
			}
		}

		//@DefaultJsonCodec(YupJsonAdapter0Error.class)
		@ToString
		private static class Yup0Error extends Yup {

			Yup errorField_0 = new Yup.Yup1();

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
		private static class Yup1 extends Yup {
			@SkipSerialization(defaultInt = 3)
			int sex = 1;

			@Override
			public ConfigType<?> getConfigType() {
				return y1;
			}
		}

		private static class Yup2 extends Yup {
			int sex2 = 2;

			@Override
			public ConfigType<?> getConfigType() {
				return y2;
			}
		}

		private static class Yup3 extends Yup {

			@Override
			public ConfigType<?> getConfigType() {
				return y3;
			}
		}
	}

	@NoArgsConstructor
	@ToString
	private static class Pizdun extends PizdunAss {
		private String s = "s";
		private transient String s2 = "s2";

		private LinkedList<Yup> yup = new LinkedList<>();
	}

	private static class PizdunAss {
		private transient String sPizdunAss = "a";
		private transient String s2PizdunAss = "b";
	}

	static final DgAdapter<?> dg0 = new DgAdapter<>(Dg.Dg0.TYPE, Dg.Dg0.class);
	static final DgAdapter<?> dg1 = new DgAdapter<>(Dg.Dg1.TYPE, Dg.Dg1.class);
	static final DgAdapter<?> dg2 = new DgAdapter<>(Dg.Dg2.TYPE, Dg.Dg2.class);
	static final DgAdapter<?> dg3 = new DgAdapter<>(Dg.Dg3.TYPE, Dg.Dg3.class);
	static final Map<String, DgAdapter<?>> dgMap = Map.of(
			dg0.keyName, dg0,
			dg1.keyName, dg1,
			dg2.keyName, dg2,
			dg3.keyName, dg3
	);

	@NoArgsConstructor
	@AllArgsConstructor
	private static class DgList {
		private List<Dg> list;
	}

	//@JsonCodecRoleConstrains(CodecRole.NONE)
	private abstract static class Dg<T> implements TypedConfig {
		private transient String value;
		protected final transient ConfigType<?> configType = dgMap.get(keyName());

		protected Dg(String value) {
			this.value = value;
		}

		@Override
		public final ConfigType<?> getConfigType() {
			return this.configType;
		}

		protected abstract String keyName();

		private static class Dg0 extends Dg<Integer> {
			static final String TYPE = "dg0";

			@SuppressWarnings("rawtypes")
			private final Dg dgError = new Dg.Dg1();

			public Dg0() {
				super("error");
			}

			@Override
			protected String keyName() {
				return TYPE;
			}
		}

		private static class Dg1 extends Dg<Integer> {
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
		private static class Dg2 extends Dg<String> {
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

		private static class Dg3 extends Dg<Boolean> {
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
			private Dg field = new Dg.Dg1("inside");
		}

		private record Dg5(@SuppressWarnings("rawtypes") Dg dg) {
		}
	}

	@AllArgsConstructor
	public static class DgAdapter<CT> implements ConfigType<CT> {

		private final String keyName;
		@Getter(onMethod_ = {@Override})
		private final Class<CT> typeClass;

		@Override
		public final String keyName() {
			return this.keyName;
		}

		protected void registerJson() {
		}

	}

	private static class Amogus {

		//private Set<Anus> anusis2 = null;
		//private Set<Integer> ints = Set.of(1, 2, 3);

		//private Pizdun ssss = new Pizdun();

		private List<Anus> anusis = null;
		private List<Amogus> lol = null;

		private int a = 1;
		private final int b = 2;
		@JsonAlias("C-Gay")
		private final int c = 3;
		private Vec3D vec = new Vec3D(1, -1, 2);
		//private Pizdun p = new Pizdun("u");
		int d = 3;

		private Map<Float, Amogus> meps;

		private Amogus amogus = null;
	}

	public record A1(@JsonAlias("hui") int a, @TransientComponent char b, @TransientComponent boolean c) {
	}

	@DefaultJsonCodec(AnusCodec.class)
	private interface Anus {

	}

	private static final class AnusCodec extends AbstractJsonCodec<Anus> {

		public AnusCodec(Type type, JsonCodecRegistry registry) {
			super(type, registry);
			System.out.println("create");
		}

		@Override
		public Anus read(JsonReader reader) throws IOException {
			reader.skipNull();
			System.out.println("read");
			return null;
		}

		@Override
		public void write(Anus value, JsonWriter writer) throws IOException {
			writer.writeNull();
			System.out.println("write");
		}
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

	@ToString
	private static class TestVec3 {
		private Vec3I i3 = new Vec3I(1);
		private Vec3F f3 = new Vec3F(2);
		private Vec3D d3 = new Vec3D(3);
		private Vec3 vA3 = new Vec3D(4);
		private Vec3 vB3 = new Vec3I(5);

		private Vec2I i2 = new Vec2I(1);
		private Vec2F f2 = new Vec2F(2);
		private Vec2D d2 = new Vec2D(3);
		private Vec2 vA2 = new Vec2D(4);
		private Vec2 vB2 = new Vec2I(5);
	}

	private static class PrePostList {
		private List<PrePostListImpl> list = Arrays.asList(new PrePostListImpl());
		
		private static class PrePostListImpl implements JsonPostDeserializeCall, JsonPreSerializeCall {
			@Override
			public void preSerializeJson() {
				System.out.println("pre");
			}
			@Override
			public void postDeserializedJson() {
				System.out.println("post");
			}
		}
	}
}
