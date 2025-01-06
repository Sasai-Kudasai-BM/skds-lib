package net.skds.lib2.io.json;

import lombok.AllArgsConstructor;
import net.skds.lib2.io.CodecRole;
import net.skds.lib2.io.json.annotation.JsonAlias;
import net.skds.lib2.io.json.annotation.JsonCodecRole;
import net.skds.lib2.io.json.codec.JsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecOptions;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;
import net.skds.lib2.mat.Vec3D;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

public class JsonTest {


	public static void main(String[] args) throws IOException, URISyntaxException {
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

		String json = codec.toJson(amogus);
		System.out.println(json);
		Amogus amogus2 = codec.parse(json);
		String json2 = codec.toJson(amogus2);

		System.out.println(json2);

		System.out.println(json2.equals(json));

	}

	@JsonCodecRole(CodecRole.SERIALIZE)
	@AllArgsConstructor
	private static class Pizdun {
		private String s;
	}

	private static class Amogus {
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
}
