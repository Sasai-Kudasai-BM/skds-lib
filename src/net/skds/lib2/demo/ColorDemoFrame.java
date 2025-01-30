package net.skds.lib2.demo;

import net.skds.lib2.mat.FastMath;
import net.skds.lib2.mat.vec2.Vec2I;
import net.skds.lib2.utils.ColorUtils;
import net.skds.lib2.utils.ImageUtils;

import javax.swing.*;
import java.awt.*;


public class ColorDemoFrame extends JFrame {

	public ColorDemoFrame() {
		super("Color demo");

		add(new HuePanel());

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		setLocation(-getWidth() / 2, -getHeight() / 2);
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);
	}

	private static class HuePanel extends JPanel {

		HuePanel() {
			setPreferredSize(new Dimension(400, 400));
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			int w = getWidth();
			int h = getHeight();
			Vec2I center = new Vec2I(w / 2, h / 2);
			int r = Math.min(w, h) / 2;
			int r2 = r * r;
			int rm = r / 2;
			int rm2 = rm * rm;

			Image image = ImageUtils.drawPerPixel(w, h, (x, y) -> {
				float d2 = center.squareDistanceToF(x, y);
				if (d2 < rm2 || d2 > r2) {
					return 0;
				}
				float hue = .5f + (float) Math.atan2(y - center.yi(), x - center.xi()) / FastMath.TWO_PI;

				return ColorUtils.packARGB(ColorUtils.hueRGB(hue), 255);
			});

			g2d.drawImage(image, 0, 0, w, h, null);
		}
	}


}
