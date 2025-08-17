package net.skds.tests;

import net.skds.lib2.misc.fields.IntField2DImpl;
import net.skds.lib2.utils.ImageUtils;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ImageTest {

	public static void main(String[] args) throws IOException {
		int w = 512;
		int h = 512;

		IntField2DImpl f = new IntField2DImpl(w, h);

		int w1 = w - 1;
		int h1 = h - 1;

		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				int c = (0xff * x / w1) << 16 | (0xff * y / h1);
				f.setValue(c | 0xff000000, x, y);
			}
		}

		BufferedImage image = ImageUtils.fieldToImage(f);

		//Files.write(Path.of("run/test.png"), ImageUtils.writeImageToArrayPng(image), SKDSFiles.DEFAULT_OPTIONS);

		JFrame frame = new JFrame();
		frame.add(new JLabel(new ImageIcon(image)));
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
}
