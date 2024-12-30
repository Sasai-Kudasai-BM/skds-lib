package net.skds.lib2.io.json;

import net.skds.lib2.io.json.codec.JsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecOptions;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;
import net.skds.lib2.utils.Types;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

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
		JsonCodec<List<String>> codec = registry.getCodec(Types.parameterizedType(List.class, String.class));

		System.out.println(codec.parse(test2));

	}
}
