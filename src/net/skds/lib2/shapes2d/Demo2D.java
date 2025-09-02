package net.skds.lib2.shapes2d;

import net.skds.lib2.utils.ColorUtils;
import net.skds.lib2.utils.Holders;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Demo2D extends JFrame {

	public Demo2D() {
		add(new Panel());

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		setLocation(-getWidth() / 2, -getHeight() / 2);
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);
	}

	private enum Mode {
		ADD,
		SELECT,
		REMOVE,
		;
	}

	private static class Panel extends JPanel {

		float lastColor = 0f;

		AABRTree<Color> tree = new AABRTree<>();
		Point point;
		AABR draw;

		Mode mode = Mode.ADD;

		Panel() {
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
					draw = null;
					point = e.getPoint();
				}

				@Override
				public void mouseDragged(MouseEvent e) {
					draw = createBox(point, e.getPoint());
					repaint();
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					if (mode == Mode.ADD) {
						addBox(point, e.getPoint());
					} else if (mode == Mode.REMOVE) {
						AABR aabr = draw;
						if (aabr != null) {
							for (var r : tree.getCollisions(aabr)) {
								r.remove();
							}
						}
					}
					draw = null;
					repaint();
				}
			};
			addMouseListener(ma);
			addMouseMotionListener(ma);
		}

		private void addBox(Point p1, Point p2) {
			Color color = createColor();
			lastColor += .27f;
			tree.put(createBox(p1, p2), color);

			Holders.IntHolder c1 = new Holders.IntHolder();
			Holders.IntHolder c2 = new Holders.IntHolder();

			tree.foreachNode((n) -> {
				if (n.getValue() == null) {
					c1.increment();
				}
			});

			tree.foreach(n -> c2.increment());

			System.out.println(c1.getValue() + "/" + c2.getValue());
		}

		private AABR createBox(Point p1, Point p2) {
			return AABR.fromToNormalized(p1.x, p1.y, p2.x, p2.y);
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

			tree.foreachNode(n -> {
				Color c = n.getValue();
				if (c == null) {
					AABR r = n.getBounding();
					c = Color.DARK_GRAY;
					g2d.setColor(c);
					g.drawRect((int) r.minX - 2, (int) r.minY - 2, (int) r.sizeX() + 4, (int) r.sizeY() + 4);
				}
			});

			tree.foreach(n -> {
				g2d.setColor(n.getValue());
				AABR r = n.getBounding();
				g.drawRect((int) r.minX, (int) r.minY, (int) r.sizeX(), (int) r.sizeY());
			});

			AABR aabr = this.draw;
			if (aabr != null) {
				if (mode == Mode.ADD) {
					g2d.setColor(createColor());
					g.drawRect((int) aabr.minX, (int) aabr.minY, (int) aabr.sizeX(), (int) aabr.sizeY());
				} else {

					g2d.setColor(Color.BLUE);
					g.drawRect((int) aabr.minX, (int) aabr.minY, (int) aabr.sizeX(), (int) aabr.sizeY());
					g2d.setColor(Color.LIGHT_GRAY);
					for (var r : tree.getCollisions(aabr)) {
						AABR rr = r.getBounding();
						g.drawRect((int) rr.minX, (int) rr.minY, (int) rr.sizeX(), (int) rr.sizeY());
					}
				}
			}
		}
	}

}
