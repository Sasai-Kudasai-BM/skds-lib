package net.skds.lib2.io.json;

import lombok.AllArgsConstructor;
import net.skds.lib2.io.CodecRole;
import net.skds.lib2.io.json.annotation.DefaultJsonCodec;
import net.skds.lib2.io.json.annotation.JsonAlias;
import net.skds.lib2.io.json.annotation.JsonCodecRoleConstrains;
import net.skds.lib2.io.json.codec.AbstractJsonCodec;
import net.skds.lib2.io.json.codec.JsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecOptions;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;
import net.skds.lib2.mat.Vec3D;
import net.skds.lib2.utils.logger.SKDSLogger;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

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

	}

	@JsonCodecRoleConstrains(CodecRole.SERIALIZE)
	@AllArgsConstructor
	private static class Pizdun {
		private String s;
	}

	private static class Amogus {

		//private Set<Anus> anusis2 = null;
		//private Set<Integer> ints = Set.of(1, 2, 3);

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
