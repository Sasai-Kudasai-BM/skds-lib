package net.skds.lib2.io.json;

import net.skds.lib2.io.json.codec.JsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecFactory;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;
import net.skds.lib2.io.json.elements.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.function.Function;

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

		Map<Type, Function<JsonCodecRegistry, JsonCodec<?>>> builtinCodecs = Map.of(
				JsonObject.class, JsonObject.Codec::new,
				JsonElement.class, JsonElement.Codec::new,
				JsonString.class, JsonString.Codec::new,
				JsonNumber.class, JsonNumber.Codec::new,
				JsonBoolean.class, JsonBoolean.Codec::new,
				JsonArray.class, JsonArray.Codec::new
		);

		JsonCodecFactory factory = (t, r) -> {
			var f = builtinCodecs.get(t);
			if (f != null) {
				return f.apply(r);
			}
			return null;
		};

		JsonCodecRegistry registry = new JsonCodecRegistry(factory);
		JsonObject jo = registry.getCodec(JsonObject.class).parse(test);
		System.out.println(jo);

		//kek("");
		//kek2("", new ArrayList<>());


		//CodeSource src = JsonTest.class.getProtectionDomain().getCodeSource();
		//if (src != null) {
		//	URL jar = src.getLocation();
		//	ZipInputStream zip = new ZipInputStream(jar.openStream());
		//	while (true) {
		//		ZipEntry e = zip.getNextEntry();
		//		if (e == null) {
		//			System.out.println("end");
		//			break;
		//		}
		//		String name = e.getName();
		//		System.out.println(name);
		//	}
		//}

		//kek("net/skds/lib2/io/json/");
	}
}
