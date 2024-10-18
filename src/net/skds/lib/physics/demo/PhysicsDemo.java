package net.skds.lib.physics.demo;

import lombok.Getter;
import net.skds.lib.collision.OBB;
import net.skds.lib.mat.Quat;
import net.skds.lib.mat.Vec3;
import net.skds.lib.physics.PhysicalSpace;
import net.skds.lib.physics.StaticBody;
import net.skds.lib.utils.ThreadUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PhysicsDemo extends JFrame {

	public final RenderPanel renderPanel;
	public final ToolPanel toolPanel;

	public final PhysicalSpace space;

	private final ConcurrentLinkedQueue<Runnable> tasks = new ConcurrentLinkedQueue<>();

	@Getter
	private boolean focus;
	private int forward;
	private int left;
	private int up;

	double speed = .1f;

	public PhysicsDemo() {
		super("PhysicsDemo");
		setPreferredSize(new Dimension(1200, 800));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		this.renderPanel = new RenderPanel(this);
		this.toolPanel = new ToolPanel(this);
		add(this.renderPanel, BorderLayout.CENTER);
		add(this.toolPanel, BorderLayout.EAST);

		this.space = new PhysicalSpace();
		this.space.setTickDuration(0.1);

		BufferedImage cursorImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");

		new StaticBody(space, Vec3.ZERO, Quat.ONE, new OBB(new Vec3(10, .1, 10))).spawn();

		ThreadUtil.runTickableDaemon(() -> {
			PhysicsDemo.this.tick();
			Runnable r;
			while ((r = tasks.poll()) != null) {
				r.run();
			}
			//if (t++ % 50 == 0) {
			//space.tick();
			//}
			renderPanel.repaint();
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

		pack();
	}

	public void addTask(Runnable task) {
		tasks.offer(task);
	}

	private void tick() {
		if (focus && (up != 0 || left != 0 || forward != 0)) {
			if (up != 0) {
				renderPanel.cameraPos.add(renderPanel.lastRot.up().scale(-speed * up));
			}
			if (left != 0) {
				renderPanel.cameraPos.add(renderPanel.lastRot.left().scale(speed * left));
			}
			if (forward != 0) {
				renderPanel.cameraPos.add(renderPanel.lastRot.forward().scale(speed * forward));
			}
			renderPanel.repaint();
		}
	}

	public static void main(String[] args) throws AWTException {
		new PhysicsDemo().setVisible(true);
	}
}



