package net.skds.lib2.io.json;

import net.skds.lib2.io.json.codec.JsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecFactory;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;
import net.skds.lib2.io.json.elements.*;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.Function;

public class JsonTest {

	public static void main(String[] args) {
		String test = """
				  {
				   "ab\\"oba"  :  777.7 ,
				   "ses"  :  0x7 ,
				   "say"  :  "gex" ,
				   "amogus"  :  [1488, "aaa", -.3,] ,
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
	}
}
