package net.skds.tests;

import net.skds.lib2.utils.logger.SKDSLogger;

public class LambdaTest {

	public static void main(String[] args) {
		SKDSLogger.replaceOuts();

		int s = 3;

		Runnable r1 = () -> kek();
		Runnable r2 = () -> kek();
		Runnable r3 = LambdaTest::kek;
		Runnable r4 = LambdaTest::kek;
		Runnable r5 = () -> kek(1337);
		Runnable r6 = () -> kek(1337);
		Runnable r7 = () -> kek(s);
		Runnable r8 = () -> kek(s);
		//Runnable r9 = () -> kek(s + 1);

		test(r1, r2);
		test(r3, r4);
		test(r5, r6);
		test(r7, r8);

		System.out.println("=========================");
		Runnable a1 = null;
		Runnable a2 = null;
		for (int i = 0; i < 2; i++) {
			a1 = a2;
			a2 = LambdaTest::kek;
		}
		test(a1, a2);

		System.out.println("=========================");
		a1 = null;
		a2 = null;
		for (int i = 0; i < 2; i++) {
			a1 = a2;
			a2 = () -> kek(s);
		}
		test(a1, a2);

		System.out.println("=========================");
		a1 = null;
		a2 = null;
		for (int i = 0; i < 2; i++) {
			a1 = a2;
			a2 = () -> kek();
		}
		test(a1, a2);

		System.out.println("=========================");
		a1 = null;
		a2 = null;
		for (int i = 0; i < 2; i++) {
			a1 = a2;
			a2 = () -> kek(k++);
		}
		test(a1, a2);
	}

	static int k;

	static void test(Runnable r1, Runnable r2) {
		System.out.println((r1 == r2) + " " + r1.equals(r2) + " " + (r1.hashCode() == r2.hashCode()));
	}

	private static void kek() {
		System.out.println("kek");
	}

	private static void kek(int i) {
		System.out.println("kek " + i);
	}
}
