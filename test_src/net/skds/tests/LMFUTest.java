package net.skds.tests;

import net.skds.lib2.benchmark.Benchmark;
import net.skds.lib2.reflection.FieldHandles;
import net.skds.lib2.utils.logger.SKDSLogger;
import net.skds.lib2.utils.logger.SKDSLoggerConfig;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class LMFUTest {

	static void main() throws NoSuchFieldException {
		SKDSLoggerConfig.compactConfig();
		SKDSLogger.replaceOuts();
		Test to = new Test();

		BiConsumer<Test, String> setter1 = FieldHandles.objSetter(Test.class.getDeclaredField("b"));
		Function<Test, String> getter1 = FieldHandles.objGetter(Test.class.getDeclaredField("b"));
		System.out.println(getter1.apply(to));
		setter1.accept(to, "lol");
		System.out.println(getter1.apply(to));

		var setter2 = FieldHandles.intSetter(Test.class.getDeclaredField("a"));
		var getter2 = FieldHandles.intGetter(Test.class.getDeclaredField("a"));
		System.out.println(getter2.get(to));
		setter2.accept(to, 1488);
		System.out.println(getter2.get(to));

		var setter3 = FieldHandles.booleanSetter(Test.class.getDeclaredField("c"));
		var getter3 = FieldHandles.booleanGetter(Test.class.getDeclaredField("c"));
		System.out.println(getter3.get(to));
		setter3.accept(to, true);
		System.out.println(getter3.get(to));

		{
			var setter = FieldHandles.byteSetter(Test.class.getDeclaredField("d"));
			var getter = FieldHandles.byteGetter(Test.class.getDeclaredField("d"));
			System.out.println(getter.get(to));
			setter.accept(to, (byte) 123);
			System.out.println(getter.get(to));
		}
		{
			var setter = FieldHandles.shortSetter(Test.class.getDeclaredField("e"));
			var getter = FieldHandles.shortGetter(Test.class.getDeclaredField("e"));
			System.out.println(getter.get(to));
			setter.accept(to, (short) 1233);
			System.out.println(getter.get(to));
		}
		{
			var setter = FieldHandles.charSetter(Test.class.getDeclaredField("f"));
			var getter = FieldHandles.charGetter(Test.class.getDeclaredField("f"));
			System.out.println(getter.get(to));
			setter.accept(to, 'f');
			System.out.println(getter.get(to));
		}
		{
			var setter = FieldHandles.longSetter(Test.class.getDeclaredField("g"));
			var getter = FieldHandles.longGetter(Test.class.getDeclaredField("g"));
			System.out.println(getter.get(to));
			setter.accept(to, 111111111111111111L);
			System.out.println(getter.get(to));
		}
		{
			var setter = FieldHandles.floatSetter(Test.class.getDeclaredField("h"));
			var getter = FieldHandles.floatGetter(Test.class.getDeclaredField("h"));
			System.out.println(getter.get(to));
			setter.accept(to, 111.222F);
			System.out.println(getter.get(to));
		}
		{
			var setter = FieldHandles.doubleSetter(Test.class.getDeclaredField("i"));
			var getter = FieldHandles.doubleGetter(Test.class.getDeclaredField("i"));
			System.out.println(getter.get(to));
			setter.accept(to, 111333444.222555666D);
			System.out.println(getter.get(to));
		}

		Test[] t2 = new Test[1024];
		for (int i = 0; i < t2.length; i++) {
			t2[i] = new Test();
			t2[i].a = i;
			t2[i].b = String.valueOf(i);
			t2[i].c = i % 3 == 0;
		}

		System.gc();
		System.out.println(Benchmark.runSimple(() -> {
			for (int i = 0; i < 10; i++) {
				for (Test t : t2) {
					String oldStr = getter1.apply(t);
					int oldInt = getter2.get(t);
					boolean oldB = getter3.get(t);
					setter1.accept(t, oldStr);
					setter2.accept(t, oldInt + 1);
					setter3.accept(t, !oldB);
				}
			}
		}));
		Field f1 = Test.class.getDeclaredField("a");
		Field f2 = Test.class.getDeclaredField("b");
		Field f3 = Test.class.getDeclaredField("c");
		System.gc();
		System.out.println(Benchmark.runSimple(() -> {
			for (int i = 0; i < 10; i++) {
				for (Test t : t2) {
					try {
						String oldStr = (String) f2.get(t);
						int oldInt = f1.getInt(t);
						boolean oldB = f3.getBoolean(t);

						f2.set(t, oldStr);
						f1.setInt(t, oldInt + 1);
						f3.setBoolean(t, !oldB);
					} catch (IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				}
			}

		}));

		System.out.println(Arrays.hashCode(t2));
	}

	private static class Test {
		int a;
		String b;
		boolean c;
		byte d;
		short e;
		char f;
		long g;
		float h;
		double i;
	}
}
