package net.skds.tests;

import net.skds.lib2.benchmark.Benchmark;
import net.skds.lib2.misc.fields.IntField2DImpl;
import net.skds.lib2.utils.ImageUtils;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ImageTest {

	public static void main(String[] args) throws IOException {
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

		JFrame frame = new JFrame();
		frame.add(new JLabel(new ImageIcon(image)));
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
}
