package net.skds.lib2.awtutils.layouts;

import lombok.Setter;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class HorizontalLayout implements LayoutManager {

	private final int spacer;
	@Setter
	private boolean centered = false;

	public HorizontalLayout() {
		this.spacer = 0;
	}

	public HorizontalLayout(int spacer, boolean centered) {
		this.spacer = spacer;
		this.centered = centered;
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
		for (Component component : components) {
			if (!component.isVisible()) {
				continue;
			}
			Dimension pref = component.getPreferredSize();
			int x2 = 0;
			int y = 0;

			if (border != null) {
				Insets insets = border.getBorderInsets(component);
				y += insets.top;
				x += insets.left;
				x2 = insets.right;
			}

			if (centered) {
				y += (parent.getHeight() - pref.height) / 2;
			}
			component.setBounds(x, y, pref.width, pref.height);
			x += spacer + pref.width + x2;
		}
		Dimension size = preferredLayoutSize(parent);
		parent.setPreferredSize(size);
		//parent.setSize(size);
	}

	@Override
	public void addLayoutComponent(String name, Component comp) {
	}

	@Override
	public void removeLayoutComponent(Component comp) {
	}

}
