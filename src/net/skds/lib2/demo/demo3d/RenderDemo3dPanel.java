package net.skds.lib2.demo.demo3d;

import net.skds.lib2.mat.MatrixStack;
import net.skds.lib2.mat.matrix3.Matrix3;
import net.skds.lib2.mat.matrix4.Matrix4;
import net.skds.lib2.mat.matrix4.Matrix4F;
import net.skds.lib2.mat.vec3.Vec3;
import net.skds.lib2.mat.vec4.Quat;
import net.skds.lib2.mat.vec4.Vec4F;
import net.skds.lib2.shapes.AABB;
import net.skds.lib2.shapes.ConvexShape;
import net.skds.lib2.shapes.OBB;
import net.skds.lib2.utils.linkiges.Pair;

import javax.swing.*;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class RenderDemo3dPanel extends JPanel {

	private final Demo3dFrame physicsDemo;

	@Setter
	@Getter
	private Vec3 cameraPos = Vec3.ZERO;
	@Getter
	private Matrix3 lastRot = Matrix3.SINGLE;

	private List<ConvexShape> shapes = new ArrayList<>();

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

		shapes.add(AABB.fromCenter(Vec3.ZERO, 1));
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

		this.shapes.forEach(b -> {
			g.setColor(Color.green);

			drawPoint(g, stack, b.getCenter(), 2);

			g.setColor(Color.BLACK);
			drawBox(g, stack, b);
		});

		stack.close();
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
			if (a.z() < 0 || b.z() < 0) {
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

		int s = (int) (100 * size * (1 - point.z()));

		g.fillOval((int) (point.x() * w) + w2, (int) -(point.y() * h) + h2, s, s);
	}

	public Matrix4F getMatrix() {
		Matrix4F proj = Matrix4.perspectiveInfinityF(cameraFov, (float) getWidth() / getHeight(), .2f);
		Quat q = Quat.fromAxisDegrees(Vec3.YP, cameraYaw).rotateAxisDegrees(Vec3.XP, cameraPitch);
		this.lastRot = Matrix3.fromQuat(q);
		return proj.multiplyF(Matrix4.fromMatrix3F(this.lastRot).transpose().translate(cameraPos.x(), cameraPos.y(), cameraPos.z()));
	}
}
