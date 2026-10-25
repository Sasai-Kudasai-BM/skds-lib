package net.skds.lib2.demo;

import net.skds.lib2.mat.FastMath;
import net.skds.lib2.shapes.AABB;
import net.skds.lib2.shapes.FastAABBTree;
import net.skds.lib2.utils.ColorUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class FastAABBTreeDemo extends JFrame {

	JCheckBox checkBox = new JCheckBox("raytrace (TODO)");

	Timer t;

	public FastAABBTreeDemo() {
		setLayout(new FlowLayout());
		add(new Panel());
		add(checkBox);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		setLocation(-getWidth() / 2, -getHeight() / 2);
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				t.stop();
			}
		});
	}

	private enum Mode {
		ADD,
		SELECT,
		REMOVE,
	}

	private class Panel extends JPanel {

		float lastColor = 0f;

		FastAABBTree<Color> tree = new FastAABBTree<>(8);

		int movingNodeId;

		double baseMinX = 200, baseMinY = 300, baseMaxX = 250, baseMaxY = 400;

		Point point;
		Point point2;

		double drawMinX, drawMinY, drawMaxX, drawMaxY;
		boolean hasDrawBox = false;

		Mode mode = Mode.ADD;

		ArrayList<Integer> collisionIdsBuffer = new ArrayList<>();

		Panel() {
			movingNodeId = tree.put(baseMinX, baseMinY, 0.0, baseMaxX, baseMaxY, 0.0, Color.PINK);

			t = new Timer(20, _ -> {
				float s = 200;
				float time = (System.currentTimeMillis() % 180_000L) / 20f;
				double offX = FastMath.cosDegr(time) * s;
				double offY = FastMath.sinDegr(time) * s;
				try {
					tree.move(movingNodeId, baseMinX + offX, baseMinY + offY, 0.0, baseMaxX + offX, baseMaxY + offY, 0.0);
				} catch (IllegalStateException e) {
					e.printStackTrace(System.err);
					movingNodeId = tree.put(baseMinX, baseMinY, 0.0, baseMaxX, baseMaxY, 0.0, Color.PINK);
				}
				repaint();
			});
			t.start();

			setPreferredSize(new Dimension(1000, 600));
			MouseAdapter ma = new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					if (e.getButton() == 1) {
						mode = Mode.ADD;
					} else if (e.getButton() == 3) {
						mode = Mode.SELECT;
					} else {
						mode = Mode.REMOVE;
					}
					hasDrawBox = false;
					point = e.getPoint();
				}

				@Override
				public void mouseDragged(MouseEvent e) {
					updateDrawBox(point, e.getPoint());
					point2 = e.getPoint();
					repaint();
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					if (mode == Mode.ADD) {
						addBox(point, e.getPoint());
					} else if (mode == Mode.REMOVE) {
						if (hasDrawBox) {
							if (checkBox.isSelected()) {
								// TODO
							} else {
								collisionIdsBuffer.clear();
								tree.forEachCollision(drawMinX, drawMinY, 0.0, drawMaxX, drawMaxY, 0.0, -1, (_, id) -> collisionIdsBuffer.add(id));
								for (int id : collisionIdsBuffer) {
									if (id == movingNodeId) {
										movingNodeId = FastAABBTree.NULL;
									}
									tree.remove(id);
								}
							}
						}
					}
					hasDrawBox = false;
					repaint();
				}
			};
			addMouseListener(ma);
			addMouseMotionListener(ma);
			setBackground(Color.LIGHT_GRAY);
		}

		private void addBox(Point p1, Point p2) {
			Color color = createColor();
			lastColor += .27f;

			double minX = Math.min(p1.x, p2.x);
			double minY = Math.min(p1.y, p2.y);
			double maxX = Math.max(p1.x, p2.x);
			double maxY = Math.max(p1.y, p2.y);

			tree.put(minX, minY, 0.0, maxX, maxY, 0.0, color);
		}

		private void updateDrawBox(Point p1, Point p2) {
			drawMinX = Math.min(p1.x, p2.x);
			drawMinY = Math.min(p1.y, p2.y);
			drawMaxX = Math.max(p1.x, p2.x);
			drawMaxY = Math.max(p1.y, p2.y);
			hasDrawBox = true;
		}

		private Color createColor() {
			if (lastColor > 1) {
				lastColor--;
			}
			return new Color(ColorUtils.hueRGB(lastColor));
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			g2d.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));


			tree.forEachNode(id -> {
				if (tree.isInternalNode(id)) {
					AABB aabb = tree.getBounds(id);
					g2d.setColor(Color.DARK_GRAY);
					g.drawRect((int) aabb.minX - 2, (int) aabb.minY - 2, (int) (aabb.maxX - aabb.minX) + 4, (int) (aabb.maxY - aabb.minY) + 4);
				}
			});

			tree.forEachCollision(0, 0, 0, 1000, 600, 1.0, -1, (color, id) -> {
				AABB aabb = tree.getBounds(id);
				g2d.setColor(color);
				g.drawRect((int) aabb.minX, (int) aabb.minY, (int) (aabb.maxX - aabb.minX), (int) (aabb.maxY - aabb.minY));
			});

			if (hasDrawBox) {
				if (mode == Mode.ADD) {
					g2d.setColor(createColor());
					g.drawRect((int) drawMinX, (int) drawMinY, (int) (drawMaxX - drawMinX), (int) (drawMaxY - drawMinY));
				} else {
					if (checkBox.isSelected()) {
						g2d.setColor(Color.BLACK);
						g2d.drawLine(point.x, point.y, point2.x, point2.y);
					} else {
						g2d.setColor(Color.BLUE);
						g.drawRect((int) drawMinX, (int) drawMinY, (int) (drawMaxX - drawMinX), (int) (drawMaxY - drawMinY));

						g2d.setColor(Color.LIGHT_GRAY);
						tree.forEachCollision(drawMinX, drawMinY, 0.0, drawMaxX, drawMaxY, 1.0, -1, (_, id) -> {
							AABB aabb = tree.getBounds(id);
							g.drawRect((int) aabb.minX, (int) aabb.minY, (int) (aabb.maxX - aabb.minX), (int) (aabb.maxY - aabb.minY));
						});
					}
				}
			}
		}
	}
}
