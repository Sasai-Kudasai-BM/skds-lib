package net.skds.tests.json;

import net.skds.lib2.io.codec.SosisonUtils;
import net.skds.lib2.io.json.elements.JsonObject;
import net.skds.tests.json.JsonTest.JsonTestRegistry;

import java.util.HashMap;

public class JsonTestNull {

	public static void test(JsonTestRegistry registry) {
		String data = "{\"value\":null}";

		JsonObject json = SosisonUtils.parseJson(data, JsonObject.class);

		System.out.println(json);

		System.out.println(SosisonUtils.toJson(json));
		System.out.println(SosisonUtils.toJson(new HashMap<>(json)));
	}
}
