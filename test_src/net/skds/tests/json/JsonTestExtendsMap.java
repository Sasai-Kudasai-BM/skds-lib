package net.skds.tests.json;

import net.skds.lib2.io.codec.SosisonUtils;

import java.util.LinkedHashMap;
import java.util.LinkedList;

@SuppressWarnings("unused")
public class JsonTestExtendsMap {

	public static void test(JsonTest.JsonTestRegistry registry) {

		Test test = new Test();
		test.put("a", "a1");
		Test2 test2 = new Test2();
		test2.add("a");
		test2.add("b");

		System.out.println(SosisonUtils.toJson(test));
		System.out.println(SosisonUtils.parseJson("""
				{
				    "test1": "1",
				    "test2": "2",
				    "ta": "3",
				    "abc": "4"
				}
				""", Test.class)
		);
		System.out.println(SosisonUtils.toJson(test2));
		System.out.println(SosisonUtils.parseJson("""
				[
				    "a1", "b2"
				]
				""", Test2.class)
		);
	}

	private static class Test extends LinkedHashMap<String, String> {

	}

	private static class Test2 extends LinkedList<String> {

	}
}
