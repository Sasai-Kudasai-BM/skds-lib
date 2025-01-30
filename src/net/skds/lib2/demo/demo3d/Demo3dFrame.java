package net.skds.lib2.demo.demo3d;

import lombok.Getter;
import net.skds.lib2.utils.ThreadUtils;

import javax.swing.*;
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

		setMinimumSize(new Dimension(300, 50));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		setLocation(-getWidth() / 2, -getHeight() / 2);
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);
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
			renderPanel.repaint();
		}
	}

	public static void main(String[] args) throws AWTException {
		new Demo3dFrame().setVisible(true);
	}
}



