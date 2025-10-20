package net.skds.tests.json;

@SuppressWarnings("unused")
public class JsonTestClassHack {

	private record Test(String s, int i) {
	}

	public static void test(JsonTest.JsonTestRegistry registry) {

		//Test test0 = new Test("aoa", 1337);
		//String json = SosisonUtils.toJson(test0);
		//System.out.println(json);
		//Test test1 = SosisonUtils.parseJson(json);
		//System.out.println(test1.equals(test0));
	}
}
