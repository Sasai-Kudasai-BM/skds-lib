package net.skds.tests;

import net.skds.lib2.awtutils.AWTUtils;
import net.skds.lib2.benchmark.Benchmark;
import net.skds.lib2.misc.fields.IntField2DImpl;
import net.skds.lib2.utils.ImageUtils;
import net.skds.lib2.utils.SKDSFiles;
import net.skds.lib2.utils.logger.SKDSLogger;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class ImageTest {

	public static void main(String[] args) throws IOException {
		SKDSLogger.replaceOuts();
		test3();
	}

	static void test3() throws IOException {
		byte[] b1 = Files.readAllBytes(Path.of("run/test.png"));
		byte[] b2 = Files.readAllBytes(Path.of("run/test2.png"));
		BufferedImage image1 = ImageUtils.readPNG(new ByteArrayInputStream(b1));
		BufferedImage image2 = ImageUtils.readPNG(new ByteArrayInputStream(b2));
		File dir = new File("run/out_img");
		File f1a = new File(dir, "test_a.jpg");
		File f2a = new File(dir, "test2_a.jpg");
		File f1b = new File(dir, "test_b.jpg");
		File f2b = new File(dir, "test2_b.jpg");

		SKDSFiles.deleteDirectory(dir);

		System.out.println("F1 A");
		System.out.println(Benchmark.runSimple(() -> {
			ImageUtils.writeJPG(image1, f1a);
		}));
		System.out.println("F2 A");
		System.out.println(Benchmark.runSimple(() -> {
			ImageUtils.writeJPG(image2, f2a);
		}));

		System.out.println("F1 B");
		System.out.println(Benchmark.runSimple(() -> {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			ImageUtils.writeJPG(image1, new BufferedOutputStream(os));
			try {
				Files.write(f1b.toPath(), os.toByteArray(), SKDSFiles.DEFAULT_OPTIONS);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}));
		System.out.println("F2 B");
		System.out.println(Benchmark.runSimple(() -> {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			ImageUtils.writeJPG(image2, new BufferedOutputStream(os));
			try {
				Files.write(f2b.toPath(), os.toByteArray(), SKDSFiles.DEFAULT_OPTIONS);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}));

	}

	static void test2() throws IOException {
		File f1 = new File("run/test.png");
		File f2 = new File("run/test2.png");
		File f1a = new File("run/test_a.png");
		File f2a = new File("run/test2_a.png");
		File f1b = new File("run/test_b.png");
		File f2b = new File("run/test2_b.png");

		System.out.println("F1");
		System.out.println(Benchmark.runSimple(() -> {
			ImageUtils.readPNG(f1);
		}));
		System.out.println("F2");
		System.out.println(Benchmark.runSimple(() -> {
			ImageUtils.readPNG(f2);
		}));

		System.out.println("F1 A");
		System.out.println(Benchmark.runSimple(() -> {
			try (FileInputStream fis = new FileInputStream(f1a)) {
				ImageUtils.readPNG(new BufferedInputStream(fis));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}));
		System.out.println("F2 A");
		System.out.println(Benchmark.runSimple(() -> {
			try (FileInputStream fis = new FileInputStream(f2a)) {
				ImageUtils.readPNG(new BufferedInputStream(fis));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}));

		System.out.println("F1 B");
		System.out.println(Benchmark.runSimple(() -> {
			try {
				InputStream is = new ByteArrayInputStream(Files.readAllBytes(f1b.toPath()));
				ImageUtils.readPNG(is);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}));
		System.out.println("F2 B");
		System.out.println(Benchmark.runSimple(() -> {
			try {
				InputStream is = new ByteArrayInputStream(Files.readAllBytes(f2b.toPath()));
				ImageUtils.readPNG(is);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}));

		byte[] b1 = Files.readAllBytes(f1b.toPath());
		byte[] b2 = Files.readAllBytes(f2b.toPath());

		System.out.println("F1 C");
		System.out.println(Benchmark.runSimple(() -> {
			ImageUtils.readPNG(new ByteArrayInputStream(b1));
		}));
		System.out.println("F2 C");
		System.out.println(Benchmark.runSimple(() -> {
			ImageUtils.readPNG(new ByteArrayInputStream(b2));
		}));

		System.out.println("F1 D");
		System.out.println(Benchmark.runSimple(() -> {
			ImageUtils.readImageUnknown(new ByteArrayInputStream(b1));
		}));
		System.out.println("F2 D");
		System.out.println(Benchmark.runSimple(() -> {
			ImageUtils.readImageUnknown(new ByteArrayInputStream(b2));
		}));

	}

	static void test1() {
		int w = 512;
		int h = 512;
		int w1 = w - 1;
		int h1 = h - 1;

		IntField2DImpl f = new IntField2DImpl(w, h);
		BufferedImage image = ImageUtils.fieldToImage(f);

		int n = 100;
		Benchmark b1 = new Benchmark(n, n) {
			@Override
			protected void prepare() {

			}

			@Override
			protected void bench() {
				for (int y = 0; y < h; y++) {
					for (int x = 0; x < w; x++) {
						int c = (0xff * x / w1) << 16 | (0xff * y / h1);
						image.setRGB(x, y, c | 0xff000000);
					}
				}
			}
		};
		Benchmark b2 = new Benchmark(n, n) {
			@Override
			protected void prepare() {

			}

			@Override
			protected void bench() {
				for (int y = 0; y < h; y++) {
					for (int x = 0; x < w; x++) {
						int c = (0xff * x / w1) << 16 | (0xff * y / h1);
						f.setValue(c | 0xff000000, x, y);
					}
				}
			}
		};
		b1.run();
		b2.run();
		System.out.println(b1.result());
		System.out.println(b2.result());

		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				int c = (0xff * x / w1) << 16 | (0xff * y / h1);
				f.setValue(c | 0xff000000, x, y);
			}
		}


		//Files.write(Path.of("run/test.png"), ImageUtils.writeImageToArrayPng(image), SKDSFiles.DEFAULT_OPTIONS);

		AWTUtils.displayImage(image);
	}
}
