package net.skds.lib2.awtutils.layouts;

import lombok.Setter;

import java.awt.*;

public class VerticalLayout implements LayoutManager {

	private final int spacer;
	@Setter
	private boolean centered = false;

	public VerticalLayout(int spacer) {
		this.spacer = spacer;
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		Dimension pref = parent.getSize();
		int w = pref.width;
		int h = spacer;
		for (Component component : parent.getComponents()) {
			if (!component.isVisible()) {
				continue;
			}
			Dimension dim = component.getPreferredSize();
			if (dim.width > w) {
				w = dim.width;
			}
			h += spacer + dim.height;
		}
		return new Dimension(w, h);
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		return preferredLayoutSize(parent);
	}

	@Override
	public void layoutContainer(Container parent) {

		Component[] components = parent.getComponents();
		int y = spacer;
		for (Component component : components) {
			if (!component.isVisible()) {
				continue;
			}
			Dimension pref = component.getPreferredSize();
			int x = 0;
			if (centered) {
				x = (parent.getWidth() - pref.width) / 2;
			}
			component.setBounds(x, y, pref.width, pref.height);
			y += spacer + pref.height;
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
