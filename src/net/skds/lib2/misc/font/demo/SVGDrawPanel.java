package net.skds.lib2.misc.font.demo;

import net.skds.lib2.mat.FastMath;
import net.w3e.lib.mat.BezierCurve;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;

public class SVGDrawPanel extends JPanel {

	private final int curveSteps = 10;
	private final Color lineColor = Color.GREEN;
	private final Color triangleColor = Color.MAGENTA;
	private final Color curve2Color = Color.BLUE;
	private final Color curve3Color = Color.RED;
	private final Color curveColor = Color.YELLOW;

	private final FontRenderContext frc = new FontRenderContext(null, false, false);

	private GeneralPath path = new GeneralPath();

	private float dx = 500 - 100;
	private float dy = 400 + 50;

	private int lmX = 0;
	private int lmY = 0;

	private float scale = 10;

	public SVGDrawPanel() {
		setBackground(Color.GRAY);

		MouseAdapter ml = new MouseAdapter() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				float s = scale - scale * e.getWheelRotation() * 0.1f;
				scale = FastMath.clamp(s, 0.1f, 40f);
				repaint();
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();
				dx += (x - lmX);
				dy += (y - lmY);
				lmX = x;
				lmY = y;
				repaint();
			}

			@Override
			public void mousePressed(MouseEvent e) {
				lmX = e.getX();
				lmY = e.getY();
			}
		};

		addMouseListener(ml);
		addMouseMotionListener(ml);
		addMouseWheelListener(ml);
	}

	public void setChar(Font font, char c) {
		char[] arr = new char[]{c};
		GlyphVector glyphVector = font.createGlyphVector(frc, arr);
		this.path = (GeneralPath) glyphVector.getGlyphOutline(0);
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		float lastX = 0;
		float lastY = 0;
		float startX = 0;
		float startY = 0;
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		PathIterator iterator = this.path.getPathIterator(null);
		float[] buffer = new float[6];
		while (!iterator.isDone()) {
			int type = iterator.currentSegment(buffer);
			iterator.next();
			switch (type) {
				case PathIterator.SEG_MOVETO -> {
					lastX = buffer[0];
					lastY = buffer[1];
					startX = lastX;
					startY = lastY;
				}
				case PathIterator.SEG_LINETO -> {
					float newX = buffer[0];
					float newY = buffer[1];
					g2d.setColor(lineColor);
					g2d.drawLine(scaleX(lastX), scaleY(lastY), scaleX(newX), scaleY(newY));
					lastX = newX;
					lastY = newY;
				}
				case PathIterator.SEG_QUADTO -> {
					float curveX = buffer[0];
					float curveY = buffer[1];
					float newX = buffer[2];
					float newY = buffer[3];
					g2d.setColor(curve2Color);
					g2d.drawLine(scaleX(lastX), scaleY(lastY), scaleX(curveX), scaleY(curveY));
					g2d.drawLine(scaleX(curveX), scaleY(curveY), scaleX(newX), scaleY(newY));

					float lcx = lastX;
					float lcy = lastY;
					g2d.setColor(curveColor);
					for (int i = 0; i <= curveSteps; i++) {
						float p = (float) i / curveSteps;
						float ncx = BezierCurve.curve1(p, lastX, curveX, newX);
						float ncy = BezierCurve.curve1(p, lastY, curveY, newY);
						g2d.drawLine(scaleX(lcx), scaleY(lcy), scaleX(ncx), scaleY(ncy));
						lcx = ncx;
						lcy = ncy;
					}

					lastX = newX;
					lastY = newY;
				}
				case PathIterator.SEG_CUBICTO -> {
					float curveX = buffer[0];
					float curveY = buffer[1];
					float curve2X = buffer[2];
					float curve2Y = buffer[3];
					float newX = buffer[4];
					float newY = buffer[5];
					g2d.setColor(curve3Color);
					g2d.drawLine(scaleX(lastX), scaleY(lastY), scaleX(curveX), scaleY(curveY));
					g2d.drawLine(scaleX(curveX), scaleY(curveY), scaleX(curve2X), scaleY(curve2Y));
					g2d.drawLine(scaleX(curve2X), scaleY(curve2Y), scaleX(newX), scaleY(newY));

					float lcx = lastX;
					float lcy = lastY;
					g2d.setColor(curveColor);
					for (int i = 0; i <= curveSteps; i++) {
						float p = (float) i / curveSteps;
						float ncx = BezierCurve.curve2(p, lastX, curveX, curve2X, newX);
						float ncy = BezierCurve.curve2(p, lastY, curveY, curve2Y, newY);
						g2d.drawLine(scaleX(lcx), scaleY(lcy), scaleX(ncx), scaleY(ncy));
						lcx = ncx;
						lcy = ncy;
					}

					lastX = newX;
					lastY = newY;
				}
				case PathIterator.SEG_CLOSE -> {
					g2d.setColor(lineColor);
					g2d.drawLine(scaleX(lastX), scaleY(lastY), scaleX(startX), scaleY(startY));
				}
				default -> throw new RuntimeException("Unknown type " + type);
			}
		}
	}

	int scaleX(float cord) {
		return (int) (cord * scale + dx);
	}

	int scaleY(float cord) {
		return (int) (cord * scale + dy);
	}
}
