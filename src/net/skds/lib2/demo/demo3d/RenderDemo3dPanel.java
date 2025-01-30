package net.skds.lib2.demo.demo3d;

import lombok.Getter;
import lombok.Setter;
import net.skds.lib2.mat.FastMath;
import net.skds.lib2.mat.MatrixStack;
import net.skds.lib2.mat.matrix3.Matrix3;
import net.skds.lib2.mat.matrix4.Matrix4;
import net.skds.lib2.mat.matrix4.Matrix4F;
import net.skds.lib2.mat.vec3.Vec3;
import net.skds.lib2.mat.vec4.Quat;
import net.skds.lib2.shapes.AABB;
import net.skds.lib2.shapes.CompositeShape;
import net.skds.lib2.shapes.CompositeSuperShape;
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
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class RenderDemo3dPanel extends JPanel {

	private final Demo3dFrame physicsDemo;

	@Setter
	@Getter
	private Vec3 cameraPos = Vec3.ZERO;
	@Getter
	private Matrix3 lastRot = Matrix3.SINGLE;

	private List<Supplier<Shape>> shapes = new ArrayList<>();

	private float cameraYaw;
	private float cameraPitch;
	private float cameraFov = 70;

	private int lastMouseX;
	private int lastMouseY;

	private int lastMouseB;
	private final Robot robot;

	public RenderDemo3dPanel(Demo3dFrame physicsDemo) {
		this.physicsDemo = physicsDemo;
		try {
			this.robot = new Robot();
		} catch (AWTException e) {
			throw new RuntimeException(e);
		}

		addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				mouseMoved(e);
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				int dX = e.getX() - lastMouseX;
				int dY = e.getY() - lastMouseY;

				lastMouseX = e.getX();
				lastMouseY = e.getY();

				if (physicsDemo.isFocus()) {
					cameraYaw -= dX * .2f;
					cameraPitch -= dY * .2f;

					if (cameraPitch > 90) cameraPitch = 90;
					if (cameraPitch < -90) cameraPitch = -90;

					//onMouseMoved();
					repaint();
				}

				//if (lastMouseB == 1) {
				//} else if (lastMouseB == 3) {
				//	cameraPos.add(lastRot.left().scale(dX * .01));
				//	cameraPos.add(lastRot.up().scale(dY * .01));
				//}
			}


		});

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				if (physicsDemo.isFocus()) resetMouse();
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (physicsDemo.isFocus() && e.getButton() == 1) {
					// TADA
					/*physicsDemo.addTask(() -> {
						new PhysicalBody(physicsDemo.space, new Vec3(0, 4, 0), Quat.ONE, new OBB(new Vec3(1, .5, 2))).spawn();
					});*/

				} else if (physicsDemo.isFocus() && e.getButton() == 3) {
					// TADA
					//physicsDemo.addTask(physicsDemo.space::tick);
				}
			}
		});

		shapes.add(new ValueSupplier<>(AABB.fromCenter(0, 0, 0, 1, 1, 1)));
		shapes.add(new ValueSupplier<>(AABB.fromCenter(0, -1, 0, 2, 1, 2)));

		CompositeSuperShape superShape = CompositeSuperShape.of(new Shape[]{
			CompositeSuperShape.of(new Shape[]{
				AABB.fromCenter(Vec3.of(0, 1, 0), Vec3.of(1, 2, .5)),

				CompositeSuperShape.of(new Shape[]{
					AABB.fromCenter(Vec3.of(0, 0.75 / 2, 0), 0.75)
				}, Vec3.ZERO, "head").move(Vec3.of(0, 2, 0))
					.rotate(Quat.fromAxisDegrees(Vec3.ZN, 10))
				,

				CompositeSuperShape.of(new Shape[]{
					AABB.fromCenter(Vec3.of(0, 0, 0), Vec3.of(0.25, 1.75, 0.25))
				}, Vec3.of(0.25 / 2, 1.75 / 2 - 0.1, 0), "hand").move(Vec3.of(-1f / 2 - 0.25 / 2, 1.1, 0)).rotate(Quat.fromAxisDegrees(Vec3.XN, 30))

			}, Vec3.ZERO, "body")
		}, Vec3.ZERO);

		CompositeSuperShape rotated = superShape
				.move(Vec3.of(2, 0, 0))
				.rotate(Matrix3.fromQuat(Quat.fromAxisDegrees(Vec3.XN, 30)))
				;

		shapes.add(() -> rotated.rotate(Quat.fromAxisDegrees(Vec3.YP, (System.currentTimeMillis() / 50d) % 360)));
		shapes.add(new ValueSupplier<>(superShape.move(Vec3.of(4, 0, 0))));

		CompositeSuperShape scaled = superShape.move(Vec3.of(6, 0, 0));

		shapes.add(() -> scaled.scale(FastMath.sinDegr((System.currentTimeMillis() / 50d) % 360) / 2d + .5));

		CompositeSuperShape moveRotScale = superShape.move(Vec3.of(8, 0, 0));

		shapes.add(() -> {
			Quat rot = Quat.fromAxisDegrees(Vec3.YN, (System.currentTimeMillis() / 10d) % 360);
			//Quat rot = Quat.fromAxisDegrees(Vec3.YP, (System.currentTimeMillis() / 10d) % 360);
			return moveRotScale.moveRotScale(Vec3.of(0, 0, FastMath.sinDegr((System.currentTimeMillis() / 50d) % 360)), rot, 1);
		});

		CompositeSuperShape posed = superShape.move(Vec3.of(10, 0, 0));

		shapes.add(() -> {
			Quat rot = Quat.fromAxisDegrees(Vec3.YP, (System.currentTimeMillis() / 10d) % 360);
			return posed.setPose((sh, p, r, s, c) -> {}, Vec3.ZP.scale(0.5).transform(rot), rot, 1);
		});
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

		//FontRenderContext context = new FontRenderContext(null, false, false);
		//GlyphVector gv = g.getFont().createGlyphVector(context, "q");
		//System.out.println(((GeneralPath) gv.getGlyphOutline(0)).getPathIterator());

		MatrixStack stack = new MatrixStack(getMatrix());

		this.shapes.forEach(b -> drawShape(g, stack, b.get(), true));
		g.setColor(Color.BLACK);

		stack.close();
	}

	private void drawShape(Graphics2D g, MatrixStack stack, Shape shape, boolean root) {
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
		//stack.translate(box.pos);
		//stack.mul(obb.normals);
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
		//stack.translate(point);
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
}
