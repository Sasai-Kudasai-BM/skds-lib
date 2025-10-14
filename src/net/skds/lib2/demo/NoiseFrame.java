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
			},
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
			}
	};

	private static final InterpolationHolder[] interpolations = {
			new InterpolationHolder(FastMath::cosInterpolate, "COS"),
			new InterpolationHolder(FastMath::lerp, "LERP"),
			new InterpolationHolder(FastMath::nearest, "NEAREST")
	};

	private static final AmplitudeFuncHolder[] amplitudeFunctions = {
			new AmplitudeFuncHolder(Noise.AmplitudeFunction.FIBONACCI, "FIBONACCI"),
			new AmplitudeFuncHolder(Noise.AmplitudeFunction.EXPONENT, "EXPONENT"),
			new AmplitudeFuncHolder(Noise.AmplitudeFunction.SQUARE, "SQUARE"),
			new AmplitudeFuncHolder(Noise.AmplitudeFunction.LINEAR, "LINEAR"),
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

	//private boolean useFields = false;
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
			slider.addChangeListener(_ -> {
				depth = slider.getValue();
				zAx.setText("Z-Axis: %.2f".formatted(depth));
				noisePanel.repaint();
			});
			add(slider);

			JLabel exp = new JLabel("Exponent: %.2f".formatted(exponent));
			add(exp);
			JSlider psSlider = new JSlider(1100, 3000, 2000);
			psSlider.addChangeListener(_ -> {
				exponent = psSlider.getValue() * 1e-3f;
				exp.setText("Exponent: %.2f".formatted(exponent));
				updateNoise();
			});
			add(psSlider);

			JLabel seedL = new JLabel("Seed: " + seed);
			add(seedL);
			JSlider sliderSeed = new JSlider(0, 50, 0);
			sliderSeed.addChangeListener(_ -> {
				seed = sliderSeed.getValue();
				seedL.setText("Seed: " + seed);
				updateNoise();
			});
			add(sliderSeed);

			var cb = new JLabel("Color bias: %.2f".formatted(colorBias));
			add(cb);
			JSlider slider2 = new JSlider(-1000, 1000, (int) (colorBias * 1000));
			slider2.addChangeListener(_ -> {
				colorBias = slider2.getValue() * 1E-3f;
				cb.setText("Color bias: %.2f".formatted(colorBias));
				noisePanel.repaint();
			});
			add(slider2);

			var cs = new JLabel("Color scale: %.2f".formatted(colorScale));
			add(cs);
			JSlider slider3 = new JSlider(100, 3000, (int) (colorScale * 1000));
			slider3.addChangeListener(_ -> {
				colorScale = slider3.getValue() * 1E-3f;
				cs.setText("Color scale: %.2f".formatted(colorScale));
				noisePanel.repaint();
			});
			add(slider3);

			var harms = new JLabel("Harmonics: " + harmonics);
			add(harms);
			JSlider slider5 = new JSlider(1, 15, harmonics);
			slider5.addChangeListener(_ -> {
				harmonics = slider5.getValue();
				harms.setText("Harmonics: " + harmonics);
				updateNoise();
			});
			add(slider5);

			add(new JLabel("Color scheme"));
			JComboBox<ColorScheme> schemeSelector = new JComboBox<>(schemes);
			schemeSelector.addActionListener(_ -> {
				colorScheme = (ColorScheme) schemeSelector.getSelectedItem();
				noisePanel.repaint();
			});
			add(schemeSelector);

			add(new JLabel("Interpolation"));
			JComboBox<InterpolationHolder> interpolationSelector = new JComboBox<>(interpolations);
			interpolationSelector.addActionListener(_ -> {
				interpolation = ((InterpolationHolder) Objects.requireNonNull(interpolationSelector.getSelectedItem())).interpolation;
				updateNoise();
			});
			add(interpolationSelector);

			add(new JLabel("AmplitudeFunction"));
			JComboBox<AmplitudeFuncHolder> amplitudeFuncSelector = new JComboBox<>(amplitudeFunctions);
			amplitudeFuncSelector.addActionListener(_ -> {
				amplitudeFunction = ((AmplitudeFuncHolder) Objects.requireNonNull(amplitudeFuncSelector.getSelectedItem())).af;
				updateNoise();
			});
			add(amplitudeFuncSelector);

			//JCheckBox fieldEnable = new JCheckBox("Use Fields");
			//fieldEnable.addActionListener(_ -> {
			//	useFields = fieldEnable.isSelected();
			//	updateNoise();
			//});
			//add(fieldEnable);

			/*
			JPanel ampPanel = new JPanel();
			ampPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 5));
			JSlider ampCount = new JSlider(1, 15, 5);
			ampCount.addChangeListener(_ -> {
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
				amp.addChangeListener(_ -> updateNoise());
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
					amp.addChangeListener(_ -> updateNoise());
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

			ImageUtils.PerPixelDraw draw = (x, y) -> {
				float vx = (x - w2) / scale - cx;
				float vy = (y - h2) / scale - cy;
				float value = noise.getValueInPoint(vx, vy, depth);
				//float value = noise.getValueInPoint(vx, vy);
				return colorScheme.getColor((value + colorBias) * colorScale);
			};

			/*
			if (useFields) {
				Noise.Field field = noise.createFieldBuffer(w, h);
				noise.fillFields(field, -cx / w, -cy / h);
				FloatField2D nf = noise.getField(field);
				draw = (x, y) -> {
					int vx = (int) ((x) / scale);
					int vy = (int) ((y) / scale);

					vx = Math.clamp(vx, 0, w - 1);
					vy = Math.clamp(vy, 0, h - 1);

					float value = nf.getValue(vx, vy);
					return colorScheme.getColor((value + colorBias) * colorScale);
				};
			} else {
				draw = (x, y) -> {
					float vx = (x - w2) / scale - cx;
					float vy = (y - h2) / scale - cy;
					//float value = noise.getValueInPoint(vx, vy, depth);
					float value = noise.getValueInPoint(vx, vy);
					return colorScheme.getColor((value + colorBias) * colorScale);
				};
			}

			 //*/
			Image image = ImageUtils.drawPerPixel(w, h, draw);

			g2d.drawImage(image, 0, 0, w, h, null);
		}
	}

	private interface ColorScheme {
		int getColor(float value);
	}
}
