package net.skds.lib2.io.json;

import net.skds.lib2.io.json.codec.JsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;
import net.skds.lib2.io.json.elements.JsonObject;

import java.lang.reflect.Type;
import java.util.Map;

public class JsonTest {

	public static void main(String[] args) {
		String test = """
				  {
				   "aboba"  :  777.7 ,
				   "ses"  :  0x7 ,
				  }
				""";

		Map<Type, JsonCodec<?>> basicCodecs = Map.of(
				JsonObject.class, new JsonObject.Codec()
		);

		JsonCodecRegistry registry = new JsonCodecRegistry(null, basicCodecs);

		JsonObject jo = registry.getCodec(JsonObject.class).parse(test);
		System.out.println(jo);
	}
}
