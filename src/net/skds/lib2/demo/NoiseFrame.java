package net.skds.lib2.demo;

import net.skds.lib2.mat.FastMath;
import net.skds.lib2.misc.noise.Noise;
import net.skds.lib2.utils.ColorUtils;
import net.skds.lib2.utils.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public class NoiseFrame extends JFrame {

	private final NoisePanel noisePanel;
	private final ControlPanel controlPanel;

	private float scale = 1;
	private float cx = 0;
	private float cy = 0;

	private float depth = 0;


	private Noise noise;

	private static final float[] amps = {1, 1, 1, 0, 1, 1, 0, 1};

	NoiseFrame() {
		super("Noise demo");

		this.noisePanel = new NoisePanel();
		this.controlPanel = new ControlPanel();

		this.noise = new Noise(0, amps);

		setLayout(new BorderLayout());
		add(controlPanel, BorderLayout.NORTH);
		add(noisePanel, BorderLayout.CENTER);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		setLocation(-getWidth() / 2, -getHeight() / 2);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private class ControlPanel extends JPanel {
		ControlPanel() {
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			add(new JLabel("Z-Axis"));
			JSlider slider = new JSlider(0, 1000, 0);
			slider.addChangeListener(e -> {
				depth = slider.getValue() / 100f;
				noisePanel.repaint();
			});
			add(slider);
		}

	}

	private class NoisePanel extends JPanel {

		int mx;
		int my;

		NoisePanel() {
			setPreferredSize(new Dimension(400, 400));

			addMouseWheelListener(new MouseAdapter() {
				@Override
				public void mouseWheelMoved(MouseWheelEvent e) {
					int dw = e.getWheelRotation();
					scale *= 1 - (dw * .1f);
					if (scale < 1) {
						scale = 1;
					}
					noisePanel.repaint();
				}
			});

			addMouseMotionListener(new MouseAdapter() {
				@Override
				public void mouseDragged(MouseEvent e) {
					cx += (e.getX() - mx) / scale;
					cy += (e.getY() - my) / scale;
					mx = e.getX();
					my = e.getY();
					repaint();
				}
			});

			addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					mx = e.getX();
					my = e.getY();
				}
			});
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			int w = getWidth();
			int h = getHeight();
			int w2 = w / 2;
			int h2 = h / 2;

			Image image = ImageUtils.drawPerPixel(w, h, (x, y) -> {
				float vx = (x - w2) / scale - cx;
				float vy = (y - h2) / scale - cy;
				float value = noise.getValueInPoint(vx, vy, depth);
				int br = FastMath.clamp((int) (value * 255 / 2), 0, 255);
				//float br = FastMath.clamp(value / 2 - .2f, 0, 1);
				//int rgb = ColorUtils.scaleRGB(200, ColorUtils.hueRGB(br));
				int rgb = ColorUtils.grayRGB(br);
				return ColorUtils.packARGB(rgb, 255);
			});

			g2d.drawImage(image, 0, 0, w, h, null);
		}
	}
}
