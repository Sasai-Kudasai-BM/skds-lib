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
import java.util.Objects;

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

	private static final InterpolationHolder[] interpolations = {
			new InterpolationHolder(FastMath::cosInterpolate, "COS"),
			new InterpolationHolder(FastMath::lerp, "LERP"),
			new InterpolationHolder(FastMath::nearest, "NEAREST")
	};

	private static final AmplitudeFuncHolder[] amplitudeFunctions = {
			new AmplitudeFuncHolder((l, e) -> {
				float amp = e;
				for (int i = 1; i < l; i++) {
					amp *= e;
				}
				return 1 / amp;
			}, "EXPONENT"),
			new AmplitudeFuncHolder((l, e) -> {
				float a = (l + 1) * e;
				return 1f / (a * a);
			}, "SQUARE"),
			new AmplitudeFuncHolder((l, e) -> 1f / (l + 1), "LINEAR"),
			new AmplitudeFuncHolder((l, e) -> {
				float a = 1;
				float a0 = 1;
				for (int i = 1; i < l; i++) {
					float b = a;
					a += a0;
					a0 = b;
				}
				return 1f / a;
			}, "FIBONACCI"),
	};

	private record InterpolationHolder(FastMath.FloatInterpolation interpolation, String name) {
		@Override
		public String toString() {
			return name;
		}
	}

	private record AmplitudeFuncHolder(Noise.AmplitudeFunction af, String name) {
		@Override
		public String toString() {
			return name;
		}
	}

	//private final List<JSlider> ampSliders = new ArrayList<>();

	private final NoisePanel noisePanel;

	private float scale = 1;
	private float cx = 0;
	private float cy = 0;
	private float depth = 0;

	private float exponent = 2;

	private int harmonics = 7;

	private float colorScale = 1.5f;
	private float colorBias = -.2f;

	private Noise.AmplitudeFunction amplitudeFunction = amplitudeFunctions[0].af;
	private FastMath.FloatInterpolation interpolation = interpolations[0].interpolation;
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

			JLabel zAx = new JLabel("Z-Axis: %.2f".formatted(depth));
			add(zAx);
			JSlider slider = new JSlider(0, 200, 0);
			slider.addChangeListener(e -> {
				depth = slider.getValue();
				zAx.setText("Z-Axis: %.2f".formatted(depth));
				noisePanel.repaint();
			});
			add(slider);

			JLabel exp = new JLabel("Exponent: %.2f".formatted(exponent));
			add(exp);
			JSlider psSlider = new JSlider(1100, 3000, 2000);
			psSlider.addChangeListener(e -> {
				exponent = psSlider.getValue() * 1e-3f;
				exp.setText("Exponent: %.2f".formatted(exponent));
				updateNoise();
			});
			add(psSlider);

			JLabel seedL = new JLabel("Seed: " + seed);
			add(seedL);
			JSlider sliderSeed = new JSlider(0, 50, 0);
			sliderSeed.addChangeListener(e -> {
				seed = sliderSeed.getValue();
				seedL.setText("Seed: " + seed);
				updateNoise();
			});
			add(sliderSeed);

			var cb = new JLabel("Color bias: %.2f".formatted(colorBias));
			add(cb);
			JSlider slider2 = new JSlider(-1000, 1000, (int) (colorBias * 1000));
			slider2.addChangeListener(e -> {
				colorBias = slider2.getValue() * 1E-3f;
				cb.setText("Color bias: %.2f".formatted(colorBias));
				noisePanel.repaint();
			});
			add(slider2);

			var cs = new JLabel("Color scale: %.2f".formatted(colorScale));
			add(cs);
			JSlider slider3 = new JSlider(100, 3000, (int) (colorScale * 1000));
			slider3.addChangeListener(e -> {
				colorScale = slider3.getValue() * 1E-3f;
				cs.setText("Color scale: %.2f".formatted(colorScale));
				noisePanel.repaint();
			});
			add(slider3);

			var harms = new JLabel("Harmonics: " + harmonics);
			add(harms);
			JSlider slider5 = new JSlider(1, 15, harmonics);
			slider5.addChangeListener(e -> {
				harmonics = slider5.getValue();
				harms.setText("Harmonics: " + harmonics);
				updateNoise();
			});
			add(slider5);

			add(new JLabel("Color scheme"));
			JComboBox<ColorScheme> schemeSelector = new JComboBox<>(schemes);
			schemeSelector.addActionListener(e -> {
				colorScheme = (ColorScheme) schemeSelector.getSelectedItem();
				noisePanel.repaint();
			});
			add(schemeSelector);

			add(new JLabel("Interpolation"));
			JComboBox<InterpolationHolder> interpolationSelector = new JComboBox<>(interpolations);
			interpolationSelector.addActionListener(e -> {
				interpolation = ((InterpolationHolder) Objects.requireNonNull(interpolationSelector.getSelectedItem())).interpolation;
				updateNoise();
			});
			add(interpolationSelector);

			add(new JLabel("AmplitudeFunction"));
			JComboBox<AmplitudeFuncHolder> amplitudeFuncSelector = new JComboBox<>(amplitudeFunctions);
			amplitudeFuncSelector.addActionListener(e -> {
				amplitudeFunction = ((AmplitudeFuncHolder) Objects.requireNonNull(amplitudeFuncSelector.getSelectedItem())).af;
				updateNoise();
			});
			add(amplitudeFuncSelector);

			/*
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
				amp.setPreferredSize(new Dimension(22, 120));
				ampPanel.add(amp);
				ampSliders.add(amp);
				amp.addChangeListener(e -> updateNoise());
			}
			add(ampPanel);
			add(ampCount);
			 */

			updateNoise();
		}

		/*
		private void setAmpSliders(List<JSlider> ampSliders, JPanel ampPanel, int count) {
			int dc = count - ampSliders.size();
			if (dc > 0) {
				for (int i = 0; i < dc; i++) {
					JSlider amp = new JSlider(JSlider.VERTICAL, 0, 200, 100);
					amp.setPreferredSize(new Dimension(20, 120));
					ampPanel.add(amp);
					ampSliders.add(amp);
					amp.addChangeListener(e -> updateNoise());
				}
				ampPanel.revalidate();
				updateNoise();
			} else if (dc < 0) {
				for (int i = 0; i > dc; i--) {
					JSlider amp = ampSliders.removeLast();
					ampPanel.remove(amp);
				}
				ampPanel.repaint();
				updateNoise();
			}
		}
		 */
	}

	private void updateNoise() {
		//float[] amps = new float[ampSliders.size()];
		//for (int i = 0; i < amps.length; i++) {
		//	amps[i] = ampSliders.get(i).getValue() * 1e-2f;
		//}
		this.noise = new Noise(seed, harmonics, amplitudeFunction, exponent, interpolation);
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
					if (scale < 0.1) {
						scale = .1f;
					} else if (scale > 10) {
						scale = 10;
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
