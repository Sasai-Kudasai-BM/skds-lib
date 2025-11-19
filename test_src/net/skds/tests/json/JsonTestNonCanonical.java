package net.skds.tests.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JsonTestNonCanonical {

	public static void test(JsonTest.JsonTestRegistry registry) {
		//System.out.println("collection: " + registry.parseJson("[[\"first\"],[\"second\"]]", TestCollection.class));

		/*System.out.println();
		System.out.println();
		System.out.println();
		System.out.println("test");

		var classes = ReflectUtils.getReflectSuperTypes(TestCollection.class).reversed();

		for (var cl : classes) {
			System.out.println(cl);
		}

		boolean superType = false;
		Type type = null;*/

		/*b1:
		for (var cl : classes) {
			for (var t : cl.superParameters()) {
				if (t instanceof TypeVariable<?> typeVariable) {
					if (typeVariable.getName().equals("E")) {
						superType = true;
						continue b1;
					}
					continue;
				}
			}
			System.out.println(cl);
		}
		System.out.println("\n" + type);*/

		/*int keyTypeIndex = -1;
		int valueTypeIndex = -1;
		Type keyType = null;
		Type valueType = null;

		ReflectUtils.ReflectSuperType prev = null;

		var classes = ReflectUtils.getReflectSuperTypes(TestMap.class).reversed();
		b1:
		for (var cl : classes) {
			var parameters = cl.parameters();
			for (int i = 0; i < parameters.length; i++) {
				var t = parameters[i];
				if (t instanceof TypeVariable<?> typeVariable) {
					String name = typeVariable.getName();
					if (keyTypeIndex == -1 && name.equals("K")) {
						keyTypeIndex = i;
						continue;
					}
					if (valueTypeIndex == -1 && name.equals("V")) {
						valueTypeIndex = i;
						continue;
					}
					if (i == keyTypeIndex) {
						System.out.println();
						System.out.println(cl.cl());
						System.out.println(cl.annotatedSuperclass());
						System.out.println(prev.cl());
						System.out.println(prev.cl());
					}
					continue;
				}
				if ((t instanceof Class<?> || t instanceof ParameterizedType)) {
					//if (superKeyType == i && keyType == null) {
					//	keyType = t;
					//}
					//if (superValueType == i && valueType == null) {
					//	valueType = t;
					//}
				}
			}
			prev = cl;
		}

		System.out.println();
		System.out.println("key " + keyType);
		System.out.println("value " + valueType);*/
	}

	private static class TwoField {
		private List<String> a;
		private List<String> b;
	}

	//@DefaultCodec(SimpleExtendsCollectionCodec.class)
	private static class TestCollection extends TestCollectionTyped<Integer, String> {
	}

	private static abstract class TestCollectionTyped<T, K> extends ArrayList<List<K>> {
	}

	//@DefaultCodec(SimpleExtendsMapCodec.class)
	private static class TestMap extends TestMapTyped<Integer, String> {
	}

	private static class TestMapTyped<T, R> extends HashMap<R, List<String>> {
	}
}
