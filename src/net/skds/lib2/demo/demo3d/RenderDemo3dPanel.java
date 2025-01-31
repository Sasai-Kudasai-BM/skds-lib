package net.skds.lib2.demo.demo3d;

import lombok.Getter;
import lombok.Setter;
import net.skds.lib2.mat.MatrixStack;
import net.skds.lib2.mat.matrix3.Matrix3;
import net.skds.lib2.mat.matrix4.Matrix4;
import net.skds.lib2.mat.matrix4.Matrix4F;
import net.skds.lib2.mat.vec3.Vec3;
import net.skds.lib2.mat.vec4.Quat;
import net.skds.lib2.shapes.CompositeShape;
import net.skds.lib2.shapes.ConvexShape;
import net.skds.lib2.shapes.Shape;
import net.skds.lib2.utils.linkiges.Pair;
import net.w3e.lib.utils.suppliers.ValueSupplier;

import javax.swing.*;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class RenderDemo3dPanel extends JPanel implements MouseMotionListener, MouseListener {

	private final Demo3dFrame demo;

	@Setter
	@Getter
	private Vec3 cameraPos = Vec3.ZERO;
	@Getter
	private Matrix3 lastRot = Matrix3.SINGLE;

	private List<Supplier<Shape>> shapes = new ArrayList<>();

	@Getter
	private float cameraYaw;
	@Getter
	private float cameraPitch;
	private float cameraFov = 70;

	private int lastMouseX;
	private int lastMouseY;

	//private int lastMouseB;
	private final Robot robot;

	public RenderDemo3dPanel(Demo3dFrame demo) {
		this.demo = demo;
		try {
			this.robot = new Robot();
		} catch (AWTException e) {
			throw new RuntimeException(e);
		}

		addMouseMotionListener(this);
		addMouseListener(this);
	}

	public void addShape(Supplier<Shape> shape) {
		this.shapes.add(shape);
	}

	public void addShape(Shape shape) {
		this.shapes.add(new ValueSupplier<Shape>(shape));
	}

	public void resetMouse() {
		Point p = getLocationOnScreen();
		lastMouseX = getWidth() / 2;
		lastMouseY = getHeight() / 2;
		robot.mouseMove(p.x + lastMouseX, p.y + lastMouseY);
	}

	@Override
	public void paintComponent(Graphics g0) {
		super.paintComponent(g0);
		Graphics2D g = (Graphics2D) g0;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		MatrixStack stack = new MatrixStack(getMatrix());

		this.shapes.forEach(b -> drawShape(g, stack, b.get(), true));
		g.setColor(Color.BLACK);

		stack.close();
	}

	private void drawShape(Graphics2D g, MatrixStack stack, Shape shape, boolean root) {
		if (shape == null) {
			return;
		}
		if (shape instanceof ConvexShape convexShape) {
			g.setColor(Color.GREEN);
			drawPoint(g, stack, convexShape.getCenter(), 2);

			g.setColor(Color.BLACK);
			drawBox(g, stack, convexShape);
		}
		if (shape instanceof CompositeShape compositeShape) {
			if (root) {
				g.setColor(Color.BLUE);
				drawPoint(g, stack, compositeShape.getCenter(), 3);
			} else {
				g.setColor(Color.RED);
				drawPoint(g, stack, compositeShape.getCenter(), 2);
			}

			for (Shape s : compositeShape.getAllShapes()) {
				drawShape(g, stack, s, false);
			}
		}
	}

	private void drawBox(Graphics2D g, MatrixStack stack, ConvexShape box) {
		var lines = box.getLines();
		int w = getWidth();
		int h = getHeight();
		int w2 = w / 2;
		int h2 = h / 2;
		Matrix4 last = stack.getLast();
		for (int i = 0; i < lines.length; i++) {
			Pair<Vec3, Vec3> line = lines[i];
			Vec3 a = line.a().getAsFloatVec4().transformF(last).getAsFloatVec3();
			Vec3 b = line.b().getAsFloatVec4().transformF(last).getAsFloatVec3();
			if ((a.z() < 0 && b.z() < 0) || a.z() > 1 || b.z() > 1) {
				continue;
			}
			g.drawLine((int) (a.x() * w) + w2, (int) (-a.y() * h) + h2, (int) (b.x() * w) + w2, (int) (-b.y() * h) + h2);
		}
	}

	private void drawPoint(Graphics2D g, MatrixStack stack, Vec3 point, float size) {
		int w = getWidth();
		int h = getHeight();
		int w2 = w / 2;
		int h2 = h / 2;
		point = point.getAsFloatVec4().transformF(stack.getLast()).getAsFloatVec3();
		if (point.z() < 0 || point.z() > 1) {
			return;
		}

		int s = (int) (100 * size * (1 - point.z()));
		int s2 = s / 2;

		g.fillOval((int) (point.x() * w) + w2 - s2, (int) -(point.y() * h) + h2 - s2, s, s);
	}

	public Matrix4F getMatrix() {
		Matrix4F proj = Matrix4.perspectiveInfinityF(cameraFov, (float) getWidth() / getHeight(), .2f);
		Quat q = Quat.fromAxisDegrees(Vec3.YP, cameraYaw).rotateAxisDegrees(Vec3.XP, cameraPitch);
		this.lastRot = Matrix3.fromQuat(q);

		return proj.multiplyF(Matrix4.fromMatrix3F(this.lastRot).transposeF().translateF(cameraPos));
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mouseMoved(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		int dX = e.getX() - this.lastMouseX;
		int dY = e.getY() - this.lastMouseY;

		this.lastMouseX = e.getX();
		this.lastMouseY = e.getY();

		if (this.demo.isFocus()) {
			this.cameraYaw -= dX * .2f;
			this.cameraPitch -= dY * .2f;

			if (this.cameraPitch > 90) this.cameraPitch = 90;
			if (this.cameraPitch < -90) this.cameraPitch = -90;

			//onMouseMoved();
			this.repaint();
			this.demo.toolPanel.repaint();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {
		if (demo.isFocus()) resetMouse();
	}

}
