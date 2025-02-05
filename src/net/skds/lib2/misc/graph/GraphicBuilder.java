package net.skds.lib2.misc.graph;

import javax.swing.*;

public class GraphicBuilder extends JFrame {

	public GraphicBuilder() {
		super("GraphicBuilder");


		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		setLocation(-getWidth() / 2, -getHeight() / 2);
		setLocationRelativeTo(null);
		setVisible(true);
	}
}
