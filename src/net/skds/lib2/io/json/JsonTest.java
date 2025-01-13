package net.skds.lib2.io.json;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.skds.lib2.io.json.annotation.DefaultJsonCodec;
import net.skds.lib2.io.json.annotation.JsonAlias;
import net.skds.lib2.io.json.annotation.TransientComponent;
import net.skds.lib2.io.json.codec.*;
import net.skds.lib2.io.json.codec.typed.ConfigType;
import net.skds.lib2.io.json.codec.typed.TypedConfig;
import net.skds.lib2.mat.Vec3D;
import net.skds.lib2.utils.logger.SKDSLogger;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

		Pizdun p = new Pizdun();
		p.yup.add(new Yup.Yup1());
		p.yup.add(new Yup.Yup1());
		p.yup.add(new Yup.Yup2());
		p.yup.add(new Yup.Yup3());
		System.out.println(JsonUtils.toJson(p));

		//JsonUtils.saveJson(new File("yup.json"), p);

		System.out.println();

		Map<String, DgAdapter<?>> dgMap = Map.of(
				dg1.keyName, dg1,
				dg2.keyName, dg2,
				dg3.keyName, dg3
		);

		JsonUtils.addTypedAdapter(Dg.class, dgMap);

		DgList dgList = new DgList(Arrays.asList(new Dg.Dg1(), new Dg.Dg2(), new Dg.Dg3()));

		System.out.println(JsonUtils.toJson(dgList));
		System.out.println(JsonUtils.toJson(new Dg.Dg4()));
		System.out.println(JsonUtils.toJson(new Dg.Dg5(new Dg.Dg1("inside"))));

		//JsonUtils.saveJson(new File("dg.json"), dgList);
	}

	static final YupCT y1 = new YupCT(Yup.Yup1.class, "e");
	static final YupCT y2 = new YupCT(Yup.Yup2.class, "e2");
	static final YupCT y3 = new YupCT(Yup.Yup3.class, "e3");

	@AllArgsConstructor
	private static class YupCT implements ConfigType<Yup> {

		final Class<? extends Yup> tClass;
		final String key;

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

		private static class Yup1 extends Yup {
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
	private static class Pizdun extends PizdunAss {
		private String s = "s";
		private transient String s2 = "s2";

		private LinkedList<Yup> yup = new LinkedList<>();
	}

	private static class PizdunAss {
		private transient String sPizdunAss = "a";
		private transient String s2PizdunAss = "b";
	}

	static final DgAdapter<?> dg1 = new DgAdapter<>(Dg.Dg1.TYPE, Dg.Dg1.class);
	static final DgAdapter<?> dg2 = new DgAdapter<>(Dg.Dg2.TYPE, Dg.Dg2.class);
	static final DgAdapter<?> dg3 = new DgAdapter<>(Dg.Dg3.TYPE, Dg.Dg3.class);
	static final Map<String, DgAdapter<?>> dgMap = Map.of(
			dg1.keyName, dg1,
			dg2.keyName, dg2,
			dg3.keyName, dg3
	);

	@NoArgsConstructor
	@AllArgsConstructor
	private static class DgList {
		private List<Dg> list;
	}

	private abstract static class Dg implements TypedConfig {
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

		private static class Dg1 extends Dg {
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
		private static class Dg2 extends Dg {
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

		private static class Dg3 extends Dg {
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
			private Dg field = new Dg.Dg1("inside");
		}

		private record Dg5(Dg dg) {
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

	public record A1(int a, @TransientComponent char b, @TransientComponent boolean c) {
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
}
