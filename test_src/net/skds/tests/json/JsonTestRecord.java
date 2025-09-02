package net.skds.tests.json;

import net.skds.lib2.io.codec.UniversalCodec;
import net.skds.lib2.io.codec.annotation.SerializationAlias;
import net.skds.lib2.io.codec.annotation.TransientComponent;
import net.skds.tests.json.JsonTest.JsonTestRegistry;

public record JsonTestRecord(@SerializationAlias("hui") int a, @TransientComponent char b,
							 @TransientComponent boolean c) {

	public static void test(JsonTestRegistry registry) {
		String test4 = """
				  {
				  }
				""";

		UniversalCodec<JsonTestRecord> cdk = registry.getCodec(JsonTestRecord.class);
		JsonTestRecord a = cdk.parse(test4);
		System.out.println(cdk.toJson(a));
	}
}
