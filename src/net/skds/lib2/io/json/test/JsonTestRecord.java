package net.skds.lib2.io.json.test;

import net.skds.lib2.io.json.annotation.JsonAlias;
import net.skds.lib2.io.json.annotation.TransientComponent;
import net.skds.lib2.io.json.codec.JsonCodec;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;

public record JsonTestRecord(@JsonAlias("hui") int a, @TransientComponent char b, @TransientComponent boolean c) {
	
	public static void test(JsonCodecRegistry registry) {
		String test4 = """
				  {
				  }
				""";

		JsonCodec<JsonTestRecord> cdk = registry.getCodec(JsonTestRecord.class);
		JsonTestRecord a = cdk.parse(test4);
		System.out.println(cdk.toJson(a));
	}
}
