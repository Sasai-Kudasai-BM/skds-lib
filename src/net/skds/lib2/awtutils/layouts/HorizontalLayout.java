package net.skds.lib2.awtutils.layouts;

import java.awt.*;

import javax.swing.JComponent;
import javax.swing.border.Border;

public class HorizontalLayout implements LayoutManager {

	private final int spacer;
	private final LayoutMode mode;

	public HorizontalLayout() {
		this.spacer = 0;
		this.mode = LayoutMode.NONE;
	}

	public HorizontalLayout(int spacer, LayoutMode mode) {
		this.spacer = spacer;
		this.mode = mode;
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		Border border = null;
		if (parent instanceof JComponent jc) {
			border = jc.getBorder();
		}
		Dimension pref = parent.getSize();
		int h = pref.height;
		int w = spacer;
		for (Component component : parent.getComponents()) {
			if (!component.isVisible()) {
				continue;
			}
			Dimension dim = component.getPreferredSize();
			int ch = dim.height;
			int cw = dim.width;
			if (border != null) {
				Insets insets = border.getBorderInsets(component);
				ch += insets.bottom + insets.top;
				cw += insets.left + insets.right;
			}
			if (ch > h) {
				h = ch;
			}
			w += spacer + cw;
		}
		if (w < pref.width) {
			w = pref.width;
		}
		return new Dimension(w, h);
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		return preferredLayoutSize(parent);
	}

	@Override
	public void layoutContainer(Container parent) {
		Border border = null;
		if (parent instanceof JComponent jc) {
			border = jc.getBorder();
		}
		Component[] components = parent.getComponents();
		int x = spacer;

		int max = 0;
		if (this.mode == LayoutMode.FILL) {
			max = preferredLayoutSize(parent).width;
		}

		for (Component component : components) {
			if (!component.isVisible()) {
				continue;
			}
			Dimension pref = component.getPreferredSize();
			int x2 = 0;
			int y2 = 0;

			if (border != null) {
				Insets insets = border.getBorderInsets(component);
				y2 += insets.top;
				x += insets.left;
				x2 = insets.right;
			}

			if (mode == LayoutMode.CENTER) {
				y2 += (parent.getHeight() - pref.height) / 2;
			}
			component.setBounds(x, y2, pref.width, Math.max(pref.height, max));
			x += spacer + pref.width + x2;
		}
		Dimension size = preferredLayoutSize(parent);
		parent.setPreferredSize(size);
		parent.repaint();
	}

	@Override
	public void addLayoutComponent(String name, Component comp) {
	}

	@Override
	public void removeLayoutComponent(Component comp) {
	}

}
