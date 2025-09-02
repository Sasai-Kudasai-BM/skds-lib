package net.skds.lib2.demo;

import net.skds.lib2.misc.fields.FloatField3D;
import net.skds.lib2.utils.ColorUtils;
import net.skds.lib2.utils.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class WaveFrame extends JFrame {

	private static final int SCALE = 4;
	private static final int P_LAYER = 0;
	private static final int VX_LAYER = P_LAYER + 1;
	private static final int VY_LAYER = VX_LAYER + 1;

	private final FloatField3D field = FloatField3D.ofSize(200, 200, VY_LAYER + 1);

	private final WavePanel wavePanel;

	private boolean pause = false;


	//private static final float[] amps = {1, 1, 1, 0, 1, 1, 0, 1};

	WaveFrame() {
		super("Wave demo");

		this.wavePanel = new WavePanel();

		//this.noise = new Noise(0, amps);

		//setLayout(new BorderLayout());
		//add(wavePanel, BorderLayout.WEST);
		add(wavePanel);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		setLocation(-getWidth() / 2, -getHeight() / 2);
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);
	}

	private void addPip(int x, int y) {

		setP(x, y, 1);

		wavePanel.repaint();
	}

	private float getP(int x, int y) {
		return field.getValue(x, y, P_LAYER);
	}

	private void setP(int x, int y, float p) {
		field.setValue(p, x, y, P_LAYER);
	}

	private float getVx(int x, int y) {
		return field.getValue(x, y, VX_LAYER);
	}

	private float getVy(int x, int y) {
		return field.getValue(x, y, VY_LAYER);
	}

	private void setV(int x, int y, float vx, float vy) {
		field.setValue(vx, x, y, VX_LAYER);
		field.setValue(vy, x, y, VY_LAYER);
	}

	private void tick() {
		int w = field.width();
		int h = field.height();

		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				
			}
		}
	}

	private class WavePanel extends JPanel {

		WavePanel() {
			setPreferredSize(new Dimension(field.width() * SCALE, field.height() * SCALE));

			addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (e.getButton() == 3) {
						pause = !pause;
						return;
					}
					addPip(e.getX() / SCALE, e.getY() / SCALE);
				}
			});
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;

			Image image = ImageUtils.drawPerPixel(field.width(), field.height(), (x, y) -> {
				float p = getP(x, y);
				float vx = getVx(x, y);
				float vy = getVy(x, y);
				return ColorUtils.packRGB(p, vx, vy);
			}, false);

			g2d.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		}
	}
}
