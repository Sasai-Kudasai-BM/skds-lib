package net.skds.lib2.awtutils.layouts;

import java.awt.*;

import javax.swing.JComponent;
import javax.swing.border.Border;

public class VerticalLayout implements LayoutManager {

	private final int spacer;
	private final LayoutMode mode;

	public VerticalLayout() {
		this.spacer = 0;
		mode = LayoutMode.NONE;
	}

	public VerticalLayout(int spacer, LayoutMode mode) {
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
		int w = pref.width;
		int h = spacer;
		for (Component component : parent.getComponents()) {
			if (!component.isVisible()) {
				continue;
			}
			Dimension dim = component.getPreferredSize();
			int cw = dim.width;
			int ch = dim.height;
			if (border != null) {
				Insets insets = border.getBorderInsets(component);
				cw += insets.left + insets.right;
				ch += insets.bottom + insets.top;
			}
			if (cw > w) {
				w = cw;
			}
			h += spacer + ch;
		}
		if (h < pref.height) {
			h = pref.height;
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
		int y = spacer;

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
				y2 += insets.bottom;
				y += insets.top;
				x2 = insets.left;
			}

			if (mode == LayoutMode.CENTER) {
				x2 += (parent.getWidth() - pref.width) / 2;
			}
			component.setBounds(x2, y, Math.max(pref.width, max), pref.height);
			y += spacer + pref.height + y2;
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
