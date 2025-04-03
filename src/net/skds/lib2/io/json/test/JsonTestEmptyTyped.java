package net.skds.lib2.io.json.test;

import lombok.NoArgsConstructor;
import lombok.ToString;
import net.skds.lib2.io.json.JsonUtils;
import net.skds.lib2.io.json.codec.typed.ConfigType;
import net.skds.lib2.io.json.codec.typed.TypedConfig;
import net.skds.lib2.io.json.test.JsonTest.JsonTestRegistry;

@ToString
@NoArgsConstructor
public class JsonTestEmptyTyped implements TypedConfig {

	public static void test(JsonTestRegistry registry) {
		JsonTestEmptyTyped test = new JsonTestEmptyTyped();
		test.a = 15;
		String json = JsonUtils.toJsonCompact(test);
		System.out.println(json);
		System.out.println(JsonUtils.parseJson(json, JsonTestEmptyTyped.class));
	}

	private int a = 10;
	private int b = 5;

	@Override
	public ConfigType<?> getConfigType() {
		return null;
	}
}
