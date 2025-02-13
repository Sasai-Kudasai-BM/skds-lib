package net.skds.lib2.demo.demo3d;

import javax.swing.*;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.w3e.lib.awtutils.SwingKeyListener;
import net.skds.lib2.mat.FastMath;
import net.skds.lib2.mat.matrix3.Matrix3;
import net.skds.lib2.mat.matrix4.Matrix4;
import net.skds.lib2.mat.matrix4.Matrix4F;
import net.skds.lib2.mat.vec3.Vec3;
import net.skds.lib2.mat.vec4.Quat;
import net.skds.lib2.utils.ThreadUtils;
import net.w3e.lib.utils.RobotUtils;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Demo3dFrame extends JFrame implements KeyListener, MouseMotionListener, MouseListener, MouseWheelListener, Demo3dShapeCollector {

	private static final Cursor BLANK_CURSOR;
	
	static {
		BufferedImage cursorImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		BLANK_CURSOR = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
	}

	public final RenderDemo3dPanel renderPanel;
	public final Demo3dToolPanel toolPanel;

	private final ConcurrentLinkedQueue<Runnable> tasks = new ConcurrentLinkedQueue<>();

	@Getter
	private boolean focus;

	private int forward;
	private int left;
	private int up;
	private boolean pressShift;

	@Setter
	@Getter
	private Vec3 cameraPos = Vec3.ZERO;
	@Setter
	@Getter
	private float cameraYaw;
	@Setter
	@Getter
	private float cameraPitch;
	@Getter
	private Matrix3 lastRot = Matrix3.SINGLE;

	@Getter
	private float cameraFov = 70;

	private int lastMouseX;
	private int lastMouseY;

	//private int lastMouseB;

	@Getter
	private double speed = .1f;

	@Setter(value = AccessLevel.PACKAGE)
	@Getter
	private Demo3dShape hovered;

	@Setter(value = AccessLevel.PACKAGE)
	@Getter
	private boolean fill;

	@Setter(value = AccessLevel.PACKAGE)
	@Getter
	private boolean sort;

	private final List<SwingKeyListener> keys = new ArrayList<>();

	final List<Demo3dShape> shapes = new ArrayList<>();

	public Demo3dFrame() {
		super("Demo3d");
		setPreferredSize(new Dimension(1200, 800));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		this.renderPanel = new RenderDemo3dPanel(this);
		this.toolPanel = new Demo3dToolPanel(this);
		add(this.renderPanel, BorderLayout.CENTER);
		add(this.toolPanel, BorderLayout.EAST);

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

		JComponent root = ((JComponent)this.getContentPane());
		new SwingKeyListener(root, KeyEvent.VK_ESCAPE, this).install();

		this.renderPanel.addKeyListener(this);
		addMouseMotionListener(this);
		addMouseListener(this);
		addMouseWheelListener(this);

		setMinimumSize(new Dimension(300, 50));
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		setLocation(-getWidth() / 2, -getHeight() / 2);
		setLocationRelativeTo(null);
		//setResizable(false);
		setVisible(true);
	}

	/*@Override
	protected JRootPane createRootPane() {
		JRootPane rootPane = new JRootPane() {
			@Override
			protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed) {
				if (e.getKeyCode() == KeyEvent.VK_TAB) {
					return super.processKeyBinding(ks, e, condition, pressed);
				}
				return true;
			}
		};
		rootPane.setOpaque(true);

		return rootPane;
	}*/

	@Override
	public final void addShape(Demo3dShape shape) {
		this.shapes.add(shape);
	}

	@Override
	public final void clear() {
		this.shapes.clear();
	}

	public final void addTask(Runnable task) {
		this.tasks.offer(task);
	}

	private void tick() {
		if (focus && (up != 0 || left != 0 || forward != 0)) {
			if (up != 0) {
				this.setCameraPos(this.cameraPos.add(this.lastRot.up().scale(speed * up)));
			}
			if (left != 0) {
				this.setCameraPos(this.cameraPos.add(this.lastRot.left().scale(-speed * left)));
			}
			if (forward != 0) {
				this.setCameraPos(this.cameraPos.add(this.lastRot.forward().scale(-speed * forward)));
			}
			this.renderPanel.repaint();
			this.toolPanel.repaint();
			this.renderPanel.requestFocus();
		}
	}

	public final void resetMouse() {
		Point p = getLocationOnScreen();
		lastMouseX = getWidth() / 2;
		lastMouseY = getHeight() / 2;
		RobotUtils.mouseMove(p.x + lastMouseX, p.y + lastMouseY);
	}

	public final Matrix4F getMatrix() {
		Matrix4F proj = Matrix4.perspectiveInfinityF(this.cameraFov, (float)this.renderPanel.getWidth() / this.renderPanel.getHeight(), .2f);
		Quat q = Quat.fromAxisDegrees(Vec3.YP, this.cameraYaw + 180).rotateAxisDegrees(Vec3.XP, cameraPitch);
		this.lastRot = Matrix3.fromQuat(q);

		return proj.multiplyF(Matrix4.fromMatrix3F(this.lastRot).transposeF().translateF(-this.cameraPos.xf(), -this.cameraPos.yf(), -this.cameraPos.zf()));
	}

	@Override
	public final void keyTyped(KeyEvent e) {
		if (!this.isFocused()) {
			return;
		}
	}

	@Override
	public final void keyPressed(KeyEvent e) {
		if (!this.isFocused() && !this.focus) {
			return;
		}
		switch (e.getKeyCode()) {
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
			case KeyEvent.VK_SHIFT -> {
				this.pressShift = true;
			}
		}
	}

	@Override
	public final void keyReleased(KeyEvent e) {
		if (!this.isFocused()) {
			return;
		}
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			if (!focus) {
				this.keys.forEach(SwingKeyListener::install);
				this.renderPanel.setFocusable(true);
				this.renderPanel.requestFocus();
				setCursor(BLANK_CURSOR);
				resetMouse();
			} else {
				this.renderPanel.setFocusable(false);
				this.keys.forEach(SwingKeyListener::uninstall);
				setCursor(null);
			}
			focus = !focus;
			e.consume();
			return;
		}
		if (!this.focus) {
			return;
		}
		e.consume();
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
			case KeyEvent.VK_SHIFT -> {
				this.pressShift = false;
			}
		}
	}

	@Override
	public final void mouseDragged(MouseEvent e) {
		mouseMoved(e);
	}

	@Override
	public final void mouseMoved(MouseEvent e) {
		if (!this.isFocus()) {
			return;
		}
		int dX = e.getX() - this.lastMouseX;
		int dY = e.getY() - this.lastMouseY;

		this.lastMouseX = e.getX();
		this.lastMouseY = e.getY();

		if (this.isFocus()) {
			this.cameraYaw -= dX / 10f;
			this.cameraPitch -= dY / 10f;

			if (this.cameraPitch > 90) this.cameraPitch = 90;
			if (this.cameraPitch < -90) this.cameraPitch = -90;

			//onMouseMoved();
			this.renderPanel.repaint();
			this.toolPanel.repaint();
		}
		this.resetMouse();
	}

	@Override
	public final void mouseClicked(MouseEvent e) {
		if (this.isFocus()) {
			if (this.hovered != null) {
				this.hovered.mouseClicked(e);
			}
		}
	}

	@Override
	public final void mousePressed(MouseEvent e) {
		if (this.isFocus()) {
			if (this.hovered != null) {
				this.hovered.mousePressed(e);
			}
		}
	}

	@Override
	public final void mouseReleased(MouseEvent e) {
		if (this.isFocus()) {
			if (this.hovered != null) {
				this.hovered.mouseReleased(e);
			}
		}
	}

	@Override
	public final void mouseEntered(MouseEvent e) {}

	@Override
	public final void mouseExited(MouseEvent e) {
		if (this.isFocus()) {
			this.resetMouse();
		}
	}

	@Override
	public final void mouseWheelMoved(MouseWheelEvent e) {
		if (!this.isFocus()) {
			return;
		}
		if (this.pressShift) {
			float clamp = FastMath.clamp(this.cameraFov + e.getWheelRotation(), 30, 120);
			if (clamp != this.speed) {
				this.cameraFov = clamp;
				this.toolPanel.repaint();
			}
		} else {
			double clamp = FastMath.clamp(this.speed + e.getWheelRotation() / -100d, 0.01, 0.5);
			if (clamp != this.speed) {
				this.speed = clamp;
				this.toolPanel.repaint();
			}
		}
	}

	public static void main(String[] args) throws AWTException {
		new Demo3dFrame();
	}
}



