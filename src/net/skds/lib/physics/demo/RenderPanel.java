package net.skds.lib.physics.demo;

import net.skds.lib.collision.Box;
import net.skds.lib.collision.OBB;
import net.skds.lib.mat.Matrix3;
import net.skds.lib.mat.Quat;
import net.skds.lib.mat.Vec3;
import net.skds.lib.mat.graphics.Matrix4f;
import net.skds.lib.mat.graphics.MatrixStack;
import net.skds.lib.mat.graphics.Quatf;
import net.skds.lib.mat.graphics.Vec3f;
import net.skds.lib.physics.PhysicalBody;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RenderPanel extends JPanel {

	private final PhysicsDemo physicsDemo;

	public final Vec3 cameraPos = new Vec3();
	public final Matrix3 lastRot = new Matrix3();

	private float cameraYaw;
	private float cameraPitch;
	private float cameraFov = 70;

	private int lastMouseX;
	private int lastMouseY;

	private int lastMouseB;
	private final Robot robot;

	public RenderPanel(PhysicsDemo physicsDemo) {
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
					physicsDemo.addTask(() -> {
						new PhysicalBody(physicsDemo.space, new Vec3(0, 4, 0), Quat.ONE, new OBB(new Vec3(1, .5, 2))).spawn();
					});

				} else if (physicsDemo.isFocus() && e.getButton() == 3) {
					physicsDemo.addTask(physicsDemo.space::tick);
				}
			}
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

		physicsDemo.space.getBodiesSet().forEach(b -> {

			g.setColor(Color.green);
			Box aabb = b.getMovingBoundingBox();
			OBB box = new OBB(aabb.getCenter(), aabb.dimensions());
			drawBox(g, stack, box);

			drawPoint(g, stack, box.getCenter(), 2);

			box = (OBB) b.getShape();
			g.setColor(Color.BLACK);
			drawBox(g, stack, box);
		});

		stack.close();
	}

	private void drawBox(Graphics2D g, MatrixStack stack, OBB box) {
		var lines = box.getLines();
		stack.push();
		//stack.translate(box.pos);
		stack.mul(box.baseMatrix);
		int w = getWidth();
		int h = getHeight();
		int w2 = w / 2;
		int h2 = h / 2;
		for (int i = 0; i < lines.length; i += 2) {
			Vec3 a = lines[i].transform(stack.getLast());
			Vec3 b = lines[i + 1].transform(stack.getLast());
			if (a.z < 0 || b.z < 0) {
				continue;
			}
			a.y *= -1;
			b.y *= -1;
			g.drawLine((int) (a.x * w) + w2, (int) (a.y * h) + h2, (int) (b.x * w) + w2, (int) (b.y * h) + h2);
		}

		stack.pop();
	}

	private void drawPoint(Graphics2D g, MatrixStack stack, Vec3 point, float size) {
		stack.push();
		//stack.translate(point);
		int w = getWidth();
		int h = getHeight();
		int w2 = w / 2;
		int h2 = h / 2;
		point = point.copy().transform(stack.getLast());

		int s = (int) (100 * size * (1 - point.z));

		g.fillOval((int) (point.x * w) + w2, (int) -(point.y * h) + h2, s, s);

		stack.pop();
	}


	public Matrix4f getMatrix() {
		Matrix4f proj = Matrix4f.perspectiveInfinity(cameraFov, (float) getWidth() / getHeight(), .2f);
		Quatf q = new Quatf(Vec3f.YP, cameraYaw, true).rotate(Vec3f.XP, cameraPitch, true);
		lastRot.set(q);
		Matrix4f m4 = Matrix4f.makeTranslation((float) cameraPos.x, (float) cameraPos.y, (float) cameraPos.z);
		return proj.mul(lastRot.to4f().transpose().mul(m4));
	}
}
