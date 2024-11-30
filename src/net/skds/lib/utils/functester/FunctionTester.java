package net.skds.lib.utils.functester;

import net.skds.lib.benchmark.Benchmark;
import net.skds.lib.mat.FastMath;
import net.skds.lib.utils.functester.api.ITestFunction;
import net.skds.lib.utils.functester.gui.Frame;

import java.awt.*;
import java.util.List;
import java.util.*;

public class FunctionTester {


	private static void test() {

		var bm1 = new Benchmark(100, 20) {

			Object[] arr = new Object[1024 * 128];

			int r = 0;

			@Override
			protected void prepare() {
				for (int i = 0; i < arr.length; i++) {
					arr[i] = UUID.randomUUID();
				}
			}

			@Override
			protected void bench() {
				// HashMap 2500/1600
				// ConcurrentHashMap 3500/1900
				// ConcurrentSkipListMap 17000/-1

				// Object2ObjectArrayMap -1/-1
				// Object2ObjectAVLTreeMap 9000/8500
				// Object2ObjectRBTreeMap 9000/10000
				// Object2ObjectLinkedOpenHashMap 3000/1300
				// Object2ObjectOpenHashMap 1700/1000
				Map<Object, Object> map = new HashMap<>();
				for (int i = 0; i < arr.length; i++) {
					Object u = arr[i];
					map.put(u, u);
				}
				for (int j = 0; j < 10; j++) {
					for (int i = 0; i < arr.length; i++) {
						Object u = arr[i];
						r += map.get(u).hashCode();
					}
				}
			}
		};

		bm1.run();

		System.out.println(bm1.result());
		System.out.println(bm1.r);
		System.exit(0);
	}

	//*
	public static void main(String[] args) {
		//test();

		FunctionTester tester = new FunctionTester();
		//*
		tester.addFunction(new ITestFunction() {
			@Override
			public double calc(float input) {
				input /= 5f;
				return (FastMath.sinDegr(input) - Math.sin(Math.toRadians(input))) * 1_000_000;
			}

			@Override
			public Color color() {
				return Color.RED;
			}
		});
		tester.addFunction(new ITestFunction() {
			@Override
			public double calc(float input) {
				input /= 5f;
				return FastMath.sinDegr(input) * 2;
			}

			@Override
			public Color color() {
				return Color.BLUE;
			}
		});
		//tester.addFunction(new ITestFunction() {
		//	@Override
		//	public double calc(float input) {	
		//		int val = (int) input + 1000;
		//		val *= 2;	
		//		if (val < 0 || val >= dat.length - 1) {
		//			return 0;
		//		}	
		//		double dd = dat[val] / 100;
		//		return Math.abs(dd) > .1 ? dd : 0;
		//	}	
		//	@Override
		//	public Color color() {
		//		return Color.RED;
		//	}
		//});
	}
	//*/

	public Frame frame;
	public List<ITestFunction> functions = new ArrayList<>();

	public int pps = 100;

	public float xScale = 1.0F / 200;
	public float yScale = 1.0F;
	public int steps = 5;

	public FunctionTester() {
		this.frame = new Frame(this);
	}

	public void addFunction(ITestFunction function) {
		functions.add(function);
	}

	public int genValue(ITestFunction function, int point) {

		float x = (float) point / (pps * xScale);
		double y = function.calc(x);

		return (int) (y * yScale * pps);
	}

}
