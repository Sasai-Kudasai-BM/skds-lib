package net.skds.tests.json;

import lombok.NoArgsConstructor;
import lombok.ToString;
import net.skds.lib2.io.codec.SosisonUtils;
import net.skds.lib2.io.codec.typed.ConfigType;
import net.skds.lib2.io.codec.typed.TypedConfig;
import net.skds.tests.json.JsonTest.JsonTestRegistry;

@ToString
@NoArgsConstructor
public class JsonTestEmptyTyped implements TypedConfig {

	public static void test(JsonTestRegistry registry) {
		JsonTestEmptyTyped test = new JsonTestEmptyTyped();
		test.a = 15;
		String json = SosisonUtils.toJsonCompact(test);
		System.out.println(json);
		System.out.println(SosisonUtils.parseJson(json, JsonTestEmptyTyped.class));
	}

	private int a = 10;
	private int b = 5;

	@Override
	public ConfigType<?> getConfigType() {
		return null;
	}
}
