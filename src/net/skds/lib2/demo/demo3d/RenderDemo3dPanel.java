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

import lombok.AllArgsConstructor;
import lombok.ToString;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class RenderDemo3dPanel extends JPanel {

	private final Demo3dFrame demo;

	//private int lastMouseB;

	private int w = getWidth();
	private int h = getHeight();
	private int w2 = w / 2;
	private int h2 = h / 2;

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
			shape.tick(this.demo);
			shape.setHovered(null);
			collectShapeHolder(shape, list::add);
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

		this.w = getWidth();
		this.h = getHeight();
		this.w2 = w / 2;
		this.h2 = h / 2;

		if (this.demo.isSort()) {
			List<RenderObject> sorted = new ArrayList<>();

			// TODO sort
			for (DemoShape3dHolder shape : list) {
				collectShape(shape, sorted::add, true);
			}

			Collections.sort(sorted);

			for (RenderObject obj : sorted) {
				if (!obj.render) {
					g.setColor(Color.BLUE);
					drawPoint(g, stack, obj.shape.getCenter(), 3);
				} else {
					drawShape(g, stack, obj.shape, obj.root);
				}
			}
			//System.out.println(sorted.get(0));

		} else {
			for (DemoShape3dHolder shape : list) {
				drawShape(g, stack, shape.getShape(), true);
			}
		}

		Vec3 axisPos = createAxisPoint(Vec3.ZERO);

		g.setColor(Color.RED);
		drawLine(g, stack, axisPos, createAxisPoint(Vec3.XP));

		g.setColor(Color.GREEN);
		drawLine(g, stack, axisPos, createAxisPoint(Vec3.YP));

		g.setColor(Color.BLUE);
		drawLine(g, stack, axisPos, createAxisPoint(Vec3.ZP));

		g.setColor(Color.BLACK);

		stack.close();
	}

	@ToString
	@AllArgsConstructor
	private class RenderObject implements Comparable<RenderObject> {
		private final Shape shape;
		private final boolean root;
		private final boolean render;
		@Override
		public int compareTo(RenderObject o) {
			return -Double.compare(this.shape.getCenter().squareDistanceTo(demo.getCameraPos()), o.shape.getCenter().squareDistanceTo(demo.getCameraPos()));
		}
	}

	private Vec3 createAxisPoint(Vec3 pos) {
		return this.demo.getCameraPos().add(
			Vec3.ZN
			.transform(this.demo.getLastRot())
			.add(pos.scale(0.025)).scale(0.5)
		);
	}

	private void collectShapeHolder(Object object, Consumer<DemoShape3dHolder> apply) {
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
				collectShapeHolder(o, apply);
			}
			return;
		}
		if (object.getClass().isArray()) {
			for (Object o : ((Object[])object)) {
				collectShapeHolder(o, apply);
			}
			return;
		}
		if (object instanceof Demo3dShape shape) {
			collectShapeHolder(shape.getShape(), apply);
			return;
		}
		if (object instanceof Shape shape) {
			throw new ClassCastException(shape.getClass() + " is not " + DemoShape3dHolder.class);
		}
	}

	private void collectShape(Object object, Consumer<RenderObject> apply, boolean root) {
		if (object == null) {
			return;
		}
		if (object instanceof Shape shape) {
			if (shape instanceof CompositeShape compositeShape) {
				apply.accept(new RenderObject(shape, root, false));
				for (Shape s : compositeShape.getAllShapes()) {
					collectShape(s, apply, false);
				}
			} else {
				apply.accept(new RenderObject(shape, root, true));
			}
			return;
		}
		if (object instanceof DemoShape3dHolder shape) {
			collectShape(shape.getShape(), apply, true);
			return;
		}
		if (object instanceof Collection collection) {
			for (Object o : collection) {
				collectShape(o, apply, false);
			}
			return;
		}
		if (object.getClass().isArray()) {
			for (Object o : ((Object[])object)) {
				collectShape(o, apply, false);
			}
			return;
		}
		throw new ClassCastException(object.getClass() + " is not " + Shape.class);
	}

	private void drawShape(Graphics2D g, MatrixStack stack, Shape shape, boolean root) {
		if (shape == null) {
			return;
		}
		if (shape instanceof ConvexShape convexShape) {
			g.setColor(Color.BLACK);
			drawBox(g, stack, convexShape);
		}
		if (shape instanceof CompositeShape compositeShape) {
			for (Shape s : compositeShape.getAllShapes()) {
				drawShape(g, stack, s, false);
			}
		}
		if (root) {
			g.setColor(Color.BLUE);
			drawPoint(g, stack, shape.getCenter(), 3);
		} else {
			g.setColor(Color.RED);
			drawPoint(g, stack, shape.getCenter(), 2);
		}
		if (shape instanceof ConvexShape) {
			g.setColor(Color.GREEN);
			drawPoint(g, stack, shape.getCenter(), 1);
		}
	}

	private void drawBox(Graphics2D g, MatrixStack stack, ConvexShape box) {
		var lines = box.getLines();

		Stroke stroke = g.getStroke();
		g.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		for (int i = 0; i < lines.length; i++) {
			Pair<Vec3, Vec3> line = lines[i];
			Vec3 a = line.a();
			Vec3 b = line.b();
			
			drawLine(g, stack, a, b);
		}
		g.setStroke(stroke);

		if (this.demo.isFill()) {
			Color color = g.getColor();
			g.setColor(Color.GRAY);
			//down
			drawSurface(g, stack, lines[0].a(), lines[1].a(), lines[2].a(), lines[3].a());
			//up
			drawSurface(g, stack, lines[4].a(), lines[5].a(), lines[6].a(), lines[7].a());
			//forward
			drawSurface(g, stack, lines[1].a(), lines[5].a(), lines[6].a(), lines[2].a());
			//backward
			drawSurface(g, stack, lines[0].a(), lines[4].a(), lines[7].a(), lines[3].a());
			//left
			drawSurface(g, stack, lines[3].a(), lines[2].a(), lines[6].a(), lines[7].a());
			//right
			drawSurface(g, stack, lines[0].a(), lines[1].a(), lines[5].a(), lines[4].a());
			g.setColor(color);
		}
	}

	private void drawSurface(Graphics2D g, MatrixStack stack, Vec3... points) {
		Matrix4 last = stack.getLast();

		Polygon polygon = new Polygon();
		for (int i = 0; i < points.length; i++) {
			Vec3 point = points[i];
			point = point.getAsFloatVec4().transformF(last).getAsFloatVec3();
			if (point.z() < 0 || point.z() > 1) {
				return;
			}
			polygon.addPoint((int) (point.x() * this.w) + this.w2, (int) (-point.y() * this.h) + this.h2);
		}
		g.fillPolygon(polygon);
	}

	private void drawLine(Graphics2D g, MatrixStack stack, Vec3 a, Vec3 b) {
		Matrix4 last = stack.getLast();
		a = a.getAsFloatVec4().transformF(last).getAsFloatVec3();
		b = b.getAsFloatVec4().transformF(last).getAsFloatVec3();
		if ((a.z() < 0 && b.z() < 0) || a.z() > 1 || b.z() > 1) {
			return;
		}
		g.drawLine((int) (a.x() * this.w) + this.w2, (int) (-a.y() * this.h) + this.h2, (int) (b.x() * this.w) + this.w2, (int) (-b.y() * this.h) + this.h2);
	}

	private void drawPoint(Graphics2D g, MatrixStack stack, Vec3 point, float size) {
		point = point.getAsFloatVec4().transformF(stack.getLast()).getAsFloatVec3();
		if (point.z() < 0 || point.z() > 1) {
			return;
		}

		int s = (int) (100 * size * (1 - point.z()));
		int s2 = s / 2;

		g.fillOval((int) (point.x() * this.w) + this.w2 - s2, (int) -(point.y() * this.h) + this.h2 - s2, s, s);
	}

}
