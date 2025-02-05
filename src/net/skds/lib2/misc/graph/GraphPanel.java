package net.skds.lib2.misc.graph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.LinkedList;
import java.util.List;

public class GraphPanel extends JPanel {

	private final List<DrawableFunction> functions = new LinkedList<>();

	private final GraphStyle style = new GraphStyle();

	public GraphPanel() {
		MouseAdapter mouseAdapter = new MouseAdapter() {

			int lmX = 0;
			int lmY = 0;

			@Override
			public void mousePressed(MouseEvent e) {
				lmX = e.getX();
				lmY = e.getY();
			}

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				super.mouseWheelMoved(e);
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				super.mouseDragged(e);
			}
		};
		addMouseListener(mouseAdapter);
		addMouseMotionListener(mouseAdapter);
		addMouseWheelListener(mouseAdapter);
	}

	public void addFunction(DrawableFunction function) {
		functions.add(function);
		repaint();
	}

	public void removeFunction(DrawableFunction function) {
		functions.remove(function);
		repaint();
	}

	public void clear() {
		functions.clear();
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		drawGrid(g2d);
		drawGraphs(g2d, this.functions);
	}

	protected void drawGrid(Graphics2D g) {

	}

	protected void drawGraphs(Graphics2D g, List<DrawableFunction> functions) {

	}
}
