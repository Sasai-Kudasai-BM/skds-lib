package net.skds.lib2.io.json.test;

import java.util.Arrays;
import java.util.List;

import net.skds.lib2.io.json.JsonPostDeserializeCall;
import net.skds.lib2.io.json.JsonPreSerializeCall;
import net.skds.lib2.io.json.JsonUtils;
import net.skds.lib2.io.json.codec.JsonCodecRegistry;
import net.skds.lib2.io.json.elements.JsonElement;
import net.skds.lib2.io.json.elements.JsonObject;

public class JsonTestSimple {
	public static void test(JsonCodecRegistry registry) {
		JsonObject jsonNullTest = new JsonObject();
		jsonNullTest.putNull("count");
		System.out.println(JsonUtils.toJson(jsonNullTest));

		System.out.println(Integer.valueOf(1).equals(1));
		System.out.println(long.class == Long.TYPE);

		String test = """
			{
				"a": "ass",
				"b": ["gf", 23, null, "ff"],
				"c": null,
				"d": 7,
				"e": true,
				"f": false,
				// am gay
				/* am gay2
				lines sex */
				"g": {
					"ab\\"oba": 777.7,
					"ses": 0x7,
					"say": "gex",
					"amogus": [1488, "aaa", -.3,],
				}
			}
		  """;
		System.out.println(JsonUtils.parseJson(test, JsonElement.class));

		String prePostTest = JsonUtils.toJson(new PrePostList());
		JsonUtils.parseJson(prePostTest, PrePostList.class);
	}

	private static class PrePostList {
		@SuppressWarnings("unused")
		private List<PrePostListImpl> list = Arrays.asList(new PrePostListImpl());

		private static class PrePostListImpl implements JsonPostDeserializeCall, JsonPreSerializeCall {
			@Override
			public void preSerializeJson() {
				System.out.println("pre");
			}

			@Override
			public void postDeserializedJson() {
				System.out.println("post");
			}
		}
	}
}
