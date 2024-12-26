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
import java.util.ArrayList;
import java.util.List;

public class NoiseFrame extends JFrame {

	private static final ColorScheme[] schemes = {
			new ColorScheme() {
				@Override
				public int getColor(float value) {
					int br = FastMath.clamp((int) (value * 255), 0, 255);
					return ColorUtils.packARGB(br, br, br, 255);
				}

				@Override
				public String toString() {
					return "GRAY";
				}
			},
			new ColorScheme() {
				@Override
				public int getColor(float value) {
					int br = FastMath.clamp((int) ((1 - value) * 255), 0, 255);
					return ColorUtils.packARGB(br, br, br, 255);
				}

				@Override
				public String toString() {
					return "GRAY INVERTED";
				}
			},
			new ColorScheme() {
				@Override
				public int getColor(float value) {
					int hue = ColorUtils.hueRGB(FastMath.clamp((1 - value) * 0.667f, 0, 0.667f));
					return ColorUtils.packARGB(hue, 255);
				}

				@Override
				public String toString() {
					return "HUE";
				}
			},
			new ColorScheme() {
				@Override
				public int getColor(float value) {
					int hue = ColorUtils.hueRGB(FastMath.clamp(value * 0.667f, 0, 0.667f));
					return ColorUtils.packARGB(hue, 255);
				}

				@Override
				public String toString() {
					return "HUE INVERTED";
				}
			}
	};

	private final List<JSlider> ampSliders = new ArrayList<>();

	private final NoisePanel noisePanel;

	private float scale = 15;
	private float cx = 0;
	private float cy = 0;
	private float depth = 0;

	private float periodScale = 1;

	private float colorScale = 1.5f;
	private float colorBias = -.2f;

	private ColorScheme colorScheme = schemes[0];
	private long seed = 0;

	private Noise noise;


	//private static final float[] amps = {1, 1, 1, 0, 1, 1, 0, 1};

	NoiseFrame() {
		super("Noise demo");

		this.noisePanel = new NoisePanel();
		ControlPanel controlPanel = new ControlPanel();

		//this.noise = new Noise(0, amps);

		setLayout(new BorderLayout());
		add(controlPanel, BorderLayout.EAST);
		add(noisePanel, BorderLayout.WEST);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		setLocation(-getWidth() / 2, -getHeight() / 2);
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);
	}

	private class ControlPanel extends JPanel {
		ControlPanel() {
			setPreferredSize(new Dimension(400, 400));
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

			add(new JLabel("Z-Axis"));
			JSlider slider = new JSlider(0, 200, 0);
			slider.addChangeListener(e -> {
				depth = slider.getValue() * 1e-2f;
				noisePanel.repaint();
			});
			add(slider);

			add(new JLabel("Period scale"));
			JSlider psSlider = new JSlider(100, 2000, 1000);
			psSlider.addChangeListener(e -> {
				periodScale = psSlider.getValue() * 1e-3f;
				recreateNoise();
			});
			add(psSlider);

			add(new JLabel("Seed"));
			JSlider sliderSeed = new JSlider(0, 50, 0);
			sliderSeed.addChangeListener(e -> {
				seed = sliderSeed.getValue();
				recreateNoise();
			});
			add(sliderSeed);

			add(new JLabel("Color bias"));
			JSlider slider2 = new JSlider(-1000, 1000, (int) (colorBias * 1000));
			slider2.addChangeListener(e -> {
				colorBias = slider2.getValue() * 1E-3f;
				noisePanel.repaint();
			});
			add(slider2);


			add(new JLabel("Color scale"));
			JSlider slider3 = new JSlider(100, 3000, (int) (colorScale * 1000));
			slider3.addChangeListener(e -> {
				colorScale = slider3.getValue() * 1E-3f;
				noisePanel.repaint();
			});
			add(slider3);

			add(new JLabel("Color scheme"));
			JComboBox<ColorScheme> schemeSelector = new JComboBox<>(schemes);
			schemeSelector.addActionListener(e -> {
				colorScheme = (ColorScheme) schemeSelector.getSelectedItem();
				noisePanel.repaint();
			});
			add(schemeSelector);

			JPanel ampPanel = new JPanel();

			ampPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 5));

			JSlider ampCount = new JSlider(1, 15, 5);
			ampCount.addChangeListener(e -> {
				setAmpSliders(ampSliders, ampPanel, ampCount.getValue());
				noisePanel.repaint();
			});
			ampCount.setSnapToTicks(true);

			int dc = ampCount.getValue() - ampSliders.size();
			for (int i = 0; i < dc; i++) {
				JSlider amp = new JSlider(JSlider.VERTICAL, 0, 200, 100);
				amp.setPreferredSize(new Dimension(22, 160));
				ampPanel.add(amp);
				ampSliders.add(amp);
				amp.addChangeListener(e -> recreateNoise());
			}

			add(ampPanel);
			add(ampCount);
			recreateNoise();
		}

		private void setAmpSliders(List<JSlider> ampSliders, JPanel ampPanel, int count) {
			int dc = count - ampSliders.size();
			if (dc > 0) {
				for (int i = 0; i < dc; i++) {
					JSlider amp = new JSlider(JSlider.VERTICAL, 0, 200, 100);
					amp.setPreferredSize(new Dimension(20, 160));
					ampPanel.add(amp);
					ampSliders.add(amp);
					amp.addChangeListener(e -> recreateNoise());
				}
				ampPanel.revalidate();
				recreateNoise();
			} else if (dc < 0) {
				for (int i = 0; i > dc; i--) {
					JSlider amp = ampSliders.removeLast();
					ampPanel.remove(amp);
				}
				ampPanel.repaint();
				recreateNoise();
			}
		}
	}

	private void recreateNoise() {
		float[] amps = new float[ampSliders.size()];
		for (int i = 0; i < amps.length; i++) {
			amps[i] = ampSliders.get(i).getValue() * 1e-2f;
		}
		this.noise = new Noise(seed, amps, periodScale);
		noisePanel.repaint();
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
				return colorScheme.getColor((value + colorBias) * colorScale);
			});

			g2d.drawImage(image, 0, 0, w, h, null);
		}
	}

	private interface ColorScheme {
		int getColor(float value);
	}
}
