package net.skds.lib2.demo.demo3d;

import javax.swing.*;

import lombok.Getter;
import net.skds.lib2.mat.FastMath;
import net.skds.lib2.mat.matrix3.Matrix3;
import net.skds.lib2.mat.vec3.Vec3;
import net.skds.lib2.mat.vec4.Quat;
import net.skds.lib2.shapes.AABB;
import net.skds.lib2.shapes.CompositeSuperShape;
import net.skds.lib2.shapes.Shape;
import net.skds.lib2.utils.ThreadUtils;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Demo3dFrame extends JFrame {

	public final RenderDemo3dPanel renderPanel;
	public final ToolDemo3dPanel toolPanel;

	private final ConcurrentLinkedQueue<Runnable> tasks = new ConcurrentLinkedQueue<>();

	@Getter
	private boolean focus;
	private int forward;
	private int left;
	private int up;

	double speed = .1f;

	public Demo3dFrame() {
		super("Demo3d");
		setPreferredSize(new Dimension(1200, 800));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		this.renderPanel = new RenderDemo3dPanel(this);
		this.toolPanel = new ToolDemo3dPanel(this);
		add(this.renderPanel, BorderLayout.CENTER);
		add(this.toolPanel, BorderLayout.EAST);

		BufferedImage cursorImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");

		ThreadUtils.runTickableDaemon(() -> {
			Demo3dFrame.this.tick();
			Runnable r;
			while ((r = tasks.poll()) != null) {
				r.run();
			}
			//if (t++ % 50 == 0) {
			//space.tick();
			//}
			this.renderPanel.repaint();
			return true;
		}, "tick", 10);

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
					case KeyEvent.VK_ESCAPE -> {
						focus = !focus;
						if (focus) {
							setCursor(blankCursor);
							renderPanel.resetMouse();
						} else {
							setCursor(null);
						}
					}
					case KeyEvent.VK_W -> {
						if (forward < 1) forward++;
					}
					case KeyEvent.VK_S -> {
						if (forward > -1) forward--;
					}
					case KeyEvent.VK_A -> {
						if (left < 1) left++;
					}
					case KeyEvent.VK_D -> {
						if (left > -1) left--;
					}
					case KeyEvent.VK_SPACE -> {
						if (up < 1) up++;
					}
					case KeyEvent.VK_CONTROL -> {
						if (up > -1) up--;
					}
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				switch (e.getKeyCode()) {
					case KeyEvent.VK_S -> {
						if (forward < 1) forward++;
					}
					case KeyEvent.VK_W -> {
						if (forward > -1) forward--;
					}
					case KeyEvent.VK_D -> {
						if (left < 1) left++;
					}
					case KeyEvent.VK_A -> {
						if (left > -1) left--;
					}
					case KeyEvent.VK_CONTROL -> {
						if (up < 1) up++;
					}
					case KeyEvent.VK_SPACE -> {
						if (up > -1) up--;
					}
				}
			}
		});

		setMinimumSize(new Dimension(300, 50));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		setLocation(-getWidth() / 2, -getHeight() / 2);
		setLocationRelativeTo(null);
		//setResizable(false);
		setVisible(true);
	}

	public Demo3dFrame initDefault() {
		this.renderPanel.addShape(AABB.fromCenter(0, 0, 0, 1, 1, 1));
		this.renderPanel.addShape(AABB.fromCenter(0, -1, 0, 2, 1, 2));

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

		this.renderPanel.addShape(() -> rotated.rotate(Quat.fromAxisDegrees(Vec3.YP, (System.currentTimeMillis() / 50d) % 360)));
		this.renderPanel.addShape(superShape.move(Vec3.of(4, 0, 0)));

		CompositeSuperShape scaled = superShape.move(Vec3.of(6, 0, 0));

		this.renderPanel.addShape(() -> scaled.scale(FastMath.sinDegr((System.currentTimeMillis() / 50d) % 360) / 2d + .5));

		CompositeSuperShape moveRotScale = superShape.move(Vec3.of(8, 0, 0));

		this.renderPanel.addShape(() -> {
			Quat rot = Quat.fromAxisDegrees(Vec3.YN, (System.currentTimeMillis() / 10d) % 360);
			return moveRotScale.moveRotScale(Vec3.of(0, 0, FastMath.sinDegr((System.currentTimeMillis() / 50d) % 360)), rot, 1);
		});

		CompositeSuperShape posed1 = superShape.move(Vec3.of(10, 0, 0));

		this.renderPanel.addShape(() -> {
			Quat rot = Quat.fromAxisDegrees(Vec3.YP, (System.currentTimeMillis() / 10d) % 360);
			return posed1.setPose((sh, p, r, s, c) -> {}, Vec3.ZP.scale(0.5).transform(rot), rot, 1);
		});

		CompositeSuperShape posed2 = superShape.move(Vec3.of(12, 0, 0));

		this.renderPanel.addShape(() -> {
			return posed2.setPose((sh, p, r, s, c) -> {
				Object attachment = sh.getAttachment();
				if (attachment != null) {
					if (attachment.equals("head")) {
						c.setRot(Quat.fromAxisDegrees(Vec3.XP, 50));
					}
				}
			}, Vec3.ZERO, Quat.fromAxisDegrees(Vec3.YP, (System.currentTimeMillis() / 100d) % 360), 1);
		});

		return this;
	}

	public void addTask(Runnable task) {
		tasks.offer(task);
	}

	private void tick() {
		if (focus && (up != 0 || left != 0 || forward != 0)) {
			if (up != 0) {
				renderPanel.setCameraPos(renderPanel.getCameraPos().add(renderPanel.getLastRot().up().scale(-speed * up)));
			}
			if (left != 0) {
				renderPanel.setCameraPos(renderPanel.getCameraPos().add(renderPanel.getLastRot().left().scale(speed * left)));
			}
			if (forward != 0) {
				renderPanel.setCameraPos(renderPanel.getCameraPos().add(renderPanel.getLastRot().forward().scale(speed * forward)));
			}
			this.renderPanel.repaint();
			this.toolPanel.repaint();
		}
	}

	public static void main(String[] args) throws AWTException {
		new Demo3dFrame().setVisible(true);
	}
}



