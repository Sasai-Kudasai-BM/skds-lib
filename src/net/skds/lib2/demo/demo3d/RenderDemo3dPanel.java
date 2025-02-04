package net.skds.lib2.demo.demo3d;

import net.skds.lib2.demo.demo3d.Demo3dShape.DemoShape3dHolder;
import net.skds.lib2.mat.MatrixStack;
import net.skds.lib2.mat.matrix4.Matrix4;
import net.skds.lib2.mat.vec3.Vec3;
import net.skds.lib2.shapes.Collision;
import net.skds.lib2.shapes.CompositeShape;
import net.skds.lib2.shapes.ConvexShape;
import net.skds.lib2.shapes.Shape;
import net.skds.lib2.utils.linkiges.Pair;

import javax.swing.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class RenderDemo3dPanel extends JPanel {

	private final Demo3dFrame demo;

	//private int lastMouseB;

	public RenderDemo3dPanel(Demo3dFrame demo) {
		this.demo = demo;
	}

	@Override
	public void paintComponent(Graphics g0) {
		super.paintComponent(g0);
		Graphics2D g = (Graphics2D) g0;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		List<DemoShape3dHolder> list = new ArrayList<>();

		for (Demo3dShape shape : this.demo.shapes) {
			shape.tick();
			shape.setHovered(null);
			collectShape(shape, list::add);
		}

		Vec3 startPos = this.demo.getCameraPos();
		Vec3 endPos = startPos.add(Vec3.ZN.scale(5).transform(this.demo.getLastRot()));

		double distance = Double.MAX_VALUE;
		DemoShape3dHolder result = null;
		for (DemoShape3dHolder s : list) {
			Shape shape = s.getShape();
			Collision collision = shape.raytrace(startPos, 
				endPos
			);

			if (collision != null && collision.distance() <= distance) {
				distance = collision.distance();
				result = s;
			}
		}
		if (result == null && list.size() == 1) {
			result = list.getFirst();
		}
		if (result != null) {
			result.setHovered();
			this.demo.setHovered(result.root);
		}

		MatrixStack stack = new MatrixStack(this.demo.getMatrix());

		for (DemoShape3dHolder shape : list) {
			drawShape(g, stack, shape.getShape(), true);
		}

		g.setColor(Color.BLACK);

		stack.close();
	}

	private void collectShape(Object object, Consumer<DemoShape3dHolder> apply) {
		if (object == null) {
			return;
		}
		if (object instanceof DemoShape3dHolder shape) {
			if (shape.getShape() != null) {
				apply.accept(shape);
			}
			return;
		}
		if (object instanceof Collection collection) {
			for (Object o : collection) {
				collectShape(o, apply);
			}
			return;
		}
		if (object.getClass().isArray()) {
			for (Object o : ((Object[])object)) {
				collectShape(o, apply);
			}
			return;
		}
		if (object instanceof Demo3dShape shape) {
			collectShape(shape.getShape(), apply);
			return;
		}
		if (object instanceof Shape shape) {
			throw new ClassCastException(shape.getClass() + " is not " + DemoShape3dHolder.class);
		}
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

}
