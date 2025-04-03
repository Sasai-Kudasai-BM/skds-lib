package net.skds.lib2.io.json.test;

import java.util.HashMap;

import net.skds.lib2.io.json.JsonUtils;
import net.skds.lib2.io.json.elements.JsonObject;
import net.skds.lib2.io.json.test.JsonTest.JsonTestRegistry;

public class JsonTestNull {
	
	public static void test(JsonTestRegistry registry) {
		String data = "{\"value\":null}";

		JsonObject json = JsonUtils.parseJson(data, JsonObject.class);

		System.out.println(json);

		System.out.println(JsonUtils.toJson(json));
		System.out.println(JsonUtils.toJson(new HashMap<>(json)));
	}
}
